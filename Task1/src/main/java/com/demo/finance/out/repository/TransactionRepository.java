package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    void save(Transaction transaction);

    boolean update(Transaction transaction);

    Optional<Transaction> findById(String id);

    List<Transaction> findByUserId(String userId);

    List<Transaction> findAll();

    boolean delete(String id);

    List<Transaction> findFiltered(String userId, LocalDate from, LocalDate to, String category,
                                   Transaction.Type type);
}