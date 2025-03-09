package com.demo.finance.domain.model;

import java.util.Objects;

/**
 * Represents a financial report for a user, including the total income, total expenses,
 * and the resulting balance (income - expenses).
 */
public class Report {

    private final Long userId;
    private final double totalIncome;
    private final double totalExpense;
    private final double balance;

    /**
     * Constructs a new Report for a user with the specified total income and total expenses.
     * The balance is calculated as the difference between total income and total expenses.
     *
     * @param userId The ID of the user this report is associated with.
     * @param totalIncome The total income for the user.
     * @param totalExpense The total expenses for the user.
     */
    public Report(Long userId, double totalIncome, double totalExpense) {
        this.userId = userId;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = totalIncome - totalExpense;
    }

    /**
     * Compares this report to another object for equality. Two reports are considered equal if
     * their user ID, total income, total expense, and balance are the same.
     *
     * @param o The object to compare to.
     * @return {@code true} if this report is equal to the provided object, otherwise {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Double.compare(totalIncome, report.totalIncome) == 0
                && Double.compare(totalExpense, report.totalExpense) == 0
                && Double.compare(balance, report.balance) == 0 && Objects.equals(userId, report.userId);
    }

    /**
     * Generates a hash code for this report. The hash code is based on the user ID, total income,
     * total expense, and balance.
     *
     * @return A hash code for this report.
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, totalIncome, totalExpense, balance);
    }

    /**
     * Returns a string representation of the report, including total income, total expense,
     * and the resulting balance.
     *
     * @return A string representation of the report.
     */
    @Override
    public String toString() {
        return "Report: Income=" + totalIncome + ", Expense=" + totalExpense + ", Balance=" + balance;
    }
}