package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.usecase.ManageTransactionsUseCase;
import com.demo.finance.domain.utils.Type;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TransactionController {
    private final ManageTransactionsUseCase manageTransactionsUseCase;

    public TransactionController(ManageTransactionsUseCase manageTransactionsUseCase) {
        this.manageTransactionsUseCase = manageTransactionsUseCase;
    }

    public void addTransaction(Long transactionId, Long userId, double amount, String category, String date,
                               String description, boolean isIncome) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be positive.");
        LocalDate transactionDate = LocalDate.parse(date);
        Type type = isIncome ? Type.INCOME : Type.EXPENSE;

        manageTransactionsUseCase
                .createTransaction(transactionId, userId, amount, category, transactionDate, description, type);
    }

    public Optional<Transaction> getTransactionById(Long transactionId) {
        return manageTransactionsUseCase.getTransactionById(transactionId);
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return manageTransactionsUseCase.getTransactionsByUserId(userId);
    }

    public void updateTransaction(Long transactionId, double amount, String category, String description) {
        manageTransactionsUseCase.updateTransaction(transactionId, amount, category, description);
    }

    public void deleteTransaction(Long transactionId) {
        manageTransactionsUseCase.deleteTransaction(transactionId);
    }

    public List<Transaction> filterTransactions(Long userId, String from, String to, String category, String type) {
        LocalDate fromDate = from.isEmpty() ? null : LocalDate.parse(from);
        LocalDate toDate = to.isEmpty() ? null : LocalDate.parse(to);
        Type transactionType = type.isEmpty() ? null : Type.valueOf(type.toUpperCase());

        return manageTransactionsUseCase.getFilteredTransactions(userId, fromDate, toDate, category, transactionType);
    }
}