package com.demo.finance.in.cli.command;

import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.domain.model.Report;

import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

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
        System.out.print("Enter Start Date (YYYY-MM-DD): ");
        String from = scanner.nextLine();
        System.out.print("Enter End Date (YYYY-MM-DD): ");
        String to = scanner.nextLine();

        Optional<Report> report =
                context.getReportController().generateReportByDate(context.getCurrentUser().getUserId(), from, to);
        report.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("No transactions found in the given period.")
        );
    }

    public void analyzeExpensesByCategory() {
        System.out.print("Enter Start Date (YYYY-MM-DD): ");
        String from = scanner.nextLine();
        System.out.print("Enter End Date (YYYY-MM-DD): ");
        String to = scanner.nextLine();

        Map<String, Double> categoryReport = context.getReportController()
                .analyzeExpensesByCategory(context.getCurrentUser().getUserId(), from, to);
        if (categoryReport.isEmpty()) {
            System.out.println("No expenses found in the given period.");
        } else {
            System.out.println("\n=== Expense Analysis by Category ===");
            categoryReport.forEach((category, total) -> System.out.println(category + ": $" + total));
        }
    }
}