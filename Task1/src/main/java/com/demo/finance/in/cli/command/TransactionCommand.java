package com.demo.finance.in.cli.command;

import com.demo.finance.domain.utils.Type;
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
        Type type = promptForTransactionType();

        Long userId = context.getCurrentUser().getUserId();
        context.getTransactionController()
                .addTransaction(userId, amount, category, date.toString(), description, type);
        System.out.println("Transaction added successfully!");
    }

    public void updateTransaction() {
        Long transactionId = promptForPositiveLong();
        Long userId = context.getCurrentUser().getUserId();
        double amount = promptForPositiveDouble();
        String category = promptForNonEmptyString("Enter Category: ");
        String description = promptForNonEmptyString("Enter Description: ");
        boolean isUpdated = context.getTransactionController()
                .updateTransaction(transactionId, userId, amount, category, description);
        if (isUpdated) {
            System.out.println("Transaction updated successfully!");
        } else {
            System.out.println("Failed to update transaction!" +
                    "Either the transaction does not exist or it does not belong to you.");
        }
    }

    public void deleteTransaction() {
        Long transactionId = promptForPositiveLong();
        Long userId = context.getCurrentUser().getUserId();
        if (context.getTransactionController().deleteTransaction(userId, transactionId)) {
            System.out.println("Transaction deleted successfully.");
        } else {
            System.out.println("Failed to delete transaction. " +
                    "Either the transaction does not exist or it does not belong to you.");
        }
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

    public void filterTransactions() {
        LocalDate fromDate = promptForOptionalDate("Enter Start Date (YYYY-MM-DD) or leave empty: ");
        LocalDate toDate = promptForOptionalDate("Enter End Date (YYYY-MM-DD) or leave empty: ");
        String category = promptForOptionalString();
        Type type = promptForOptionalTransactionType();
        Long userId = context.getCurrentUser().getUserId();

        List<Transaction> transactions =
                context.getTransactionController().filterTransactions(userId, fromDate, toDate, category, type);

        if (transactions.isEmpty()) {
            System.out.println("No matching transactions found.");
        } else {
            transactions.forEach(System.out::println);
        }
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
                System.out.print("Enter Transaction ID: ");
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

    private String promptForOptionalString() {
        System.out.print("Enter Category or leave empty: ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? null : input;
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

    private LocalDate promptForOptionalDate(String message) {
        System.out.print(message);
        String input = scanner.nextLine().trim();
        try {
            return input.isEmpty() ? null : LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date format. Skipping input.");
            return null;
        }
    }

    private Type promptForTransactionType() {
        while (true) {
            System.out.print("What Type? (i = INCOME / e = EXPENSE): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("i") || input.equalsIgnoreCase("income")) {
                return Type.INCOME;
            }
            if (input.equals("e") || input.equalsIgnoreCase("expense")) {
                return Type.EXPENSE;
            }
            System.out.println("Error: Please enter 'i' for INCOME, 'e' for EXPENSE");
        }
    }

    private Type promptForOptionalTransactionType() {
        while (true) {
            System.out.print("Input type (i = INCOME / e = EXPENSE) or leave empty: ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("i") || input.equalsIgnoreCase("income")) {
                return Type.INCOME;
            }
            if (input.equals("e") || input.equalsIgnoreCase("expense")) {
                return Type.EXPENSE;
            }
            return null;
        }
    }
}