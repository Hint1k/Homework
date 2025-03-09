package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.out.service.TransactionService;
import com.demo.finance.domain.utils.Type;

import java.time.LocalDate;
import java.util.List;

/**
 * The {@code TransactionController} class handles incoming requests related to transaction management.
 * It provides methods to add, get, update, delete, and filter transactions for users.
 */
public class TransactionController {
    private final TransactionService transactionService;

    /**
     * Constructs a {@code TransactionController} with the specified {@code TransactionService}.
     *
     * @param transactionService the {@code TransactionService} used for managing transactions
     */
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Adds a new transaction for the specified user.
     *
     * @param userId the ID of the user for whom the transaction is being added
     * @param amount the amount of the transaction
     * @param category the category of the transaction
     * @param date the date of the transaction (in the format "yyyy-MM-dd")
     * @param description a description of the transaction
     * @param type the type of the transaction (either income or expense)
     */
    public void addTransaction(Long userId, double amount, String category, String date, String description,
                               Type type) {
        transactionService.createTransaction(userId, amount, category, date, description, type);
    }

    /**
     * Retrieves a transaction by its ID.
     *
     * @param transactionId the ID of the transaction to retrieve
     * @return the {@code Transaction} object if found, otherwise {@code null}
     */
    public Transaction getTransaction(Long transactionId) {
        return transactionService.getTransaction(transactionId);
    }

    /**
     * Retrieves all transactions for the specified user.
     *
     * @param userId the ID of the user whose transactions are to be retrieved
     * @return a list of {@code Transaction} objects for the specified user
     */
    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionService.getTransactionsByUserId(userId);
    }

    /**
     * Updates an existing transaction for the specified user.
     *
     * @param transactionId the ID of the transaction to update
     * @param userId the ID of the user who owns the transaction
     * @param amount the updated amount of the transaction
     * @param category the updated category of the transaction
     * @param description the updated description of the transaction
     * @return {@code true} if the transaction was successfully updated, {@code false} otherwise
     */
    public boolean updateTransaction(Long transactionId, Long userId, double amount, String category,
                                     String description) {
        return transactionService.updateTransaction(transactionId, userId, amount, category, description);
    }

    /**
     * Deletes a transaction for the specified user.
     *
     * @param userId the ID of the user who owns the transaction
     * @param transactionId the ID of the transaction to delete
     * @return {@code true} if the transaction was successfully deleted, {@code false} otherwise
     */
    public boolean deleteTransaction(Long userId, Long transactionId) {
        return transactionService.deleteTransaction(userId, transactionId);
    }

    /**
     * Filters transactions for the specified user based on the given criteria.
     *
     * @param userId the ID of the user whose transactions are to be filtered
     * @param from the start date of the filter range (inclusive)
     * @param to the end date of the filter range (inclusive)
     * @param category the category of the transactions to filter by
     * @param type the type of the transactions to filter by (either income or expense)
     * @return a list of {@code Transaction} objects matching the specified filter criteria
     */
    public List<Transaction> filterTransactions(Long userId, LocalDate from, LocalDate to, String category,
                                                Type type) {
        return transactionService.getFilteredTransactions(userId, from, to, category, type);
    }
}