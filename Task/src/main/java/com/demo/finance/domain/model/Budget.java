package com.demo.finance.domain.model;

import com.demo.finance.domain.utils.GeneratedKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a budget for a user, including the monthly limit and current expenses.
 * This class allows for tracking and modifying a user's budget details.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Budget {

    @GeneratedKey
    private Long budgetId;
    private Long userId;
    private BigDecimal monthlyLimit;
    private BigDecimal currentExpenses;

    /**
     * Constructs a new {@code Budget} object with the specified user ID and monthly limit.
     * This constructor initializes the current expenses to zero and is typically used when
     * creating a new budget without an initially assigned budget ID.
     *
     * @param userId        the ID of the user associated with the budget
     * @param monthlyLimit  the monthly spending limit for the budget
     */
    public Budget(Long userId, BigDecimal monthlyLimit) {
        this.userId = userId;
        this.monthlyLimit = monthlyLimit;
        this.currentExpenses = new BigDecimal(0);
    }

    /**
     * Compares this budget to another object for equality. Two budgets are considered equal if
     * their user ID, monthly limit, and current expenses are the same.
     *
     * @param o The object to compare to.
     * @return {@code true} if this budget is equal to the provided object, otherwise {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Budget budget = (Budget) o;
        return Objects.equals(budgetId, budget.budgetId) && Objects.equals(userId, budget.userId)
                && Objects.equals(monthlyLimit, budget.monthlyLimit)
                && Objects.equals(currentExpenses, budget.currentExpenses);
    }

    /**
     * Generates a hash code for this budget. The hash code is based on the user ID, monthly limit,
     * and current expenses.
     *
     * @return A hash code for this budget.
     */
    @Override
    public int hashCode() {
        return Objects.hash(budgetId, userId, monthlyLimit, currentExpenses);
    }

    /**
     * Returns a string representation of the budget, including the current expenses and the monthly limit.
     *
     * @return A string representation of the budget.
     */
    @Override
    public String toString() {
        return "Budget: " + currentExpenses + "/" + monthlyLimit;
    }
}