package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    void save(Transaction transaction);

    boolean update(Transaction transaction);

    List<Transaction> findByUserId(Long userId);

    Transaction findByTransactionId(Long transactionId);

    boolean delete(Long transactionId);

    List<Transaction> findFiltered(Long userId, LocalDate from, LocalDate to, String category, Type type);

    Long generateNextId();

    Optional<Transaction> findByUserIdAndTransactionId(Long userId, Long transactionId);
}