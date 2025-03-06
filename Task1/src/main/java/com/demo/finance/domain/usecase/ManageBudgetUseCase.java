package com.demo.finance.domain.usecase;

import com.demo.finance.domain.model.Budget;
import com.demo.finance.out.repository.BudgetRepository;

import java.util.Optional;

public class ManageBudgetUseCase {
    private final BudgetRepository budgetRepository;

    public ManageBudgetUseCase(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public boolean setMonthlyBudget(String userId, double limit) {
        budgetRepository.save(new Budget(userId, limit));
        return true; // Indicate success
    }

    public Optional<Budget> getBudget(String userId) {
        return budgetRepository.findByUserId(userId);
    }

    public void trackExpense(String userId, double amount) {
        budgetRepository.findByUserId(userId).ifPresent(budget -> budget.addExpense(amount));
    }

    public boolean isBudgetExceeded(String userId, double transactionAmount) {
        return budgetRepository.findByUserId(userId)
                .map(budget -> budget.getCurrentExpenses() + transactionAmount > budget.getMonthlyLimit())
                .orElse(false);
    }
}