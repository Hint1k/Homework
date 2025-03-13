package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.time.LocalDate;

/**
 * The TransactionRepositoryImpl class provides an implementation of the TransactionRepository interface.
 * It manages Transaction entities using an in-memory ConcurrentHashMap for storage.
 */
public class TransactionRepositoryImpl implements TransactionRepository {

    private final Map<Long, Transaction> transactions = new ConcurrentHashMap<>();

    /**
     * Saves a Transaction entity to the repository.
     *
     * @param transaction the Transaction entity to be saved
     */
    @Override
    public void save(Transaction transaction) {
        transactions.put(transaction.getTransactionId(), transaction);
    }

    /**
     * Updates an existing Transaction entity in the repository.
     *
     * @param transaction the Transaction entity with updated details
     * @return true if the transaction was updated successfully, false otherwise
     */
    @Override
    public boolean update(Transaction transaction) {
        if (transactions.containsKey(transaction.getTransactionId())) {
            transactions.put(transaction.getTransactionId(), transaction);
            return true;
        }
        return false;
    }

    /**
     * Deletes a Transaction entity from the repository by its ID.
     *
     * @param transactionId the ID of the transaction to be deleted
     * @return true if the transaction was deleted successfully, false otherwise
     */
    @Override
    public boolean delete(Long transactionId) {
        if (transactions.containsKey(transactionId)) {
            transactions.remove(transactionId);
            return true;
        }
        return false;
    }

    /**
     * Finds a Transaction entity by its ID.
     *
     * @param transactionId the ID of the transaction to be found
     * @return the Transaction entity if found, otherwise null
     * @throws IllegalArgumentException if the transaction ID is null
     */
    @Override
    public Transaction findByTransactionId(Long transactionId) {
        if (transactionId == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null.");
        }
        return transactions.get(transactionId);
    }

    /**
     * Finds all Transaction entities associated with a specific user ID.
     *
     * @param userId the ID of the user
     * @return a list of Transaction entities associated with the user
     */
    @Override
    public List<Transaction> findByUserId(Long userId) {
        return transactions.values().stream()
                .filter(t -> t.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

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
    @Override
    public List<Transaction> findFiltered(Long userId, LocalDate from, LocalDate to, String category, Type type) {
        return transactions.values().stream()
                .filter(t -> t.getUserId().equals(userId)) // Always filter by ID, other filters - optional
                .filter(t -> from == null || !t.getDate().isBefore(from))
                .filter(t -> to == null || !t.getDate().isAfter(to))
                .filter(t -> category == null || t.getCategory().equalsIgnoreCase(category))
                .filter(t -> type == null || t.getType() == type)
                .collect(Collectors.toList());
    }

    /**
     * Generates the next available transaction ID.
     *
     * @return the next transaction ID
     */
    @Override
    public Long generateNextId() {
        return transactions.keySet().stream().max(Long::compare).orElse(0L) + 1;
    }

    /**
     * Finds a Transaction entity by its ID and associated user ID.
     *
     * @param transactionId the ID of the transaction
     * @param userId the ID of the user associated with the transaction
     * @return an Optional containing the Transaction entity if found, otherwise an empty Optional
     */
    @Override
    public Optional<Transaction> findByUserIdAndTransactionId(Long transactionId, Long userId) {
        return transactions.values().stream()
                .filter(t -> t.getTransactionId().equals(transactionId) && t.getUserId().equals(userId))
                .findFirst();
    }
}