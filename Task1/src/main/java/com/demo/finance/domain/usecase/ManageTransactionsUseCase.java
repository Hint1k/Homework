package com.demo.finance.domain.usecase;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.out.repository.TransactionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ManageTransactionsUseCase {
    private final TransactionRepository transactionRepository;

    public ManageTransactionsUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void createTransaction(String id, String userId, double amount, String category, LocalDate date,
                                  String description, Transaction.Type type) {
        Transaction transaction = new Transaction(id, userId, amount, category, date, description, type);
        transactionRepository.save(transaction);
    }

    public Optional<Transaction> getTransactionById(String id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> getTransactionsByUserId(String userId) {
        return transactionRepository.findByUserId(userId);
    }

    public void updateTransaction(String id, double amount, String category, String description) {
        transactionRepository.findById(id).ifPresent(transaction -> {
            transaction.setAmount(amount);
            transaction.setCategory(category);
            transaction.setDescription(description);
            transactionRepository.update(transaction);
        });
    }

    public void deleteTransaction(String id) {
        transactionRepository.delete(id);
    }

    public List<Transaction> getFilteredTransactions(String userId, LocalDate from, LocalDate to, String category,
                                                     Transaction.Type type) {
        return transactionRepository.findFiltered(userId, from, to, category, type);
    }
}