package com.demo.finance.out.service;

import com.demo.finance.domain.model.Budget;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;
import java.util.Optional;

/**
 * {@code BudgetService} defines the operations related to managing a user's budget.
 * It provides methods for setting a monthly budget, retrieving the budget, calculating
 * expenses for a given month, and formatting the budget for display.
 */
public interface BudgetService {

    Budget setMonthlyBudget(Long userId, BigDecimal limit);

    Budget getBudget(Long userId);

    /**
     * Calculates the total expenses for a user for a given month.
     *
     * @param userId the ID of the user whose expenses are to be calculated
     * @param currentMonth the month for which the expenses are to be calculated
     * @return the total expenses for the specified month
     */
    BigDecimal calculateExpensesForMonth(Long userId, YearMonth currentMonth);

    Map<String, Object> getBudgetData(Long userId);
}