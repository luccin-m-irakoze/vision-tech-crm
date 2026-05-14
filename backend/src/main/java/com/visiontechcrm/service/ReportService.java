package com.visiontechcrm.service;

import com.visiontechcrm.dto.ReportSummaryDto;
import com.visiontechcrm.model.Lead;
import com.visiontechcrm.model.LeadStage;
import com.visiontechcrm.model.SupportTicket;
import com.visiontechcrm.model.TicketStatus;
import com.visiontechcrm.repository.CustomerRepository;
import com.visiontechcrm.repository.LeadRepository;
import com.visiontechcrm.repository.SupportTicketRepository;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides aggregated statistics for the dashboard and reports page.
 *
 * <p>Read-only by design: it never mutates business data, only summarises it.
 */
@Service
@Transactional(readOnly = true)
public class ReportService {

    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final CustomerRepository customerRepository;
    private final LeadRepository leadRepository;
    private final SupportTicketRepository ticketRepository;

    public ReportService(
            CustomerRepository customerRepository,
            LeadRepository leadRepository,
            SupportTicketRepository ticketRepository) {
        this.customerRepository = customerRepository;
        this.leadRepository = leadRepository;
        this.ticketRepository = ticketRepository;
    }

    public ReportSummaryDto buildSummary() {
        long totalCustomers = customerRepository.count();
        long closedWon = leadRepository.countByStage(LeadStage.CLOSED_WON);

        Map<String, Long> leadsByStage = new EnumMap<>(LeadStage.class).keySet().isEmpty()
                ? new LinkedHashMap<>()
                : new LinkedHashMap<>();
        for (LeadStage s : LeadStage.values()) {
            leadsByStage.put(s.name(), leadRepository.countByStage(s));
        }

        long openLeads =
                leadsByStage.getOrDefault(LeadStage.NEW.name(), 0L)
                        + leadsByStage.getOrDefault(LeadStage.CONTACTED.name(), 0L)
                        + leadsByStage.getOrDefault(LeadStage.QUALIFIED.name(), 0L)
                        + leadsByStage.getOrDefault(LeadStage.PROPOSAL.name(), 0L);

        Map<String, Long> ticketsByStatus = new LinkedHashMap<>();
        for (TicketStatus s : TicketStatus.values()) {
            ticketsByStatus.put(s.name(), ticketRepository.countByStatus(s));
        }
        long openTickets =
                ticketsByStatus.getOrDefault(TicketStatus.OPEN.name(), 0L)
                        + ticketsByStatus.getOrDefault(TicketStatus.IN_PROGRESS.name(), 0L)
                        + ticketsByStatus.getOrDefault(TicketStatus.ESCALATED.name(), 0L);

        return ReportSummaryDto.builder()
                .totalCustomers(totalCustomers)
                .openLeads(openLeads)
                .openTickets(openTickets)
                .closedWonLeads(closedWon)
                .leadsByStage(leadsByStage)
                .ticketsByStatus(ticketsByStatus)
                .monthlyLeads(computeMonthlyLeads())
                .build();
    }

    /** Aggregates lead counts by year-month over the last 6 months. */
    private List<ReportSummaryDto.MonthlyCount> computeMonthlyLeads() {
        Map<String, Long> grouped = new TreeMap<>();
        for (Lead l : leadRepository.findAll()) {
            String key = l.getCreatedAt() == null ? "unknown" : l.getCreatedAt().format(MONTH_FMT);
            grouped.merge(key, 1L, Long::sum);
        }
        return grouped.entrySet().stream()
                .map(e -> new ReportSummaryDto.MonthlyCount(e.getKey(), e.getValue()))
                .toList();
    }

    /** Helper kept for potential SupportTicket SLA reports. */
    @SuppressWarnings("unused")
    private long countResolved(List<SupportTicket> tickets) {
        return tickets.stream().filter(t -> t.getStatus() == TicketStatus.RESOLVED).count();
    }
}
