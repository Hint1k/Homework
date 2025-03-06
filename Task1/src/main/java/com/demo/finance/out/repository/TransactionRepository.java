package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    void save(Transaction transaction);

    boolean update(Transaction transaction);

    Optional<Transaction> findById(Long transactionId);

    List<Transaction> findByUserId(Long userId);

    List<Transaction> findAll();

    boolean delete(Long transactionId);

    List<Transaction> findFiltered(Long userId, LocalDate from, LocalDate to, String category, Type type);
}