package com.demo.finance.out.service;

import com.demo.finance.domain.model.Goal;

import java.util.List;
import java.util.Optional;

/**
 * {@code GoalService} defines the contract for services related to managing user financial goals.
 * It includes methods for creating, retrieving, updating, and deleting goals, as well as calculating
 * the total balance for a specific goal.
 */
public interface GoalService {

    /**
     * Creates a new goal for a user.
     *
     * @param userId the ID of the user for whom the goal is being created
     * @param goalName the name of the goal
     * @param targetAmount the target amount to be saved for the goal
     * @param duration the duration (in months) to achieve the goal
     */
    void createGoal(Long userId, String goalName, double targetAmount, int duration);

    /**
     * Retrieves a specific goal for a user.
     *
     * @param userId the ID of the user whose goal is being retrieved
     * @param goalName the name of the goal to retrieve
     * @return an {@code Optional<Goal>} containing the goal, or an empty optional if the goal does not exist
     */
    Optional<Goal> getGoal(Long userId, String goalName);

    /**
     * Retrieves all goals associated with a user.
     *
     * @param userId the ID of the user whose goals are being retrieved
     * @return a list of goals associated with the user
     */
    List<Goal> getUserGoals(Long userId);

    /**
     * Updates an existing goal for a user.
     *
     * @param userId the ID of the user whose goal is being updated
     * @param oldGoalName the name of the goal to be updated
     * @param newGoalName the new name for the goal
     * @param newTargetAmount the new target amount for the goal
     * @param newDuration the new duration (in months) to achieve the goal
     */
    void updateGoal(Long userId, String oldGoalName, String newGoalName, double newTargetAmount, int newDuration);

    /**
     * Deletes a goal for a user.
     *
     * @param userId the ID of the user whose goal is to be deleted
     * @param goalName the name of the goal to delete
     */
    void deleteGoal(Long userId, String goalName);

    /**
     * Calculates the total balance of a user's goal.
     *
     * @param userId the ID of the user whose goal balance is being calculated
     * @param goal the goal for which the balance is being calculated
     * @return the total balance accumulated towards the goal
     */
    double calculateTotalBalance(Long userId, Goal goal);
}