package com.visiontechcrm.controller;

import com.visiontechcrm.dto.ReportSummaryDto;
import com.visiontechcrm.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ReportSummaryDto> summary() {
        return ResponseEntity.ok(reportService.buildSummary());
    }
}
