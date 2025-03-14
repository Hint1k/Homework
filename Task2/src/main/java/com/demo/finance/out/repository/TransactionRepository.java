package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * The TransactionRepository interface defines the methods for managing Transaction entities in the repository.
 * It provides CRUD operations and query methods for Transaction objects.
 */
public interface TransactionRepository {

    /**
     * Saves a Transaction entity to the repository.
     *
     * @param transaction the Transaction entity to be saved
     */
    void save(Transaction transaction);

    /**
     * Updates an existing Transaction entity in the repository.
     *
     * @param transaction the Transaction entity with updated details
     * @return true if the transaction was updated successfully, false otherwise
     */
    boolean update(Transaction transaction);

    /**
     * Deletes a Transaction entity from the repository by its ID.
     *
     * @param transactionId the ID of the transaction to be deleted
     * @return true if the transaction was deleted successfully, false otherwise
     */
    boolean delete(Long transactionId);

    /**
     * Finds a Transaction entity by its ID.
     *
     * @param transactionId the ID of the transaction to be found
     * @return the Transaction entity if found, otherwise null
     */
    Transaction findById(Long transactionId);

    /**
     * Finds all Transaction entities associated with a specific user ID.
     *
     * @param userId the ID of the user
     * @return a list of Transaction entities associated with the user
     */
    List<Transaction> findByUserId(Long userId);

    /**
     * Finds Transaction entities associated with a specific user ID, filtered by date range, category, and type.
     *
     * @param userId the ID of the user
     * @param from the start date of the filter range (inclusive)
     * @param to the end date of the filter range (inclusive)
     * @param category the category to filter by (optional)
     * @param type the type of transaction to filter by (optional)
     * @return a list of Transaction entities matching the filters
     */
    List<Transaction> findFiltered(Long userId, LocalDate from, LocalDate to, String category, Type type);

    /**
     * Finds a Transaction entity by its ID and associated user ID.
     *
     * @param userId the ID of the user associated with the transaction
     * @param transactionId the ID of the transaction
     * @return an Optional containing the Transaction entity if found, otherwise an empty Optional
     */
    Optional<Transaction> findByUserIdAndTransactionId(Long userId, Long transactionId);
}