package com.demo.finance.out.service;

import com.demo.finance.domain.model.Budget;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

/**
 * The {@code BudgetService} interface defines the contract for operations related to budget management.
 * It provides methods for setting, retrieving, and analyzing budget-related data for users.
 */
public interface BudgetService {

    /**
     * Sets a monthly budget limit for a specific user.
     *
     * @param userId the unique identifier of the user
     * @param limit  the maximum amount ({@link BigDecimal}) allowed for the user's monthly budget
     * @return a {@link Budget} object representing the newly set monthly budget
     */
    Budget setMonthlyBudget(Long userId, BigDecimal limit);

    /**
     * Retrieves the current budget information for a specific user.
     *
     * @param userId the unique identifier of the user
     * @return a {@link Budget} object containing the user's budget details
     */
    Budget getBudget(Long userId);

    /**
     * Calculates the total expenses incurred by a user for a specific month.
     *
     * @param userId      the unique identifier of the user
     * @param currentMonth the month and year ({@link YearMonth}) for which expenses are calculated
     * @return the total expenses as a {@link BigDecimal} value for the specified month
     */
    BigDecimal calculateExpensesForMonth(Long userId, YearMonth currentMonth);

    /**
     * Retrieves a comprehensive set of budget-related data for a specific user.
     *
     * @param userId the unique identifier of the user
     * @return a {@link Map} containing key-value pairs representing various budget-related data points
     */
    Map<String, Object> getBudgetData(Long userId);
}