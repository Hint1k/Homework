package com.demo.finance.out.service;

import com.demo.finance.domain.model.Report;
import com.demo.finance.domain.usecase.ReportUseCase;

import java.util.Optional;
import java.time.LocalDate;
import java.util.Map;

public class ReportService {
    private final ReportUseCase reportUseCase;

    public ReportService(ReportUseCase reportUseCase) {
        this.reportUseCase = reportUseCase;
    }

    public Optional<Report> generateUserReport(Long userId) {
        return reportUseCase.generateUserReport(userId);
    }

    public Optional<Report> generateReportByDate(Long userId, LocalDate from, LocalDate to) {
        return reportUseCase.generateReportByDate(userId, from, to);
    }

    public Map<String, Double> analyzeExpensesByCategory(Long userId, LocalDate from, LocalDate to) {
        return reportUseCase.analyzeExpensesByCategory(userId, from, to);
    }
}