package com.demo.finance.in.controller;

import com.demo.finance.out.service.BudgetService;

import java.math.BigDecimal;

/**
 * The {@code BudgetController} class handles incoming requests related to budget management. It provides
 * methods to set a monthly budget for a user and view the current budget.
 */
public class BudgetController {
    private final BudgetService budgetService;

    /**
     * Constructs a {@code BudgetController} with the specified {@code BudgetService}.
     *
     * @param budgetService the {@code BudgetService} used for managing the budget
     */
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    /**
     * Sets the monthly budget for a user identified by the specified {@code userId}.
     *
     * @param userId the ID of the user for whom the budget is to be set
     * @param amount the amount of the budget to be set for the user
     */
    public void setBudget(Long userId, BigDecimal amount) {
        budgetService.setMonthlyBudget(userId, amount);
    }

    /**
     * Retrieves the formatted budget details for a user identified by the specified {@code userId}.
     *
     * @param userId the ID of the user whose budget details are to be retrieved
     * @return a formatted string representing the user's current budget
     */
    public String viewBudget(Long userId) {
        return budgetService.getFormattedBudget(userId);
    }
}