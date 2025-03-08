package com.demo.finance.out.service;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    void createTransaction(Long userId, double amount, String category, LocalDate date, String description, Type type);

    List<Transaction> getTransactionsByUserId(Long userId);

    boolean updateTransaction(Long transactionId, Long userId, double amount, String category, String description);

    boolean deleteTransaction(Long userId, Long transactionId);

    List<Transaction> getFilteredTransactions(Long userId, LocalDate from, LocalDate to, String category, Type type);
}