package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.time.LocalDate;

public class TransactionRepositoryImpl implements TransactionRepository {

    private final Map<Long, Transaction> transactions = new ConcurrentHashMap<>();

    @Override
    public void save(Transaction transaction) {
        transactions.put(transaction.getTransactionId(), transaction);
    }

    @Override
    public boolean update(Transaction transaction) {
        if (transactions.containsKey(transaction.getTransactionId())) {
            transactions.put(transaction.getTransactionId(), transaction);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Long transactionId) {
        if (transactions.containsKey(transactionId)) {
            transactions.remove(transactionId);
            return true;
        }
        return false;
    }

    @Override
    public Transaction findByTransactionId(Long transactionId) {
        if (transactionId == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null.");
        }
        return transactions.get(transactionId);
    }

    @Override
    public List<Transaction> findByUserId(Long userId) {
        return transactions.values().stream()
                .filter(t -> t.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findFiltered(Long userId, LocalDate from, LocalDate to, String category, Type type) {
        return transactions.values().stream()
                .filter(t -> t.getUserId().equals(userId)) // Always filter by ID, other filters - optional
                .filter(t -> from == null || !t.getDate().isBefore(from))
                .filter(t -> to == null || !t.getDate().isAfter(to))
                .filter(t -> category == null || t.getCategory().equalsIgnoreCase(category))
                .filter(t -> type == null || t.getType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public Long generateNextId() {
        return transactions.keySet().stream().max(Long::compare).orElse(0L) + 1;
    }

    @Override
    public Optional<Transaction> findByUserIdAndTransactionId(Long transactionId, Long userId) {
        return transactions.values().stream()
                .filter(t -> t.getTransactionId().equals(transactionId) && t.getUserId().equals(userId))
                .findFirst();
    }
}