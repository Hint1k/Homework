package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Budget;
import com.demo.finance.domain.usecase.BudgetUseCase;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

public class BudgetController {
    private final BudgetUseCase budgetUseCase;

    public BudgetController(BudgetUseCase budgetUseCase) {
        this.budgetUseCase = budgetUseCase;
    }

    public boolean setBudget(Long userId, double amount) {
        return budgetUseCase.setMonthlyBudget(userId, amount);
    }

    public String viewBudget(Long userId) {
        // Get the current month and year
        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(now);

        // Retrieve all expense transactions for the current month
        double totalExpenses = budgetUseCase.calculateExpensesForMonth(userId, currentMonth);

        // Retrieve the user's budget
        Optional<Budget> budget = budgetUseCase.getBudget(userId);
        if (budget.isPresent()) {
            double monthlyLimit = budget.get().getMonthlyLimit();
            return String.format("Budget: %.2f/%.2f", totalExpenses, monthlyLimit);
        } else {
            return "No budget set.";
        }
    }
}