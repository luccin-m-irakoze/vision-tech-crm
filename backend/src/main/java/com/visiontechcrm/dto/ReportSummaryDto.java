package com.visiontechcrm.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Aggregated counts shown on the dashboard and reports page. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummaryDto {

    private long totalCustomers;
    private long openLeads;
    private long openTickets;
    private long closedWonLeads;

    private Map<String, Long> leadsByStage;
    private Map<String, Long> ticketsByStatus;
    private List<MonthlyCount> monthlyLeads;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyCount {
        private String month;
        private long count;
    }
}
