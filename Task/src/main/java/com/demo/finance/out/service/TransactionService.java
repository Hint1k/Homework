package com.demo.finance.out.service;

import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.PaginatedResponse;

public interface TransactionService {

    Long createTransaction(TransactionDto transactionDto);

    Transaction getTransactionByUserIdAndTransactionId(Long userId, Long transactionId);

    Transaction getTransaction(Long transactionId);

    boolean updateTransaction(TransactionDto dto, Long userId);

    boolean deleteTransaction(Long userId, Long transactionId);

    PaginatedResponse<TransactionDto> getPaginatedTransactionsForUser(Long userId, int page, int size);
}