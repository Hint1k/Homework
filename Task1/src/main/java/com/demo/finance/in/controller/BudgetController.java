package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Budget;
import com.demo.finance.out.service.BudgetService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

public class BudgetController {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    public boolean setBudget(Long userId, double amount) {
        return budgetService.setMonthlyBudget(userId, amount);
    }

    public String viewBudget(Long userId) {
        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(now);
        double totalExpenses = budgetService.calculateExpensesForMonth(userId, currentMonth);
        Optional<Budget> budget = budgetService.getBudget(userId);
        if (budget.isPresent()) {
            double monthlyLimit = budget.get().getMonthlyLimit();
            return String.format("Budget: %.2f/%.2f", totalExpenses, monthlyLimit);
        } else {
            return "No budget set.";
        }
    }
}