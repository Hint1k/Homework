package com.demo.finance.out.service;

import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.PaginatedResponse;

/**
 * The {@code TransactionService} interface defines the contract for operations related to transaction management.
 * It provides methods for creating, retrieving, updating, deleting, and paginating transactions.
 */
public interface TransactionService {

    /**
     * Creates a new transaction in the system based on the provided transaction data.
     * <p>
     * This method maps the provided {@link TransactionDto} to a {@link Transaction} entity,
     * associates the transaction with the specified user ID, and saves it to the database.
     * The transaction details typically include the amount, category, date, description, and type.
     * <p>
     * Example request payload for creating a transaction:
     *
     * @param transactionDto the {@link TransactionDto} object containing the details of the transaction to create
     * @param userId         the unique identifier of the user associated with the transaction
     * @return the unique identifier ({@code Long}) of the newly created transaction
     * @throws IllegalArgumentException if the provided transaction data is invalid or incomplete
     */
    Long createTransaction(TransactionDto transactionDto, Long userId);

    /**
     * Retrieves a specific transaction associated with a user by their user ID and transaction ID.
     *
     * @param userId         the unique identifier of the user
     * @param transactionId  the unique identifier of the transaction
     * @return the {@link Transaction} object matching the provided user ID and transaction ID
     */
    Transaction getTransactionByUserIdAndTransactionId(Long userId, Long transactionId);

    /**
     * Retrieves a transaction by its unique transaction ID.
     *
     * @param transactionId the unique identifier of the transaction
     * @return the {@link Transaction} object matching the provided transaction ID
     */
    Transaction getTransaction(Long transactionId, Long userId);

    /**
     * Updates an existing transaction in the system based on the provided transaction data.
     *
     * @param dto    the {@link TransactionDto} object containing updated transaction details
     * @param userId the unique identifier of the user who owns the transaction
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    boolean updateTransaction(TransactionDto dto, Long userId);

    /**
     * Deletes a transaction from the system based on the provided user ID and transaction ID.
     *
     * @param userId         the unique identifier of the user
     * @param transactionId  the unique identifier of the transaction
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    boolean deleteTransaction(Long userId, Long transactionId);

    /**
     * Retrieves a paginated list of transactions associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @param page   the page number to retrieve (zero-based index)
     * @param size   the number of transactions to include per page
     * @return a {@link PaginatedResponse} object containing a paginated list of {@link TransactionDto} objects
     */
    PaginatedResponse<TransactionDto> getPaginatedTransactionsForUser(Long userId, int page, int size);
}