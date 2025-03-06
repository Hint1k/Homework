package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.usecase.ManageTransactionsUseCase;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TransactionController {
    private final ManageTransactionsUseCase manageTransactionsUseCase;

    public TransactionController(ManageTransactionsUseCase manageTransactionsUseCase) {
        this.manageTransactionsUseCase = manageTransactionsUseCase;
    }

    public void addTransaction(String id, String userId, double amount, String category, String date,
                               String description, boolean isIncome) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be positive.");
        LocalDate transactionDate = LocalDate.parse(date);
        Transaction.Type type = isIncome ? Transaction.Type.INCOME : Transaction.Type.EXPENSE;

        manageTransactionsUseCase.createTransaction(id, userId, amount, category, transactionDate, description, type);
    }

    public Optional<Transaction> getTransactionById(String id) {
        return manageTransactionsUseCase.getTransactionById(id);
    }

    public List<Transaction> getTransactionsByUserId(String userId) {
        return manageTransactionsUseCase.getTransactionsByUserId(userId);
    }

    public void updateTransaction(String id, double amount, String category, String description) {
        manageTransactionsUseCase.updateTransaction(id, amount, category, description);
    }

    public void deleteTransaction(String id) {
        manageTransactionsUseCase.deleteTransaction(id);
    }
}