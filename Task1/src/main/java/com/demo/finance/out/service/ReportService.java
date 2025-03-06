package com.demo.finance.out.service;

import com.demo.finance.domain.model.Report;
import com.demo.finance.domain.usecase.GenerateReportUseCase;

import java.util.Optional;
import java.time.LocalDate;
import java.util.Map;

public class ReportService {
    private final GenerateReportUseCase generateReportUseCase;

    public ReportService(GenerateReportUseCase generateReportUseCase) {
        this.generateReportUseCase = generateReportUseCase;
    }

    public Optional<Report> generateUserReport(String userId) {
        return generateReportUseCase.generateUserReport(userId);
    }

    public Optional<Report> generateReportByDate(String userId, LocalDate from, LocalDate to) {
        return generateReportUseCase.generateReportByDate(userId, from, to);
    }

    public Map<String, Double> analyzeExpensesByCategory(String userId, LocalDate from, LocalDate to) {
        return generateReportUseCase.analyzeExpensesByCategory(userId, from, to);
    }
}