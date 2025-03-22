package com.demo.finance.out.service;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * The {@code TransactionService} interface defines methods for managing user transactions.
 * This includes creating, updating, deleting, and retrieving transactions, as well as filtering transactions
 * by specific criteria.
 */
public interface TransactionService {

    /**
     * Creates a new transaction for a user.
     *
     * @param userId      the ID of the user for whom the transaction is created
     * @param amount      the amount of the transaction
     * @param category    the category of the transaction (e.g., food, rent, etc.)
     * @param date        the date of the transaction
     * @param description a description of the transaction
     * @param type        the type of transaction (either {@link Type#INCOME} or {@link Type#EXPENSE})
     */
    void createTransaction(Long userId, BigDecimal amount, String category, String date, String description, Type type);

    /**
     * Retrieves all transactions associated with a specific user.
     *
     * @param userId the ID of the user whose transactions are retrieved
     * @return a list of {@link Transaction} objects belonging to the user
     */
    List<Transaction> getTransactionsByUserId(Long userId);

    /**
     * Retrieves a specific transaction by its ID.
     *
     * @param transactionId the ID of the transaction to be retrieved
     * @return the {@link Transaction} object with the specified ID
     */
    Transaction getTransaction(Long transactionId);

    /**
     * Updates an existing transaction for a user.
     *
     * @param transactionId the ID of the transaction to be updated
     * @param userId        the ID of the user associated with the transaction
     * @param amount        the new amount for the transaction
     * @param category      the new category for the transaction
     * @param description   the new description for the transaction
     * @return {@code true} if the transaction was successfully updated, {@code false} otherwise
     */
    boolean updateTransaction(Long transactionId, Long userId, BigDecimal amount, String category, String description);

    /**
     * Deletes a transaction for a user.
     *
     * @param userId        the ID of the user associated with the transaction to be deleted
     * @param transactionId the ID of the transaction to be deleted
     * @return {@code true} if the transaction was successfully deleted, {@code false} otherwise
     */
    boolean deleteTransaction(Long userId, Long transactionId);

    /**
     * Retrieves a list of transactions for a user that match the specified filtering criteria.
     *
     * @param userId   the ID of the user whose transactions are retrieved
     * @param from     the start date for filtering the transactions
     * @param to       the end date for filtering the transactions
     * @param category the category to filter the transactions by (can be {@code null} to ignore)
     * @param type     the type of transactions to filter (either {@link Type#INCOME} or {@link Type#EXPENSE})
     * @return a list of {@link Transaction} objects that match the filtering criteria
     */
    List<Transaction> getFilteredTransactions(Long userId, LocalDate from, LocalDate to, String category, Type type);

    List<Transaction> getFilteredTransactionsWithPagination(Long userId, LocalDate fromDate, LocalDate toDate,
                                                            String category, Type type, int page, int size);

    int getTotalFilteredTransactionsCount(Long userId, LocalDate fromDate, LocalDate toDate, String category,
                                          Type type);
}