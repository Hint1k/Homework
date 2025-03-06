package com.demo.finance.domain.model;

import java.util.Objects;

public class Report {

    private final String userId;
    private final double totalIncome;
    private final double totalExpense;
    private final double balance;

    public Report(String userId, double totalIncome, double totalExpense) {
        this.userId = userId;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = totalIncome - totalExpense;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Double.compare(totalIncome, report.totalIncome) == 0
                && Double.compare(totalExpense, report.totalExpense) == 0
                && Double.compare(balance, report.balance) == 0 && Objects.equals(userId, report.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, totalIncome, totalExpense, balance);
    }

    @Override
    public String toString() {
        return "Report: Income=" + totalIncome + ", Expense=" + totalExpense + ", Balance=" + balance;
    }
}