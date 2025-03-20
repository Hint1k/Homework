package com.demo.finance.out.service;

import com.demo.finance.domain.model.Report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

/**
 * The {@code ReportService} interface defines methods for generating financial reports for users.
 * It includes generating user-specific reports, reports by date range, and analyzing expenses by category.
 */
public interface ReportService {

    /**
     * Generates a report for a specific user.
     *
     * @param userId the ID of the user for whom the report is generated
     * @return an {@link Optional} containing the generated {@link Report}, or {@code Optional.empty()}
     * if no report is found
     */
    Optional<Report> generateUserReport(Long userId);

    /**
     * Generates a report for a specific user within a given date range.
     *
     * @param userId the ID of the user for whom the report is generated
     * @param from the start date of the range
     * @param to the end date of the range
     * @return an {@link Optional} containing the generated {@link Report}, or {@code Optional.empty()}
     * if no report is found
     */
    Optional<Report> generateReportByDate(Long userId, LocalDate from, LocalDate to);

    /**
     * Generates a report for a specific user within a given date range, using string representations of dates.
     *
     * @param userId the ID of the user for whom the report is generated
     * @param fromDate the start date of the range, represented as a string
     * @param toDate the end date of the range, represented as a string
     * @return an {@link Optional} containing the generated {@link Report}, or {@code Optional.empty()}
     * if no report is found
     */
    Optional<Report> generateReportByDate(Long userId, String fromDate, String toDate);

    /**
     * Analyzes the user's expenses by category within a specified date range.
     *
     * @param userId the ID of the user whose expenses are analyzed
     * @param from the start date of the range
     * @param to the end date of the range
     * @return a {@link Map} where the key is the expense category and the value is the total amount spent
     * in that category
     */
    Map<String, BigDecimal> analyzeExpensesByCategory(Long userId, LocalDate from, LocalDate to);


    /**
     * Analyzes the user's expenses by category within a specified date range, using string representations of dates.
     *
     * @param userId the ID of the user whose expenses are analyzed
     * @param fromDate the start date of the range, represented as a string
     * @param toDate the end date of the range, represented as a string
     * @return a {@link Map} where the key is the expense category and the value is the total amount spent
     * in that category
     */
    Map<String, BigDecimal> analyzeExpensesByCategory(Long userId, String fromDate, String toDate);
}