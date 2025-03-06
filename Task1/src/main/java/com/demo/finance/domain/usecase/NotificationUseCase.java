package com.demo.finance.domain.usecase;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.out.repository.BudgetRepository;
import com.demo.finance.out.repository.GoalRepository;
import com.demo.finance.domain.model.Budget;

import java.util.List;
import java.util.Optional;

public class NotificationUseCase {
    private final BudgetRepository budgetRepository;
    private final GoalRepository goalRepository;

    public NotificationUseCase(BudgetRepository budgetRepository, GoalRepository goalRepository) {
        this.budgetRepository = budgetRepository;
        this.goalRepository = goalRepository;
    }

    public String getBudgetLimitNotification(Long userId) {
        Optional<Budget> budgetOpt = budgetRepository.findByUserId(userId);
        if (budgetOpt.isEmpty()) {
            return "No budget set for user.";
        }

        Budget budget = budgetOpt.get();
        return budget.isExceeded()
                ? "⚠️ Warning: Budget limit exceeded! You have spent " + budget.getCurrentExpenses() +
                " out of " + budget.getMonthlyLimit() + "."
                : "✅ Budget is under control. Remaining budget: " +
                (budget.getMonthlyLimit() - budget.getCurrentExpenses()) + ".";
    }

    public String getGoalCompletionNotification(Long userId) {
        List<Goal> userGoals = goalRepository.findByUserId(userId);
        if (userGoals.isEmpty()) {
            return "No goals set.";
        }

        StringBuilder notification = new StringBuilder();
        for (Goal goal : userGoals) {
            if (goal.isAchieved()) {
                notification.append("🎉 Goal achieved: '").append(goal.getGoalName())
                        .append("'! Target: ").append(goal.getTargetAmount())
                        .append(", Saved: ").append(goal.getSavedAmount()).append("\n");
            } else {
                notification.append("⏳ Goal '").append(goal.getGoalName())
                        .append("' progress: ").append(goal.getSavedAmount())
                        .append(" / ").append(goal.getTargetAmount()).append("\n");
            }
        }

        return notification.toString().trim();
    }
}