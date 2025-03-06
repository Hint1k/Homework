package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Budget;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BudgetRepositoryImpl implements BudgetRepository {
    private final Map<String, Budget> budgets = new ConcurrentHashMap<>();

    @Override
    public void save(Budget budget) {
        budgets.put(budget.getUserId(), budget);
    }

    @Override
    public Optional<Budget> findByUserId(String userId) {
        return Optional.ofNullable(budgets.get(userId));
    }
}