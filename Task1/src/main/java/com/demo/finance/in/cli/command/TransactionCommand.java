package com.demo.finance.in.cli.command;

import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.domain.model.Transaction;

import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TransactionCommand {
    private final CommandContext context;
    private final Scanner scanner;

    public TransactionCommand(CommandContext context, Scanner scanner) {
        this.context = context;
        this.scanner = scanner;
    }

    public void addTransaction() {
        double amount = promptForPositiveDouble();
        String category = promptForNonEmptyString("Enter Category: ");
        LocalDate date = promptForValidDate();
        String description = promptForNonEmptyString("Enter Description: ");
        boolean isIncome = promptForBoolean();

        Long userId = context.getCurrentUser().getUserId();
        context.getTransactionController()
                .addTransaction(userId, amount, category, date.toString(), description, isIncome);
        System.out.println("Transaction added successfully!");
    }

    public void viewTransactionsByUserId() {
        List<Transaction> transactions = context.getTransactionController()
                .getTransactionsByUserId(context.getCurrentUser().getUserId());
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            transactions.forEach(System.out::println);
        }
    }

    public void viewAllTransactions() {
        List<Transaction> transactions = context.getTransactionController().getAllTransactions();
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            transactions.forEach(System.out::println);
        }
    }

    public void filterTransactions() {
        String fromDate = promptForOptionalDate("Enter Start Date (YYYY-MM-DD) or leave empty: ");
        String toDate = promptForOptionalDate("Enter End Date (YYYY-MM-DD) or leave empty: ");
        String category = promptForNonEmptyString("Enter Category: ");
        String type = promptForTransactionType();

        List<Transaction> transactions = context.getTransactionController().filterTransactions(
                context.getCurrentUser().getUserId(), fromDate, toDate, category, type);

        if (transactions.isEmpty()) {
            System.out.println("No matching transactions found.");
        } else {
            transactions.forEach(System.out::println);
        }
    }

    public void deleteTransaction() {
        long transactionId = promptForPositiveLong();
        context.getTransactionController().deleteTransaction(transactionId);
        System.out.println("Transaction deleted successfully.");
    }

    private double promptForPositiveDouble() {
        while (true) {
            try {
                System.out.print("Enter Amount: ");
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value > 0) return value;
                System.out.println("Error: Value must be positive.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }

    private long promptForPositiveLong() {
        while (true) {
            try {
                System.out.print("Enter Transaction ID to delete: ");
                long value = Long.parseLong(scanner.nextLine().trim());
                if (value > 0) return value;
                System.out.println("Error: Value must be positive.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }

    private String promptForNonEmptyString(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("Error: Input cannot be empty.");
        }
    }

    private LocalDate promptForValidDate() {
        while (true) {
            System.out.print("Enter Date (YYYY-MM-DD): ");
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Error: Please enter a valid date in YYYY-MM-DD format.");
            }
        }
    }

    private String promptForOptionalDate(String message) {
        System.out.print(message);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return "";
        try {
            LocalDate.parse(input);
            return input;
        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date format. Skipping input.");
            return "";
        }
    }

    private boolean promptForBoolean() {
        while (true) {
            System.out.print("Is it Income? (y = yes / n = no): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            }
            if (input.equals("n") || input.equals("no")) {
                return false;
            }
            System.out.println("Error: Please enter 'y' for yes or 'n' for no.");
        }
    }

    private String promptForTransactionType() {
        while (true) {
            System.out.print("What Type? (i = INCOME / e = EXPENSE): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("i") || input.equals("income")) {
                return "INCOME";
            }
            if (input.equals("e") || input.equals("expense")) {
                return "EXPENSE";
            }
            System.out.println("Error: Please enter 'i' for INCOME, 'e' for EXPENSE");
        }
    }
}