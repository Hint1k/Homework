package com.demo.finance.domain.model;

import java.util.Objects;

/**
 * Represents a budget for a user, including the monthly limit and current expenses.
 * This class allows for tracking and modifying a user's budget details.
 */
public class Budget {

    private final Long userId; // Associated user
    private double monthlyLimit;
    private double currentExpenses;

    /**
     * Constructs a new Budget for a user with a specified monthly limit.
     *
     * @param userId The ID of the user this budget is associated with.
     * @param monthlyLimit The maximum amount the user can spend in a month.
     */
    public Budget(Long userId, double monthlyLimit) {
        this.userId = userId;
        this.monthlyLimit = monthlyLimit;
        this.currentExpenses = 0.0;
    }

    /**
     * Gets the ID of the user associated with this budget.
     *
     * @return The user ID.
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Gets the monthly spending limit of the budget.
     *
     * @return The monthly limit.
     */
    public double getMonthlyLimit() {
        return monthlyLimit;
    }

    /**
     * Sets a new monthly spending limit for the budget.
     *
     * @param monthlyLimit The new monthly limit.
     */
    public void setMonthlyLimit(double monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    /**
     * Gets the current expenses of the budget.
     *
     * @return The current expenses.
     */
    public double getCurrentExpenses() {
        return currentExpenses;
    }

    /**
     * Sets the current expenses of the budget.
     *
     * @param currentExpenses The new current expenses.
     */
    public void setCurrentExpenses(double currentExpenses) {
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
        return Double.compare(monthlyLimit, budget.monthlyLimit) == 0
                && Double.compare(currentExpenses, budget.currentExpenses) == 0
                && Objects.equals(userId, budget.userId);
    }

    /**
     * Generates a hash code for this budget. The hash code is based on the user ID, monthly limit,
     * and current expenses.
     *
     * @return A hash code for this budget.
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, monthlyLimit, currentExpenses);
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