package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Budget;
import com.demo.finance.domain.usecase.BudgetUseCase;

import java.util.Optional;

public class BudgetController {
    private final BudgetUseCase budgetUseCase;

    public BudgetController(BudgetUseCase budgetUseCase) {
        this.budgetUseCase = budgetUseCase;
    }

    public boolean setBudget(Long userId, double amount) {
        return budgetUseCase.setMonthlyBudget(userId, amount);
    }

    public Optional<Budget> getBudget(Long userId) {
        return budgetUseCase.getBudget(userId);
    }

    public boolean checkBudgetLimit(Long userId, double transactionAmount) {
        return budgetUseCase.isBudgetExceeded(userId, transactionAmount);
    }

    public void recordExpense(Long userId, double transactionAmount) {
        budgetUseCase.trackExpense(userId, transactionAmount);
    }
}