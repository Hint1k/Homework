package com.demo.finance.in.cli.command;

import com.demo.finance.domain.utils.MaxRetriesReachedException;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.domain.model.Report;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * Command class for generating reports related to the user's transactions and expenses.
 * Provides functionality for full reports, reports by date, and expense analysis by category.
 */
public class ReportCommand {

    private final Scanner scanner;
    private final CommandContext context;
    private final ValidationUtils validationUtils;
    private static final String PROMPT_START_DATE = "Enter Start Date (YYYY-MM-DD): ";
    private static final String PROMPT_END_DATE = "Enter End Date (YYYY-MM-DD): ";

    /**
     * Initializes the ReportCommand with the provided CommandContext, ValidationUtils, and Scanner.
     *
     * @param context The CommandContext that holds controllers for reports.
     * @param validationUtils Utility class for validation operations.
     * @param scanner The scanner used for input from the user.
     */
    public ReportCommand(CommandContext context, ValidationUtils validationUtils, Scanner scanner) {
        this.scanner = scanner;
        this.context = context;
        this.validationUtils = validationUtils;
    }

    /**
     * Generates and displays a full report of all transactions for the current user.
     * If no transactions are found, a message indicating this is shown.
     */
    public void generateFullReport() {
        Optional<Report> report = context.getReportController().generateReport(context.getCurrentUser().getUserId());
        report.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("No transactions found.")
        );
    }

    /**
     * Generates and displays a report of transactions within a specified date range for the current user.
     * The user is prompted to enter start and end dates.
     * If no transactions are found in the specified period, a message is displayed.
     */
    public void generateReportByDate() {
        try {
            LocalDate from = validationUtils.promptForValidDate(PROMPT_START_DATE, scanner);
            LocalDate to = validationUtils.promptForValidDate(PROMPT_END_DATE, scanner);
            Optional<Report> report = context.getReportController()
                    .generateReportByDate(context.getCurrentUser().getUserId(), from.toString(), to.toString());
            report.ifPresentOrElse(
                    System.out::println,
                    () -> System.out.println("No transactions found in the given period.")
            );
        } catch (MaxRetriesReachedException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Analyzes the user's expenses by category within a specified date range.
     * The user is prompted to enter start and end dates.
     * The analysis results are printed, showing the total expenses for each category.
     * If no expenses are found, a message is displayed.
     */
    public void analyzeExpensesByCategory() {
        try {
            LocalDate from = validationUtils.promptForValidDate(PROMPT_START_DATE, scanner);
            LocalDate to = validationUtils.promptForValidDate(PROMPT_END_DATE, scanner);

            Map<String, BigDecimal> categoryReport = context.getReportController()
                    .analyzeExpensesByCategory(context.getCurrentUser().getUserId(), from.toString(), to.toString());
            if (categoryReport.isEmpty()) {
                System.out.println("No expenses found in the given period.");
            } else {
                System.out.println("\n=== Expense Analysis by Category ===");
                categoryReport.forEach((category, total) -> System.out.println(category + ": $" + total));
            }
        } catch (MaxRetriesReachedException e) {
            System.out.println(e.getMessage());
        }
    }
}