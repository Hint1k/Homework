package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Budget;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BudgetRepositoryImpl implements BudgetRepository {
    private final Map<Long, Budget> budgets = new ConcurrentHashMap<>();

    @Override
    public boolean save(Budget budget) {
        budgets.put(budget.getUserId(), budget);
        return true;
    }

    @Override
    public Optional<Budget> findByUserId(Long userId) {
        return Optional.ofNullable(budgets.get(userId));
    }
}