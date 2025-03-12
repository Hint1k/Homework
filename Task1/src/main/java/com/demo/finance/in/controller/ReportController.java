package com.demo.finance.in.controller;

import com.demo.finance.out.service.ReportService;
import com.demo.finance.domain.model.Report;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Map;

/**
 * The {@code ReportController} class handles incoming requests related to report generation and analysis.
 * It provides methods to generate user reports and analyze expenses by category.
 */
public class ReportController {
    private final ReportService reportService;

    /**
     * Constructs a {@code ReportController} with the specified {@code ReportService}.
     *
     * @param reportService the {@code ReportService} used for generating reports and analyzing data
     */
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Generates a report for the specified user.
     *
     * @param userId the ID of the user for whom the report is to be generated
     * @return an {@code Optional} containing the generated {@code Report} if successful, or empty if not
     */
    public Optional<Report> generateReport(Long userId) {
        return reportService.generateUserReport(userId);
    }

    /**
     * Generates a report for the specified user within a given date range.
     *
     * @param userId the ID of the user for whom the report is to be generated
     * @param fromDate the start date of the report range (in the format "yyyy-MM-dd")
     * @param toDate the end date of the report range (in the format "yyyy-MM-dd")
     * @return an {@code Optional} containing the generated {@code Report} for the date range, or empty if not
     */
    public Optional<Report> generateReportByDate(Long userId, String fromDate, String toDate) {
        return reportService.generateReportByDate(userId, fromDate, toDate);
    }

    /**
     * Analyzes the expenses of a user by category within a given date range.
     *
     * @param userId the ID of the user whose expenses are to be analyzed
     * @param fromDate the start date of the expense analysis range (in the format "yyyy-MM-dd")
     * @param toDate the end date of the expense analysis range (in the format "yyyy-MM-dd")
     * @return a {@code Map} where keys are category names and values are the corresponding total expenses
     * in that category
     */
    public Map<String, BigDecimal> analyzeExpensesByCategory(Long userId, String fromDate, String toDate) {
        return reportService.analyzeExpensesByCategory(userId, fromDate, toDate);
    }
}