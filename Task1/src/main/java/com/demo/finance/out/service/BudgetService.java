package com.demo.finance.out.service;

import com.demo.finance.domain.model.Budget;

import java.time.YearMonth;
import java.util.Optional;

/**
 * {@code BudgetService} defines the operations related to managing a user's budget.
 * It provides methods for setting a monthly budget, retrieving the budget, calculating
 * expenses for a given month, and formatting the budget for display.
 */
public interface BudgetService {

    /**
     * Sets the monthly budget for a user.
     *
     * @param userId the ID of the user to set the budget for
     * @param limit the budget limit for the month
     * @return {@code true} if the budget was successfully set, {@code false} otherwise
     */
    boolean setMonthlyBudget(Long userId, double limit);

    /**
     * Retrieves the current budget for a user.
     *
     * @param userId the ID of the user whose budget is to be retrieved
     * @return an {@code Optional<Budget>} containing the user's budget, or an empty optional if no budget is set
     */
    Optional<Budget> getBudget(Long userId);

    /**
     * Calculates the total expenses for a user for a given month.
     *
     * @param userId the ID of the user whose expenses are to be calculated
     * @param currentMonth the month for which the expenses are to be calculated
     * @return the total expenses for the specified month
     */
    double calculateExpensesForMonth(Long userId, YearMonth currentMonth);

    /**
     * Retrieves the formatted string representation of a user's budget.
     *
     * @param userId the ID of the user whose budget is to be formatted
     * @return a string representing the user's budget in a readable format
     */
    String getFormattedBudget(Long userId);
}