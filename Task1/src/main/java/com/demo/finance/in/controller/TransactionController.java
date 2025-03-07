package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.usecase.TransactionsUseCase;
import com.demo.finance.domain.utils.Type;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TransactionController {
    private final TransactionsUseCase transactionsUseCase;

    public TransactionController(TransactionsUseCase transactionsUseCase) {
        this.transactionsUseCase = transactionsUseCase;
    }

    public void addTransaction(Long userId, double amount, String category, String date,
                               String description, Type type) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be positive.");
        LocalDate transactionDate = LocalDate.parse(date);

        transactionsUseCase.createTransaction(userId, amount, category, transactionDate, description, type);
    }

    public Optional<Transaction> getTransactionById(Long transactionId) {
        return transactionsUseCase.getTransactionById(transactionId);
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionsUseCase.getTransactionsByUserId(userId);
    }

    public List<Transaction> getAllTransactions() {
        return transactionsUseCase.getAllTransactions();
    }

    public boolean updateTransaction(Long transactionId, Long userId, double amount, String category,
                                     String description) {
        return transactionsUseCase.updateTransaction(transactionId, userId, amount, category, description);
    }

    public boolean deleteTransaction(Long userId, Long transactionId) {
        return transactionsUseCase.deleteTransaction(userId, transactionId);
    }

    public List<Transaction> filterTransactions(Long userId, String from, String to, String category, Type type) {
        LocalDate fromDate = from.isEmpty() ? null : LocalDate.parse(from);
        LocalDate toDate = to.isEmpty() ? null : LocalDate.parse(to);
        return transactionsUseCase.getFilteredTransactions(userId, fromDate, toDate, category, type);
    }
}