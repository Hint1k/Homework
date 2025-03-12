package com.demo.finance.out.service.impl;

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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * {@code NotificationServiceImpl} provides the implementation of notifications for budget and goal updates.
 * It interacts with repositories and services to check the status of the user's budget and goals,
 * and sends email notifications accordingly.
 */
public class NotificationServiceImpl implements NotificationService {

    private final BudgetRepository budgetRepository;
    private final GoalRepository goalRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BalanceUtils balanceUtils;
    private final EmailService emailService;

    /**
     * Constructs a {@code NotificationServiceImpl} instance with the provided dependencies.
     *
     * @param budgetRepository        the repository for budget data
     * @param goalRepository          the repository for goal data
     * @param transactionRepository   the repository for transaction data
     * @param userRepository          the repository for user data
     * @param balanceUtils            utility class for calculating balances
     * @param emailService            utility class for sending email notifications
     */
    public NotificationServiceImpl(BudgetRepository budgetRepository, GoalRepository goalRepository,
                                   TransactionRepository transactionRepository, UserRepository userRepository,
                                   BalanceUtils balanceUtils, EmailService emailService) {
        this.budgetRepository = budgetRepository;
        this.goalRepository = goalRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.balanceUtils = balanceUtils;
        this.emailService = emailService;
    }

    /**
     * Fetches and returns the notification related to the user's budget and sends it via email.
     *
     * @param userId the ID of the user for whom the budget notification is being fetched
     * @return the notification message for the budget
     */
    @Override
    public String fetchBudgetNotification(Long userId) {
        String notification = getBudgetLimitNotification(userId);
        sendNotificationViaEmail(userId, "Budget Notification", notification);
        return notification;
    }

    /**
     * Fetches and returns the notification related to the user's goal and sends it via email.
     *
     * @param userId the ID of the user for whom the goal notification is being fetched
     * @return the notification message for the goal
     */
    @Override
    public String fetchGoalNotification(Long userId) {
        String notification = getGoalCompletionNotification(userId);
        sendNotificationViaEmail(userId, "Goal Notification", notification);
        return notification;
    }

    /**
     * Retrieves the notification about the user's budget limit and compares it with current expenses.
     *
     * @param userId the ID of the user for whom the budget notification is being generated
     * @return the budget limit notification message
     */
    private String getBudgetLimitNotification(Long userId) {
        Optional<Budget> budgetOpt = budgetRepository.findByUserId(userId);
        if (budgetOpt.isEmpty()) {
            return "No budget set for user.";
        }

        Budget budget = budgetOpt.get();
        BigDecimal totalExpenses = calculateTotalExpenses(userId);

        BigDecimal remainingBudget = budget.getMonthlyLimit().subtract(totalExpenses);
        if (remainingBudget.compareTo(BigDecimal.ZERO) < 0) {
            return "🚨 Budget exceeded! Limit: " + budget.getMonthlyLimit() + ", Expenses: " + totalExpenses;
        } else {
            return "✅ Budget is under control. Remaining budget: " + remainingBudget;
        }
    }

    /**
     * Retrieves the notification about the user's goal progress or achievement.
     *
     * @param userId the ID of the user for whom the goal notification is being generated
     * @return the goal completion notification message
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
     * Sends a notification via email to the user.
     *
     * @param userId the ID of the user to receive the notification
     * @param subject the subject of the email
     * @param body the body content of the email
     */
    private void sendNotificationViaEmail(Long userId, String subject, String body) {
        userRepository.findByUserId(userId).ifPresentOrElse(
                user -> emailService.sendEmail(user.getEmail(), subject, body),
                () -> System.out.println("User email not found.")
        );
    }

    /**
     * Calculates the total expenses of a user for the current month.
     *
     * @param userId the ID of the user for whom the expenses are being calculated
     * @return the total expenses for the user in the current month
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