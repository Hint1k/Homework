package com.demo.finance.out.service;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * The {@code TransactionServiceImpl} class implements the {@link TransactionService} interface.
 * It provides functionality for managing transactions, including creating, updating, deleting,
 * and filtering transactions.
 */
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    /**
     * Constructor to initialize the service with a {@link TransactionRepository}.
     *
     * @param transactionRepository the {@link TransactionRepository} used for transaction data persistence
     */
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Creates a new transaction for a user.
     *
     * @param userId the ID of the user for whom the transaction is created
     * @param amount the amount of the transaction
     * @param category the category of the transaction (e.g., food, rent, etc.)
     * @param date the date of the transaction
     * @param description a description of the transaction
     * @param type the type of transaction (either {@link Type#INCOME} or {@link Type#EXPENSE})
     * @throws IllegalArgumentException if the amount is negative
     */
    @Override
    public void createTransaction(Long userId, BigDecimal amount, String category, String date, String description,
                                  Type type) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        LocalDate transactionDate = LocalDate.parse(date);
        Long transactionId = transactionRepository.generateNextId();
        Transaction transaction =
                new Transaction(transactionId, userId, amount, category, transactionDate, description, type);
        transactionRepository.save(transaction);
    }

    /**
     * Retrieves all transactions associated with a specific user.
     *
     * @param userId the ID of the user whose transactions are retrieved
     * @return a list of {@link Transaction} objects belonging to the user
     */
    @Override
    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    /**
     * Retrieves a specific transaction by its ID.
     *
     * @param transactionId the ID of the transaction to be retrieved
     * @return the {@link Transaction} object with the specified ID
     */
    @Override
    public Transaction getTransaction(Long transactionId) {
        return transactionRepository.findByTransactionId(transactionId);
    }

    /**
     * Updates an existing transaction for a user.
     *
     * @param transactionId the ID of the transaction to be updated
     * @param userId the ID of the user associated with the transaction
     * @param amount the new amount for the transaction
     * @param category the new category for the transaction
     * @param description the new description for the transaction
     * @return {@code true} if the transaction was successfully updated, {@code false} otherwise
     */
    @Override
    public boolean updateTransaction(Long transactionId, Long userId, BigDecimal amount, String category,
                                     String description) {
        Optional<Transaction> transaction = transactionRepository.findByUserIdAndTransactionId(transactionId, userId);
        if (transaction.isPresent()) {
            Transaction updatedTransaction = transaction.get();
            updatedTransaction.setAmount(amount);
            updatedTransaction.setCategory(category);
            updatedTransaction.setDescription(description);
            transactionRepository.update(updatedTransaction);
            return true;
        }
        return false;
    }

    /**
     * Deletes a transaction for a user.
     *
     * @param userId the ID of the user associated with the transaction to be deleted
     * @param transactionId the ID of the transaction to be deleted
     * @return {@code true} if the transaction was successfully deleted, {@code false} otherwise
     */
    @Override
    public boolean deleteTransaction(Long userId, Long transactionId) {
        Optional<Transaction> transaction = transactionRepository.findByUserIdAndTransactionId(userId, transactionId);
        if (transaction.isPresent()) {
            transactionRepository.delete(transactionId);
            return true;
        }
        return false;
    }

    /**
     * Retrieves a list of transactions for a user that match the specified filtering criteria.
     *
     * @param userId the ID of the user whose transactions are retrieved
     * @param from the start date for filtering the transactions
     * @param to the end date for filtering the transactions
     * @param category the category to filter the transactions by (can be {@code null} to ignore)
     * @param type the type of transactions to filter (either {@link Type#INCOME} or {@link Type#EXPENSE})
     * @return a list of {@link Transaction} objects that match the filtering criteria
     */
    @Override
    public List<Transaction> getFilteredTransactions(Long userId, LocalDate from, LocalDate to, String category,
                                                     Type type) {
        return transactionRepository.findFiltered(userId, from, to, category, type);
    }
}