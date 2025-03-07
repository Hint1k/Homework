package com.demo.finance.in.cli.command;

import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.domain.model.Report;

import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ReportCommand {
    private final CommandContext context;
    private final Scanner scanner;

    public ReportCommand(CommandContext context, Scanner scanner) {
        this.context = context;
        this.scanner = scanner;
    }

    public void generateFullReport() {
        Optional<Report> report = context.getReportController().generateReport(context.getCurrentUser().getUserId());
        report.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("No transactions found.")
        );
    }

    public void generateReportByDate() {
        LocalDate from = promptForValidDate("Enter Start Date (YYYY-MM-DD): ");
        LocalDate to = promptForValidDate("Enter End Date (YYYY-MM-DD): ");

        Optional<Report> report =
                context.getReportController()
                        .generateReportByDate(context.getCurrentUser().getUserId(), from.toString(), to.toString());
        report.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("No transactions found in the given period.")
        );
    }

    public void analyzeExpensesByCategory() {
        LocalDate from = promptForValidDate("Enter Start Date (YYYY-MM-DD): ");
        LocalDate to = promptForValidDate("Enter End Date (YYYY-MM-DD): ");

        Map<String, Double> categoryReport = context.getReportController()
                .analyzeExpensesByCategory(context.getCurrentUser().getUserId(), from.toString(), to.toString());
        if (categoryReport.isEmpty()) {
            System.out.println("No expenses found in the given period.");
        } else {
            System.out.println("\n=== Expense Analysis by Category ===");
            categoryReport.forEach((category, total) -> System.out.println(category + ": $" + total));
        }
    }

    private LocalDate promptForValidDate(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Error: Please enter a valid date in YYYY-MM-DD format.");
            }
        }
    }
}