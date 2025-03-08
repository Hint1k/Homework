package com.demo.finance.out.service;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.TransactionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void createTransaction(Long userId, double amount, String category, String date, String description,
                                  Type type) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        LocalDate transactionDate = LocalDate.parse(date);
        Long transactionId = transactionRepository.generateNextId();
        Transaction transaction =
                new Transaction(transactionId, userId, amount, category, transactionDate, description, type);
        transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    @Override
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

    @Override
    public boolean deleteTransaction(Long userId, Long transactionId) {
        Optional<Transaction> transaction = transactionRepository.findByUserIdAndTransactionId(userId, transactionId);
        if (transaction.isPresent()) {
            transactionRepository.delete(transactionId);
            return true;
        }
        return false;
    }

    @Override
    public List<Transaction> getFilteredTransactions(Long userId, LocalDate from, LocalDate to, String category,
                                                     Type type) {
        return transactionRepository.findFiltered(userId, from, to, category, type);
    }
}