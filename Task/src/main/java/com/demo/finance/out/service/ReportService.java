package com.demo.finance.out.service;

import com.demo.finance.domain.model.Report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * The {@code ReportService} interface defines the contract for operations related to report generation and analysis.
 * It provides methods for generating user-specific reports and analyzing financial data such as expenses by category.
 */
public interface ReportService {

    /**
     * Generates a comprehensive financial report for a specific user.
     *
     * @param userId the unique identifier of the user for whom the report is generated
     * @return a {@link Report} object containing the user's financial data
     */
    Report generateUserReport(Long userId);

    /**
     * Generates a financial report for a specific user within a given date range.
     *
     * @param userId the unique identifier of the user
     * @param from   the start date of the report period (inclusive)
     * @param to     the end date of the report period (inclusive)
     * @return a {@link Report} object containing the user's financial data within the specified date range
     */
    Report generateReportByDate(Long userId, LocalDate from, LocalDate to);

    /**
     * Analyzes and aggregates expenses by category for a specific user within a given date range.
     *
     * @param userId the unique identifier of the user
     * @param from   the start date of the analysis period (inclusive)
     * @param to     the end date of the analysis period (inclusive)
     * @return a {@link Map} where the keys represent expense categories and the values represent the total amount spent in each category
     */
    Map<String, BigDecimal> analyzeExpensesByCategory(Long userId, LocalDate from, LocalDate to);
}