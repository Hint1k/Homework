package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Report;

import java.util.Optional;

import com.demo.finance.out.service.ReportService;

public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    public Optional<Report> generateReport(String userId) {
        return reportService.generateUserReport(userId);
    }
}
