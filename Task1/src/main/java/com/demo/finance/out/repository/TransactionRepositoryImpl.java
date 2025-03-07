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
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }

    @Override
    public Optional<Transaction> findById(Long transactionId) {
        return Optional.ofNullable(transactions.get(transactionId));
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
                .filter(t -> t.getUserId().equals(userId))
                .filter(t -> from == null || to == null || t.isWithinDateRange(from, to))
                .filter(t -> category == null || t.matchesCategory(category))
                .filter(t -> type == null || t.matchesType(type))
                .collect(Collectors.toList());
    }

    @Override
    public Long generateNextId() {
        return transactions.keySet().stream().max(Long::compare).orElse(0L) + 1;
    }
}