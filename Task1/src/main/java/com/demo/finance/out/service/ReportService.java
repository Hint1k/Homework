package com.demo.finance.out.service;

import com.demo.finance.domain.model.Report;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public interface ReportService {

    Optional<Report> generateUserReport(Long userId);

    Optional<Report> generateReportByDate(Long userId, LocalDate from, LocalDate to);

    Map<String, Double> analyzeExpensesByCategory(Long userId, LocalDate from, LocalDate to);

    Optional<Report> generateReportByDate(Long userId, String fromDate, String toDate);

    Map<String, Double> analyzeExpensesByCategory(Long userId, String fromDate, String toDate);
}