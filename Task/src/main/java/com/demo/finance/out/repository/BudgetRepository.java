package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Budget;

public interface BudgetRepository {

    boolean save(Budget budget);

    boolean delete(Long budgetId);

    boolean update(Budget updatedBudget);

    Budget findById(Long budgetId);

    Budget findByUserId(Long userId);
}