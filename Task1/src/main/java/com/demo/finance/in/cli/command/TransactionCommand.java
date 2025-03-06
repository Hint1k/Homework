package com.demo.finance.in.cli.command;

import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.domain.model.Transaction;

import java.util.List;
import java.util.Scanner;

public class TransactionCommand {
    private final CommandContext context;
    private final Scanner scanner;

    public TransactionCommand(CommandContext context, Scanner scanner) {
        this.context = context;
        this.scanner = scanner;
    }

    public void addTransaction() {
        System.out.print("Enter Transaction ID: ");
        Long transactionId = scanner.nextLong();
        System.out.print("Enter Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter Category: ");
        String category = scanner.nextLine();
        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter Description: ");
        String description = scanner.nextLine();
        System.out.print("Is it Income? (yes/no): ");
        boolean isIncome = scanner.nextLine().equalsIgnoreCase("yes");

        context.getTransactionController().addTransaction(
                transactionId, context.getCurrentUser().getUserId(), amount, category, date, description, isIncome);
        System.out.println("Transaction added successfully!");
    }

    public void viewAllTransactions() {
        List<Transaction> transactions = context.getTransactionController()
                .getTransactionsByUserId(context.getCurrentUser().getUserId());
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            transactions.forEach(System.out::println);
        }
    }

    public void filterTransactions() {
        System.out.print("Enter Start Date (YYYY-MM-DD) or leave empty: ");
        String fromDate = scanner.nextLine().trim();
        System.out.print("Enter End Date (YYYY-MM-DD) or leave empty: ");
        String toDate = scanner.nextLine().trim();
        System.out.print("Enter Category or leave empty: ");
        String category = scanner.nextLine().trim();
        System.out.print("Enter Type (INCOME/EXPENSE) or leave empty: ");
        String type = scanner.nextLine().trim();

        List<Transaction> transactions = context.getTransactionController().filterTransactions(
                context.getCurrentUser().getUserId(), fromDate, toDate, category, type);

        if (transactions.isEmpty()) {
            System.out.println("No matching transactions found.");
        } else {
            transactions.forEach(System.out::println);
        }
    }

    public void deleteTransaction() {
        System.out.print("Enter Transaction ID to delete: ");
        Long transactionId = scanner.nextLong();
        context.getTransactionController().deleteTransaction(transactionId);
        System.out.println("Transaction deleted successfully.");
    }
}