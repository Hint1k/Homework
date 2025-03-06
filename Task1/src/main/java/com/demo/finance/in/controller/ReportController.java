package com.demo.finance.in.controller;

import com.demo.finance.out.service.ReportService;
import com.demo.finance.domain.model.Report;

import java.util.Optional;
import java.time.LocalDate;
import java.util.Map;

public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    public Optional<Report> generateReport(Long userId) {
        return reportService.generateUserReport(userId);
    }

    public Optional<Report> generateReportByDate(Long userId, String fromDate, String toDate) {
        LocalDate from = LocalDate.parse(fromDate);
        LocalDate to = LocalDate.parse(toDate);
        return reportService.generateReportByDate(userId, from, to);
    }

    public Map<String, Double> analyzeExpensesByCategory(Long userId, String fromDate, String toDate) {
        LocalDate from = LocalDate.parse(fromDate);
        LocalDate to = LocalDate.parse(toDate);
        return reportService.analyzeExpensesByCategory(userId, from, to);
    }
}