package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Budget;
import com.demo.finance.domain.usecase.ManageBudgetUseCase;

import java.util.Optional;

public class BudgetController {
    private final ManageBudgetUseCase manageBudgetUseCase;

    public BudgetController(ManageBudgetUseCase manageBudgetUseCase) {
        this.manageBudgetUseCase = manageBudgetUseCase;
    }

    public boolean setBudget(Long userId, double amount) {
        return manageBudgetUseCase.setMonthlyBudget(userId, amount);
    }

    public Optional<Budget> getBudget(Long userId) {
        return manageBudgetUseCase.getBudget(userId);
    }

    public boolean checkBudgetLimit(Long userId, double transactionAmount) {
        return manageBudgetUseCase.isBudgetExceeded(userId, transactionAmount);
    }

    public void recordExpense(Long userId, double transactionAmount) {
        manageBudgetUseCase.trackExpense(userId, transactionAmount);
    }
}