package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.time.LocalDate;

public class TransactionRepositoryImpl implements TransactionRepository {

    private final Map<Long, Transaction> transactions = new ConcurrentHashMap<>();

    public TransactionRepositoryImpl() {
        // Initialize default transactions
        initializeDefaultTransactions();
    }

    private void initializeDefaultTransactions() {
        // Add 10 default transactions
        save(new Transaction(1L, 2L, 500.0, "Salary", LocalDate.of(2025, 3, 1), "Monthly salary", Type.INCOME));
        save(new Transaction(2L, 2L, 200.0, "Freelance", LocalDate.of(2025, 3, 2), "Project payment", Type.INCOME));
        save(new Transaction(3L, 2L, 50.0, "Food", LocalDate.of(2025, 3, 3), "Dinner with friends", Type.EXPENSE));
        save(new Transaction(4L, 2L, 30.0, "Transport", LocalDate.of(2025, 3, 4), "Bus fare", Type.EXPENSE));
        save(new Transaction(5L, 2L, 100.0, "Shopping", LocalDate.of(2025, 3, 5), "Clothes purchase", Type.EXPENSE));
        save(new Transaction(6L, 2L, 1000.0, "Bonus", LocalDate.of(2025, 4, 1), "Year-end bonus", Type.INCOME));
        save(new Transaction(7L, 2L, 200.0, "Rent", LocalDate.of(2025, 4, 2), "Monthly rent", Type.EXPENSE));
        save(new Transaction(8L, 2L, 150.0, "Utilities", LocalDate.of(2025, 4, 3), "Electricity bill", Type.EXPENSE));
        save(new Transaction(9L, 2L, 75.0, "Entertainment", LocalDate.of(2025, 4, 4), "Movie tickets", Type.EXPENSE));
        save(new Transaction(10L, 2L, 400.0, "Investment", LocalDate.of(2025, 4, 5), "Stock purchase", Type.EXPENSE));
    }

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
    public Optional<Transaction> findByUserIdAndId(Long userId, Long transactionId) {
        return transactions.values().stream()
                .filter(t -> t.getUserId().equals(userId) && t.getTransactionId().equals(transactionId))
                .findFirst();
    }
}