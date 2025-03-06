package com.demo.finance.out.service;

import com.demo.finance.domain.model.Report;
import com.demo.finance.domain.usecase.GenerateReportUseCase;

import java.util.Optional;

public class ReportService {
    private final GenerateReportUseCase generateReportUseCase;

    public ReportService(GenerateReportUseCase generateReportUseCase) {
        this.generateReportUseCase = generateReportUseCase;
    }

    public Optional<Report> generateUserReport(String userId) {
        return generateReportUseCase.generateUserReport(userId);
    }
}