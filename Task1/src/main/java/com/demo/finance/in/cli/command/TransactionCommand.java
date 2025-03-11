package com.demo.finance.in.cli.command;

import com.demo.finance.domain.utils.MaxRetriesReachedException;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.domain.model.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Scanner;

/**
 * Command class for handling transactions, including adding, updating, deleting, viewing, and filtering.
 * This class interacts with the TransactionController to perform the operations.
 */
public class TransactionCommand {

    private final CommandContext context;
    private final ValidationUtils validationUtils;
    private final Scanner scanner;
    private static final String OR_KEEP_CURRENT_VALUE = " or leave it blank to keep current value: ";

    /**
     * Initializes the TransactionCommand with the provided CommandContext, ValidationUtils, and Scanner.
     *
     * @param context The CommandContext that holds controllers for transactions.
     * @param validationUtils Utility class for validation operations.
     * @param scanner The scanner used for input from the user.
     */
    public TransactionCommand(CommandContext context, ValidationUtils validationUtils, Scanner scanner) {
        this.context = context;
        this.validationUtils = validationUtils;
        this.scanner = scanner;
    }

    /**
     * Adds a new transaction for the current user. The user is prompted for transaction details.
     * The transaction is then added via the TransactionController.
     */
    public void addTransaction() {
        try {
            BigDecimal amount = validationUtils.promptForPositiveBigDecimal("Enter Amount: ", scanner);
            String category = validationUtils.promptForNonEmptyString("Enter Category: ", scanner);
            LocalDate date = validationUtils.promptForValidDate("Enter Date (YYYY-MM-DD): ", scanner);
            String description = validationUtils.promptForNonEmptyString("Enter Description: ", scanner);
            Type type = validationUtils.promptForTransactionType(scanner);

            Long userId = context.getCurrentUser().getUserId();
            context.getTransactionController()
                    .addTransaction(userId, amount, category, date.toString(), description, type);
            System.out.println("Transaction added successfully!");
        } catch (MaxRetriesReachedException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Updates an existing transaction. The user is prompted for the transaction ID and optional updated details.
     * If the transaction is found and belongs to the user, it is updated via the TransactionController.
     */
    public void updateTransaction() {
        Long transactionId;
        try {
            transactionId = validationUtils.promptForPositiveLong("Enter Transaction ID: ", scanner);
        } catch (MaxRetriesReachedException e) {
            System.out.println(e.getMessage());
            return;
        }
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
        BigDecimal amount = validationUtils.promptForOptionalPositiveBigDecimal("Enter Amount"
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

    /**
     * Deletes an existing transaction. The user is prompted for the transaction ID.
     * If the transaction exists and belongs to the user, it is deleted via the TransactionController.
     */
    public void deleteTransaction() {
        Long transactionId;
        try {
            transactionId = validationUtils.promptForPositiveLong("Enter Transaction ID: ", scanner);
        } catch (MaxRetriesReachedException e) {
            System.out.println(e.getMessage());
            return;
        }
        Long userId = context.getCurrentUser().getUserId();
        if (context.getTransactionController().deleteTransaction(userId, transactionId)) {
            System.out.println("Transaction deleted successfully.");
        } else {
            System.out.println("Failed to delete transaction. " +
                    "Either the transaction does not exist or it does not belong to you.");
        }
    }

    /**
     * Displays all transactions for the current user.
     * If no transactions are found, a message indicating this is shown.
     */
    public void viewTransactionsByUserId() {
        List<Transaction> transactions = context.getTransactionController()
                .getTransactionsByUserId(context.getCurrentUser().getUserId());
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            transactions.forEach(System.out::println);
        }
    }

    /**
     * Filters and displays transactions based on optional criteria such as date range, category, and type.
     * If no transactions match the filters, a message indicating this is shown.
     */
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