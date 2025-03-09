package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.out.service.GoalService;

import java.util.List;
import java.util.Optional;

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
     * @param userId the ID of the user for whom the goal is to be created
     * @param name the name of the goal
     * @param targetAmount the target amount for the goal
     * @param duration the duration of the goal in months
     */
    public void createGoal(Long userId, String name, double targetAmount, int duration) {
        goalService.createGoal(userId, name, targetAmount, duration);
    }

    /**
     * Retrieves a goal for a user by the goal name.
     *
     * @param userId the ID of the user whose goal is to be retrieved
     * @param name the name of the goal to retrieve
     * @return an {@code Optional} containing the goal if found, otherwise {@code Optional.empty()}
     */
    public Optional<Goal> getGoal(Long userId, String name) {
        return goalService.getGoal(userId, name);
    }

    /**
     * Retrieves all goals for a user.
     *
     * @param userId the ID of the user whose goals are to be retrieved
     * @return a list of the user's goals
     */
    public List<Goal> getAllGoals(Long userId) {
        return goalService.getUserGoals(userId);
    }

    /**
     * Updates an existing goal for a user with new details.
     *
     * @param userId the ID of the user whose goal is to be updated
     * @param oldGoalName the current name of the goal to be updated
     * @param newGoalName the new name for the goal
     * @param newTargetAmount the new target amount for the goal
     * @param newDuration the new duration for the goal in months
     */
    public void updateGoal(Long userId, String oldGoalName, String newGoalName, double newTargetAmount,
                           int newDuration) {
        goalService.updateGoal(userId, oldGoalName, newGoalName, newTargetAmount, newDuration);
    }

    /**
     * Deletes a goal for a user.
     *
     * @param userId the ID of the user whose goal is to be deleted
     * @param goalName the name of the goal to delete
     */
    public void deleteGoal(Long userId, String goalName) {
        goalService.deleteGoal(userId, goalName);
    }

    /**
     * Calculates the total balance towards a specific goal for a user.
     *
     * @param userId the ID of the user whose goal balance is to be calculated
     * @param goal the goal for which the balance is to be calculated
     * @return the total balance towards the specified goal
     */
    public double calculateTotalBalance(Long userId, Goal goal) {
        return goalService.calculateTotalBalance(userId, goal);
    }
}