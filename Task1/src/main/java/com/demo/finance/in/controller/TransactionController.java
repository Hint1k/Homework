package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.out.service.TransactionService;
import com.demo.finance.domain.utils.Type;

import java.time.LocalDate;
import java.util.List;

public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void addTransaction(Long userId, double amount, String category, String date, String description,
                               Type type) {
        transactionService.createTransaction(userId, amount, category, date, description, type);
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionService.getTransactionsByUserId(userId);
    }

    public boolean updateTransaction(Long transactionId, Long userId, double amount, String category,
                                     String description) {
        return transactionService.updateTransaction(transactionId, userId, amount, category, description);
    }

    public boolean deleteTransaction(Long userId, Long transactionId) {
        return transactionService.deleteTransaction(userId, transactionId);
    }

    public List<Transaction> filterTransactions(Long userId, LocalDate from, LocalDate to, String category,
                                                Type type) {
        return transactionService.getFilteredTransactions(userId, from, to, category, type);
    }
}