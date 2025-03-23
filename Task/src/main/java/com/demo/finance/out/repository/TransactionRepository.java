package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository {

    Long save(Transaction transaction);

    boolean update(Transaction transaction);

    boolean delete(Long transactionId);

    Transaction findById(Long transactionId);

    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByUserId(Long userId, int offset, int size);

    List<Transaction> findFiltered(Long userId, LocalDate from, LocalDate to, String category, Type type);

    Transaction findByUserIdAndTransactionId(Long userId, Long transactionId);

    int getTotalTransactionCountForUser(Long userId);
}