package com.demo.finance.out.service;

import com.demo.finance.domain.model.Goal;

import java.math.BigDecimal;
import java.util.List;

/**
 * {@code GoalService} defines the contract for services related to managing user financial goals.
 * It includes methods for creating, retrieving, updating, and deleting goals, as well as calculating
 * the total balance for a specific goal.
 */
public interface GoalService {

    /**
     * Creates a new goal for a user.
     *
     * @param userId       the ID of the user for whom the goal is being created
     * @param goalName     the name of the goal
     * @param targetAmount the target amount to be saved for the goal
     * @param duration     the duration (in months) to achieve the goal
     */
    void createGoal(Long userId, String goalName, BigDecimal targetAmount, int duration);

    Goal getGoal(Long goalId);

    /**
     * Retrieves all goals associated with a user.
     *
     * @param userId the ID of the user whose goals are being retrieved
     * @return a list of goals associated with the user
     */
    List<Goal> getGoalsByUserId(Long userId);

    boolean updateGoal(Long goalId, Long userId, String newGoalName, BigDecimal newTargetAmount, int newDuration);

    boolean deleteGoal(Long userId, Long goalId);

    /**
     * Calculates the total balance of a user's goal.
     *
     * @param userId the ID of the user whose goal balance is being calculated
     * @param goal   the goal for which the balance is being calculated
     * @return the total balance accumulated towards the goal
     */
    BigDecimal calculateTotalBalance(Long userId, Goal goal);
}