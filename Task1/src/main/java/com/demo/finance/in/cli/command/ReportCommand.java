package com.demo.finance.in.cli.command;

import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.domain.model.Report;

import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;
import java.util.Scanner;

public class ReportCommand {

    private final Scanner scanner;
    private final CommandContext context;
    private final ValidationUtils validationUtils;
    private static final String PROMPT_START_DATE = "Enter Start Date (YYYY-MM-DD): ";
    private static final String PROMPT_END_DATE = "Enter End Date (YYYY-MM-DD): ";

    public ReportCommand(CommandContext context, ValidationUtils validationUtils, Scanner scanner) {
        this.scanner = scanner;
        this.context = context;
        this.validationUtils = validationUtils;
    }

    public void generateFullReport() {
        Optional<Report> report = context.getReportController().generateReport(context.getCurrentUser().getUserId());
        report.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("No transactions found.")
        );
    }

    public void generateReportByDate() {
        LocalDate from = validationUtils.promptForValidDate(PROMPT_START_DATE, scanner);
        LocalDate to = validationUtils.promptForValidDate(PROMPT_END_DATE, scanner);

        Optional<Report> report =
                context.getReportController()
                        .generateReportByDate(context.getCurrentUser().getUserId(), from.toString(), to.toString());
        report.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("No transactions found in the given period.")
        );
    }

    public void analyzeExpensesByCategory() {
        LocalDate from = validationUtils.promptForValidDate(PROMPT_START_DATE, scanner);
        LocalDate to = validationUtils.promptForValidDate(PROMPT_END_DATE, scanner);

        Map<String, Double> categoryReport = context.getReportController()
                .analyzeExpensesByCategory(context.getCurrentUser().getUserId(), from.toString(), to.toString());
        if (categoryReport.isEmpty()) {
            System.out.println("No expenses found in the given period.");
        } else {
            System.out.println("\n=== Expense Analysis by Category ===");
            categoryReport.forEach((category, total) -> System.out.println(category + ": $" + total));
        }
    }
}