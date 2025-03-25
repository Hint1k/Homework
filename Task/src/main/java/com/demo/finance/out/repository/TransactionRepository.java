package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;

import java.time.LocalDate;
import java.util.List;

/**
 * The {@code TransactionRepository} interface defines the contract for operations related to transaction data persistence.
 * It provides methods for saving, updating, deleting, and retrieving transactions from the database.
 */
public interface TransactionRepository {

    /**
     * Saves a new transaction to the database.
     *
     * @param transaction the {@link Transaction} object to be saved
     * @return the unique identifier ({@code Long}) of the newly saved transaction
     */
    Long save(Transaction transaction);

    /**
     * Updates an existing transaction in the database.
     *
     * @param transaction the {@link Transaction} object containing updated information
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    boolean update(Transaction transaction);

    /**
     * Deletes a transaction from the database based on its unique transaction ID.
     *
     * @param transactionId the unique identifier of the transaction to delete
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    boolean delete(Long transactionId);

    /**
     * Retrieves a specific transaction by its unique transaction ID.
     *
     * @param transactionId the unique identifier of the transaction
     * @return the {@link Transaction} object matching the provided transaction ID, or {@code null} if not found
     */
    Transaction findById(Long transactionId);

    /**
     * Retrieves all transactions associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @return a {@link List} of {@link Transaction} objects associated with the user
     */
    List<Transaction> findByUserId(Long userId);

    /**
     * Retrieves a paginated list of transactions associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @param offset the starting index for pagination (zero-based)
     * @param size   the maximum number of transactions to retrieve
     * @return a {@link List} of {@link Transaction} objects representing the paginated results
     */
    List<Transaction> findByUserId(Long userId, int offset, int size);

    /**
     * Retrieves a filtered list of transactions based on user ID, date range, category, and transaction type.
     *
     * @param userId   the unique identifier of the user
     * @param from     the start date of the filter period (inclusive)
     * @param to       the end date of the filter period (inclusive)
     * @param category the category of the transactions to filter by (optional, can be {@code null})
     * @param type     the type of the transactions to filter by (optional, can be {@code null})
     * @return a {@link List} of {@link Transaction} objects matching the filter criteria
     */
    List<Transaction> findFiltered(Long userId, LocalDate from, LocalDate to, String category, Type type);

    /**
     * Retrieves a specific transaction associated with a user by their user ID and transaction ID.
     *
     * @param userId        the unique identifier of the user
     * @param transactionId the unique identifier of the transaction
     * @return the {@link Transaction} object matching the provided user ID and transaction ID, or {@code null} if not found
     */
    Transaction findByUserIdAndTransactionId(Long userId, Long transactionId);

    /**
     * Retrieves the total count of transactions associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @return the total number of transactions as an integer
     */
    int getTotalTransactionCountForUser(Long userId);
}