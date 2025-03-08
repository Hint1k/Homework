package com.demo.finance.in.controller;

import com.demo.finance.out.service.BudgetService;

public class BudgetController {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    public boolean setBudget(Long userId, double amount) {
        return budgetService.setMonthlyBudget(userId, amount);
    }

    public String viewBudget(Long userId) {
        return budgetService.getFormattedBudget(userId);
    }
}