package com.visiontechcrm.service;

import com.visiontechcrm.dto.TicketDto;
import com.visiontechcrm.exception.ResourceNotFoundException;
import com.visiontechcrm.mapper.TicketMapper;
import com.visiontechcrm.model.Priority;
import com.visiontechcrm.model.SupportTicket;
import com.visiontechcrm.model.TicketStatus;
import com.visiontechcrm.repository.CustomerRepository;
import com.visiontechcrm.repository.SupportTicketRepository;
import com.visiontechcrm.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Manages the lifecycle of customer support tickets. */
@Service
@Transactional
public class TicketService {

    private final SupportTicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;

    public TicketService(
            SupportTicketRepository ticketRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository,
            TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.ticketMapper = ticketMapper;
    }

    @Transactional(readOnly = true)
    public List<TicketDto> findAll() {
        return ticketRepository.findAll().stream().map(ticketMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public TicketDto findById(Long id) {
        return ticketMapper.toDto(loadOrThrow(id));
    }

    public TicketDto create(TicketDto dto) {
        SupportTicket ticket =
                SupportTicket.builder()
                        .customer(
                                customerRepository
                                        .findById(dto.getCustomerId())
                                        .orElseThrow(
                                                () ->
                                                        new ResourceNotFoundException(
                                                                "Customer", dto.getCustomerId())))
                        .assignee(
                                dto.getAssigneeId() == null
                                        ? null
                                        : userRepository.findById(dto.getAssigneeId()).orElse(null))
                        .subject(dto.getSubject())
                        .description(dto.getDescription())
                        .priority(dto.getPriority() == null ? Priority.MEDIUM : dto.getPriority())
                        .status(TicketStatus.OPEN)
                        .build();
        return ticketMapper.toDto(ticketRepository.save(ticket));
    }

    public TicketDto update(Long id, TicketDto dto) {
        SupportTicket ticket = loadOrThrow(id);
        ticket.setSubject(dto.getSubject());
        ticket.setDescription(dto.getDescription());
        if (dto.getPriority() != null) {
            ticket.setPriority(dto.getPriority());
        }
        if (dto.getStatus() != null) {
            ticket.setStatus(dto.getStatus());
            if (dto.getStatus() == TicketStatus.RESOLVED && ticket.getResolvedAt() == null) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
        }
        if (dto.getAssigneeId() != null) {
            userRepository.findById(dto.getAssigneeId()).ifPresent(ticket::setAssignee);
        }
        return ticketMapper.toDto(ticketRepository.save(ticket));
    }

    /** Marks a ticket as ESCALATED so L2 engineers see it. */
    public TicketDto escalate(Long id) {
        SupportTicket ticket = loadOrThrow(id);
        ticket.setStatus(TicketStatus.ESCALATED);
        if (ticket.getPriority() == Priority.LOW || ticket.getPriority() == Priority.MEDIUM) {
            ticket.setPriority(Priority.HIGH);
        }
        return ticketMapper.toDto(ticketRepository.save(ticket));
    }

    /** Closes a ticket and stamps the resolution time. */
    public TicketDto resolve(Long id) {
        SupportTicket ticket = loadOrThrow(id);
        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setResolvedAt(LocalDateTime.now());
        return ticketMapper.toDto(ticketRepository.save(ticket));
    }

    public void delete(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new ResourceNotFoundException("SupportTicket", id);
        }
        ticketRepository.deleteById(id);
    }

    private SupportTicket loadOrThrow(Long id) {
        return ticketRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", id));
    }
}
