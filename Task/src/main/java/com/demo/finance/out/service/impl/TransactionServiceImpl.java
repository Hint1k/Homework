package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.mapper.TransactionMapper;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.out.repository.TransactionRepository;
import com.demo.finance.out.service.TransactionService;

import java.util.List;
import java.util.logging.Logger;

public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    protected static final Logger log = Logger.getLogger(TransactionServiceImpl.class.getName());

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Long createTransaction(TransactionDto dto) {
        Transaction transaction = TransactionMapper.INSTANCE.toEntity(dto);
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction getTransactionByUserIdAndTransactionId(Long userId, Long transactionId) {
        return transactionRepository.findByUserIdAndTransactionId(userId, transactionId);
    }

    @Override
    public Transaction getTransaction(Long transactionId) {
        return transactionRepository.findById(transactionId);
    }

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

    @Override
    public boolean deleteTransaction(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findByUserIdAndTransactionId(userId, transactionId);
        if (transaction != null) {
            return transactionRepository.delete(transactionId);
        }
        return false;
    }

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