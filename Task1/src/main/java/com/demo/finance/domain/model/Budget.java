package com.demo.finance.domain.model;

import java.util.Objects;

public class Budget {

    private final String userId; // Associated user
    private double monthlyLimit;
    private double currentExpenses;

    public Budget(String userId, double monthlyLimit) {
        this.userId = userId;
        this.monthlyLimit = monthlyLimit;
        this.currentExpenses = 0.0;
    }

    public String getUserId() {
        return userId;
    }

    public double getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(double monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public double getCurrentExpenses() {
        return currentExpenses;
    }

    public void addExpense(double amount) {
        this.currentExpenses += amount;
    }

    public boolean isExceeded() {
        return currentExpenses > monthlyLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Budget budget = (Budget) o;
        return Double.compare(monthlyLimit, budget.monthlyLimit) == 0
                && Double.compare(currentExpenses, budget.currentExpenses) == 0
                && Objects.equals(userId, budget.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, monthlyLimit, currentExpenses);
    }

    @Override
    public String toString() {
        return "Budget: " + currentExpenses + "/" + monthlyLimit;
    }
}