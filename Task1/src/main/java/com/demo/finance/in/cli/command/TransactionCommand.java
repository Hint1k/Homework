package com.demo.finance.in.cli.command;

import com.demo.finance.domain.utils.Type;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.domain.model.Transaction;

import java.util.List;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Scanner;

public class TransactionCommand {

    private final CommandContext context;
    private final ValidationUtils validationUtils;
    private final Scanner scanner;
    private static final String OR_KEEP_CURRENT_VALUE = " or leave it blank to keep current value: ";

    public TransactionCommand(CommandContext context, ValidationUtils validationUtils, Scanner scanner) {
        this.context = context;
        this.validationUtils = validationUtils;
        this.scanner = scanner;
    }

    public void addTransaction() {
        Double amount = validationUtils.promptForPositiveDouble("Enter Amount: ", scanner);
        String category = validationUtils.promptForNonEmptyString("Enter Category: ", scanner);
        LocalDate date = validationUtils.promptForValidDate("Enter Date (YYYY-MM-DD): ", scanner);
        String description = validationUtils.promptForNonEmptyString("Enter Description: ", scanner);
        Type type = validationUtils.promptForTransactionType(scanner);

        Long userId = context.getCurrentUser().getUserId();
        context.getTransactionController()
                .addTransaction(userId, amount, category, date.toString(), description, type);
        System.out.println("Transaction added successfully!");
    }

    public void updateTransaction() {
        Long transactionId = validationUtils.promptForPositiveLong("Enter Transaction ID: ", scanner);
        Transaction transactionToUpdate = context.getTransactionController().getTransaction(transactionId);
        if (transactionToUpdate == null) {
            System.out.println("Error: Transaction not found.");
            return;
        }
        Long userId = context.getCurrentUser().getUserId();
        if (!Objects.equals(userId, transactionToUpdate.getUserId())) {
            System.out.println("Error: You are not the transaction owner to update this transaction.");
            return;
        }
        Double amount = validationUtils.promptForOptionalPositiveDouble("Enter Amount"
                + OR_KEEP_CURRENT_VALUE, scanner);
        if (amount == null) {
            amount = transactionToUpdate.getAmount();
        }
        String category = validationUtils.promptForOptionalString("Enter Category"
                + OR_KEEP_CURRENT_VALUE, scanner);
        if (category == null) {
            category = transactionToUpdate.getCategory();
        }
        String description = validationUtils.promptForOptionalString("Enter Description"
                + OR_KEEP_CURRENT_VALUE, scanner);
        if (description == null) {
            description = transactionToUpdate.getDescription();
        }
        boolean isUpdated = context.getTransactionController()
                .updateTransaction(transactionId, userId, amount, category, description);
        if (isUpdated) {
            System.out.println("Transaction updated successfully!");
        } else {
            System.out.println("Failed to update transaction! Server error.");
        }
    }

    public void deleteTransaction() {
        Long transactionId = validationUtils.promptForPositiveLong("Enter Transaction ID: ", scanner);
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
        LocalDate fromDate = validationUtils
                .promptForOptionalDate("Enter Start Date (YYYY-MM-DD) or leave empty: ", scanner);
        LocalDate toDate = validationUtils
                .promptForOptionalDate("Enter End Date (YYYY-MM-DD) or leave empty: ", scanner);
        String category = validationUtils.promptForOptionalString("Enter Category or leave empty: ", scanner);
        Type type = validationUtils.promptForOptionalTransactionType(scanner);
        Long userId = context.getCurrentUser().getUserId();

        List<Transaction> transactions =
                context.getTransactionController().filterTransactions(userId, fromDate, toDate, category, type);

        if (transactions.isEmpty()) {
            System.out.println("No transactions found matching the filters.");
        } else {
            transactions.forEach(System.out::println);
        }
    }
}