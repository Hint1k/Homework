package com.demo.finance.domain.usecase;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.TransactionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ManageTransactionsUseCase {
    private final TransactionRepository transactionRepository;

    public ManageTransactionsUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void createTransaction(Long userId, double amount, String category, LocalDate date,
                                  String description, Type type) {
        Long transactionId = transactionRepository.generateNextId();
        Transaction transaction = new Transaction(transactionId, userId, amount, category, date, description, type);
        transactionRepository.save(transaction);
    }

    public Optional<Transaction> getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId);
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public void updateTransaction(Long transactionId, double amount, String category, String description) {
        transactionRepository.findById(transactionId).ifPresent(transaction -> {
            transaction.setAmount(amount);
            transaction.setCategory(category);
            transaction.setDescription(description);
            transactionRepository.update(transaction);
        });
    }

    public void deleteTransaction(Long transactionId) {
        transactionRepository.delete(transactionId);
    }

    public List<Transaction> getFilteredTransactions(Long userId, LocalDate from, LocalDate to, String category,
                                                     Type type) {
        return transactionRepository.findFiltered(userId, from, to, category, type);
    }
}