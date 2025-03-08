package com.demo.finance.out.service;

import com.demo.finance.domain.utils.MockEmailUtils;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.BalanceUtils;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.BudgetRepository;
import com.demo.finance.out.repository.GoalRepository;
import com.demo.finance.domain.model.Budget;
import com.demo.finance.out.repository.TransactionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class NotificationService {

    private final BudgetRepository budgetRepository;
    private final GoalRepository goalRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final MockEmailUtils mockEmailUtils;

    public NotificationService(BudgetRepository budgetRepository, GoalRepository goalRepository,
                               TransactionRepository transactionRepository, UserRepository userRepository,
                               MockEmailUtils mockEmailUtils) {
        this.budgetRepository = budgetRepository;
        this.goalRepository = goalRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.mockEmailUtils = mockEmailUtils;
    }

    public String fetchBudgetNotification(Long userId) {
        String notification = getBudgetLimitNotification(userId);
        sendNotificationViaEmail(userId, "Budget Notification", notification);
        return notification;
    }

    public String fetchGoalNotification(Long userId) {
        String notification = getGoalCompletionNotification(userId);
        sendNotificationViaEmail(userId, "Goal Notification", notification);
        return notification;
    }

    private String getBudgetLimitNotification(Long userId) {
        Optional<Budget> budgetOpt = budgetRepository.findByUserId(userId);
        if (budgetOpt.isEmpty()) {
            return "No budget set for user.";
        }

        Budget budget = budgetOpt.get();
        double totalExpenses = calculateTotalExpenses(userId);

        double remainingBudget = budget.getMonthlyLimit() - totalExpenses;
        if (remainingBudget < 0) {
            return "🚨 Budget exceeded! Limit: " + budget.getMonthlyLimit() + ", Expenses: " + totalExpenses;
        } else {
            return "✅ Budget is under control. Remaining budget: " + remainingBudget;
        }
    }

    private String getGoalCompletionNotification(Long userId) {
        List<Goal> userGoals = goalRepository.findByUserId(userId);
        if (userGoals.isEmpty()) {
            return "No goals set.";
        }

        StringBuilder notification = new StringBuilder();
        for (Goal goal : userGoals) {
            double totalBalance = calculateTotalBalance(userId, goal);
            double progress = goal.calculateProgress(totalBalance);

            if (progress >= 100) {
                notification.append("🎉 Goal achieved: '").append(goal.getGoalName())
                        .append("'! Target: ").append(goal.getTargetAmount())
                        .append(", Balance: ").append(totalBalance).append("\n");
            } else {
                notification.append("⏳ Goal '").append(goal.getGoalName())
                        .append("' progress: ").append(progress)
                        .append("% (Balance: ").append(totalBalance)
                        .append(" / Target: ").append(goal.getTargetAmount()).append(")\n");
            }
        }

        return notification.toString().trim();
    }

    private void sendNotificationViaEmail(Long userId, String subject, String body) {
        userRepository.findByUserId(userId).ifPresentOrElse(
                user -> mockEmailUtils.sendEmail(user.getEmail(), subject, body),
                () -> System.out.println("User email not found.")
        );
    }

    private double calculateTotalExpenses(Long userId) {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        return transactionRepository.findByUserId(userId).stream()
                .filter(t -> t.getType() == Type.EXPENSE)
                .filter(t -> !t.getDate().isBefore(startOfMonth) && !t.getDate().isAfter(endOfMonth))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    private double calculateTotalBalance(Long userId, Goal goal) {
        LocalDate startDate = goal.getStartTime();
        LocalDate endDate = startDate.plusMonths(goal.getDuration());

        return BalanceUtils.calculateTotalBalance(userId, startDate, endDate, transactionRepository);
    }
}