package com.demo.finance.out.service;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.TransactionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void createTransaction(Long userId, double amount, String category, LocalDate date,
                                  String description, Type type) {
        Long transactionId = transactionRepository.generateNextId();
        Transaction transaction = new Transaction(transactionId, userId, amount, category, date, description, type);
        transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    public boolean updateTransaction(Long transactionId, Long userId, double amount, String category,
                                     String description) {
        Optional<Transaction> transaction = transactionRepository.findByUserIdAndTransactionId(transactionId, userId);
        if (transaction.isPresent()) {
            Transaction updatedTransaction = transaction.get();
            updatedTransaction.setAmount(amount);
            updatedTransaction.setCategory(category);
            updatedTransaction.setDescription(description);
            transactionRepository.update(updatedTransaction);
            return true;
        }
        return false;
    }

    public boolean deleteTransaction(Long userId, Long transactionId) {
        Optional<Transaction> transaction = transactionRepository.findByUserIdAndTransactionId(userId, transactionId);
        if (transaction.isPresent()) {
            transactionRepository.delete(transactionId);
            return true;
        }
        return false;
    }

    public List<Transaction> getFilteredTransactions(Long userId, LocalDate from, LocalDate to, String category,
                                                     Type type) {
        return transactionRepository.findFiltered(userId, from, to, category, type);
    }
}