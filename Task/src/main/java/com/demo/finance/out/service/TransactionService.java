package com.demo.finance.out.service;

import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.domain.utils.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * The {@code TransactionService} interface defines methods for managing user transactions.
 * This includes creating, updating, deleting, and retrieving transactions, as well as filtering transactions
 * by specific criteria.
 */
public interface TransactionService {

    Long createTransaction(TransactionDto transactionDto);

    Transaction getTransactionByUserIdAndTransactionId(Long userId, Long transactionId);

    /**
     * Retrieves a specific transaction by its ID.
     *
     * @param transactionId the ID of the transaction to be retrieved
     * @return the {@link Transaction} object with the specified ID
     */
    Transaction getTransaction(Long transactionId);

    boolean updateTransaction(TransactionDto dto, Long userId);

    /**
     * Deletes a transaction for a user.
     *
     * @param userId        the ID of the user associated with the transaction to be deleted
     * @param transactionId the ID of the transaction to be deleted
     * @return {@code true} if the transaction was successfully deleted, {@code false} otherwise
     */
    boolean deleteTransaction(Long userId, Long transactionId);


    PaginatedResponse<TransactionDto> getPaginatedTransactionsForUser(Long userId, int page, int size);
}