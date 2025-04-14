package com.demo.finance.out.service.impl;

import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.BalanceUtils;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.BudgetRepository;
import com.demo.finance.out.repository.GoalRepository;
import com.demo.finance.domain.model.Budget;
import com.demo.finance.out.repository.TransactionRepository;
import com.demo.finance.out.service.EmailService;
import com.demo.finance.out.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * The {@code NotificationServiceImpl} class implements the {@link NotificationService} interface
 * and provides concrete implementations for fetching budget and goal notifications.
 * It interacts with various repositories and utilities to generate and send notifications to users.
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final BudgetRepository budgetRepository;
    private final GoalRepository goalRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BalanceUtils balanceUtils;
    private final EmailService emailService;

    /**
     * Fetches a budget-related notification for a specific user and sends it via email.
     *
     * @param userId the unique identifier of the user for whom the budget notification is fetched
     * @return a {@link String} containing the budget notification message for the user
     */
    @Override
    public String fetchBudgetNotification(Long userId) {
        String notification = getBudgetLimitNotification(userId);
        sendNotificationViaEmail(userId, "Budget Notification", notification);
        return notification;
    }

    /**
     * Fetches a goal-related notification for a specific user and sends it via email.
     *
     * @param userId the unique identifier of the user for whom the goal notification is fetched
     * @return a {@link String} containing the goal notification message for the user
     */
    @Override
    public String fetchGoalNotification(Long userId) {
        String notification = getGoalCompletionNotification(userId);
        sendNotificationViaEmail(userId, "Goal Notification", notification);
        return notification;
    }

    /**
     * Generates a budget limit notification for a specific user based on their expenses and budget limit.
     *
     * @param userId the unique identifier of the user
     * @return a {@link String} containing the budget limit notification message
     */
    private String getBudgetLimitNotification(Long userId) {
        Budget budget = budgetRepository.findByUserId(userId);
        if (budget == null) {
            return "No budget set for user.";
        }
        BigDecimal totalExpenses = calculateTotalExpenses(userId);
        BigDecimal remainingBudget = budget.getMonthlyLimit().subtract(totalExpenses);
        if (remainingBudget.compareTo(BigDecimal.ZERO) < 0) {
            return "🚨 Budget exceeded! Limit: " + budget.getMonthlyLimit() + ", Expenses: " + totalExpenses;
        } else {
            return "✅ Budget is under control. Remaining budget: " + remainingBudget;
        }
    }

    /**
     * Generates a goal completion notification for a specific user, including progress updates for all goals.
     *
     * @param userId the unique identifier of the user
     * @return a {@link String} containing the goal completion notification message
     */
    private String getGoalCompletionNotification(Long userId) {
        List<Goal> userGoals = goalRepository.findByUserId(userId);
        if (userGoals.isEmpty()) {
            return "No goals set.";
        }
        StringBuilder notification = new StringBuilder();
        for (Goal goal : userGoals) {
            BigDecimal totalBalance = balanceUtils.calculateBalance(userId, goal);
            BigDecimal progress = goal.calculateProgress(totalBalance);
            String formattedProgress = progress.setScale(2, RoundingMode.HALF_UP) + "%";
            if (progress.compareTo(BigDecimal.valueOf(100)) >= 0) {
                notification.append("🎉 Goal achieved: '").append(goal.getGoalName())
                        .append("'! Target: ").append(goal.getTargetAmount())
                        .append(", Balance: ").append(totalBalance).append("\n");
            } else {
                notification.append("⏳ Goal '").append(goal.getGoalName())
                        .append("' progress: ").append(formattedProgress).append("\n");
            }
        }

        return notification.toString().trim();
    }

    /**
     * Sends a notification to a user via email.
     *
     * @param userId  the unique identifier of the user
     * @param subject the subject of the email notification
     * @param body    the content or body of the email notification
     * @throws RuntimeException if the user is not found in the database
     */
    private void sendNotificationViaEmail(Long userId, String subject, String body) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found.");
        } else {
            emailService.sendEmail(user.getEmail(), subject, body);
        }
    }

    /**
     * Calculates the total expenses incurred by a user within the current month.
     *
     * @param userId the unique identifier of the user
     * @return the total expenses as a {@link BigDecimal} value for the current month
     */
    private BigDecimal calculateTotalExpenses(Long userId) {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        return transactionRepository.findByUserId(userId).stream()
                .filter(t -> t.getType() == Type.EXPENSE)
                .filter(t -> !t.getDate().isBefore(startOfMonth) && !t.getDate().isAfter(endOfMonth))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}