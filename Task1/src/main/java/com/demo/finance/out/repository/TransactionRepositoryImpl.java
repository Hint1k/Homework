package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Transaction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.time.LocalDate;

public class TransactionRepositoryImpl implements TransactionRepository {

    private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();

    @Override
    public void save(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
    }

    @Override
    public boolean update(Transaction transaction) {
        if (transactions.containsKey(transaction.getId())) {
            transactions.put(transaction.getId(), transaction);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        if (transactions.containsKey(id)) {
            transactions.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }

    @Override
    public Optional<Transaction> findById(String id) {
        return Optional.ofNullable(transactions.get(id));
    }

    @Override
    public List<Transaction> findByUserId(String userId) {
        return transactions.values().stream()
                .filter(t -> t.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findFiltered(String userId, LocalDate from, LocalDate to, String category,
                                          Transaction.Type type) {
        return transactions.values().stream()
                .filter(t -> t.getUserId().equals(userId))
                .filter(t -> from == null || to == null || t.isWithinDateRange(from, to))
                .filter(t -> category == null || t.matchesCategory(category))
                .filter(t -> type == null || t.matchesType(type))
                .collect(Collectors.toList());
    }
}