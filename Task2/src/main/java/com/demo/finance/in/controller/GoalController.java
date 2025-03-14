package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.out.service.GoalService;

import java.math.BigDecimal;
import java.util.List;

/**
 * The {@code GoalController} class handles incoming requests related to goal management. It provides
 * methods to create, retrieve, update, and delete goals for users, as well as to calculate the total balance
 * towards a goal.
 */
public class GoalController {
    private final GoalService goalService;

    /**
     * Constructs a {@code GoalController} with the specified {@code GoalService}.
     *
     * @param goalService the {@code GoalService} used for managing goals
     */
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    /**
     * Creates a new goal for a user with the specified details.
     *
     * @param userId       the ID of the user for whom the goal is to be created
     * @param name         the name of the goal
     * @param targetAmount the target amount for the goal
     * @param duration     the duration of the goal in months
     */
    public void createGoal(Long userId, String name, BigDecimal targetAmount, int duration) {
        goalService.createGoal(userId, name, targetAmount, duration);
    }

    public Goal getGoal(Long goalId) {
        return goalService.getGoal(goalId);
    }

    /**
     * Retrieves all goals for a user.
     *
     * @param userId the ID of the user whose goals are to be retrieved
     * @return a list of the user's goals
     */
    public List<Goal> getAllGoalsByUserId(Long userId) {
        return goalService.getGoalsByUserId(userId);
    }

    public boolean updateGoal(Long goalId, Long userId, String newGoalName, BigDecimal newTargetAmount,
                              int newDuration) {
        return goalService.updateGoal(goalId, userId, newGoalName, newTargetAmount, newDuration);
    }

    public boolean deleteGoal(Long userId, Long goalId) {
        return goalService.deleteGoal(userId, goalId);
    }

    /**
     * Calculates the total balance towards a specific goal for a user.
     *
     * @param userId the ID of the user whose goal balance is to be calculated
     * @param goal   the goal for which the balance is to be calculated
     * @return the total balance towards the specified goal
     */
    public BigDecimal calculateTotalBalance(Long userId, Goal goal) {
        return goalService.calculateTotalBalance(userId, goal);
    }
}