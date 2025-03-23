package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.mapper.TransactionMapper;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.out.repository.TransactionRepository;
import com.demo.finance.out.service.TransactionService;

import java.util.List;
import java.util.logging.Logger;

/**
 * The {@code TransactionServiceImpl} class implements the {@link TransactionService} interface
 * and provides concrete implementations for transaction-related operations.
 * It interacts with the database through the {@link TransactionRepository} and handles logic for creating,
 * retrieving, updating, deleting, and paginating transactions.
 */
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    /**
     * Logger instance for logging events and errors in the {@code TransactionServiceImpl} class.
     */
    protected static final Logger log = Logger.getLogger(TransactionServiceImpl.class.getName());

    /**
     * Constructs a new instance of {@code TransactionServiceImpl} with the provided repository.
     *
     * @param transactionRepository the repository used to interact with transaction data in the database
     */
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Creates a new transaction in the system based on the provided transaction data.
     *
     * @param dto the {@link TransactionDto} object containing the details of the transaction to create
     * @return the unique identifier ({@code Long}) of the newly created transaction
     */
    @Override
    public Long createTransaction(TransactionDto dto) {
        Transaction transaction = TransactionMapper.INSTANCE.toEntity(dto);
        return transactionRepository.save(transaction);
    }

    /**
     * Retrieves a specific transaction associated with a user by their user ID and transaction ID.
     *
     * @param userId        the unique identifier of the user
     * @param transactionId the unique identifier of the transaction
     * @return the {@link Transaction} object matching the provided user ID and transaction ID, or {@code null} if not found
     */
    @Override
    public Transaction getTransactionByUserIdAndTransactionId(Long userId, Long transactionId) {
        return transactionRepository.findByUserIdAndTransactionId(userId, transactionId);
    }

    /**
     * Retrieves a transaction by its unique transaction ID.
     *
     * @param transactionId the unique identifier of the transaction
     * @return the {@link Transaction} object matching the provided transaction ID, or {@code null} if not found
     */
    @Override
    public Transaction getTransaction(Long transactionId) {
        return transactionRepository.findById(transactionId);
    }

    /**
     * Updates an existing transaction in the system based on the provided transaction data.
     *
     * @param dto    the {@link TransactionDto} object containing updated transaction details
     * @param userId the unique identifier of the user who owns the transaction
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    @Override
    public boolean updateTransaction(TransactionDto dto, Long userId) {
        Long transactionId = dto.getTransactionId();
        Transaction transaction = transactionRepository.findByUserIdAndTransactionId(userId, transactionId);
        if (transaction != null) {
            transaction.setAmount(dto.getAmount());
            transaction.setCategory(dto.getCategory());
            transaction.setDescription(dto.getDescription());
            transactionRepository.update(transaction);
            return true;
        }
        return false;
    }

    /**
     * Deletes a transaction from the system based on the provided user ID and transaction ID.
     *
     * @param userId        the unique identifier of the user
     * @param transactionId the unique identifier of the transaction
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    @Override
    public boolean deleteTransaction(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findByUserIdAndTransactionId(userId, transactionId);
        if (transaction != null) {
            return transactionRepository.delete(transactionId);
        }
        return false;
    }

    /**
     * Retrieves a paginated list of transactions associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @param page   the page number to retrieve (one-based index)
     * @param size   the number of transactions to include per page
     * @return a {@link PaginatedResponse} object containing a paginated list of {@link TransactionDto} objects
     */
    @Override
    public PaginatedResponse<TransactionDto> getPaginatedTransactionsForUser(Long userId, int page, int size) {
        int offset = (page - 1) * size;
        List<Transaction> transactions = transactionRepository.findByUserId(userId, offset, size);
        int totalTransactions = transactionRepository.getTotalTransactionCountForUser(userId);
        List<TransactionDto> dtoList = transactions.stream().map(TransactionMapper.INSTANCE::toDto).toList();
        return new PaginatedResponse<>(dtoList, totalTransactions, (int) Math.ceil((double) totalTransactions / size),
                page, size);
    }
}