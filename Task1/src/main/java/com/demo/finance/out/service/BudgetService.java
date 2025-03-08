package com.demo.finance.out.service;

import com.demo.finance.domain.model.Budget;

import java.time.YearMonth;
import java.util.Optional;

public interface BudgetService {

    boolean setMonthlyBudget(Long userId, double limit);

    Optional<Budget> getBudget(Long userId);

    double calculateExpensesForMonth(Long userId, YearMonth currentMonth);

    String getFormattedBudget(Long userId);
}