package com.demo.finance.out.service;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.utils.BalanceUtils;
import com.demo.finance.out.repository.GoalRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * {@code GoalServiceImpl} implements the {@code GoalService} interface.
 * This service provides methods for creating, retrieving, updating, and deleting financial goals.
 * It also calculates the total balance accumulated towards a goal.
 */
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final BalanceUtils balanceUtils;

    /**
     * Constructs a new {@code GoalServiceImpl} instance with the provided repository and utility classes.
     *
     * @param goalRepository the repository for accessing and modifying goals
     * @param balanceUtils the utility class for calculating balance towards goals
     */
    public GoalServiceImpl(GoalRepository goalRepository, BalanceUtils balanceUtils) {
        this.goalRepository = goalRepository;
        this.balanceUtils = balanceUtils;
    }

    /**
     * Creates a new goal for a user.
     *
     * @param userId the ID of the user for whom the goal is being created
     * @param goalName the name of the goal
     * @param targetAmount the target amount to be saved for the goal
     * @param duration the duration (in months) to achieve the goal
     */
    @Override
    public void createGoal(Long userId, String goalName, BigDecimal targetAmount, int duration) {
        goalRepository.save(new Goal(userId, goalName, targetAmount, duration));
    }

    /**
     * Retrieves a specific goal for a user.
     *
     * @param userId the ID of the user whose goal is being retrieved
     * @param goalName the name of the goal to retrieve
     * @return an {@code Optional<Goal>} containing the goal, or an empty optional if the goal does not exist
     */
    @Override
    public Optional<Goal> getGoal(Long userId, String goalName) {
        return goalRepository.findByUserIdAndName(userId, goalName);
    }

    /**
     * Retrieves all goals associated with a user.
     *
     * @param userId the ID of the user whose goals are being retrieved
     * @return a list of goals associated with the user
     */
    @Override
    public List<Goal> getUserGoals(Long userId) {
        return goalRepository.findByUserId(userId);
    }

    /**
     * Updates an existing goal for a user.
     *
     * @param userId the ID of the user whose goal is being updated
     * @param oldGoalName the name of the goal to be updated
     * @param newGoalName the new name for the goal
     * @param newTargetAmount the new target amount for the goal
     * @param newDuration the new duration (in months) to achieve the goal
     * @throws IllegalArgumentException if the goal to update does not exist
     */
    @Override
    public void updateGoal(Long userId, String oldGoalName, String newGoalName, BigDecimal newTargetAmount,
                           int newDuration) {
        Optional<Goal> existingGoal = goalRepository.findByUserIdAndName(userId, oldGoalName);
        if (existingGoal.isPresent()) {
            Goal updatedGoal = new Goal(userId, newGoalName, newTargetAmount, newDuration);
            goalRepository.updateGoal(userId, oldGoalName, updatedGoal);
        } else {
            throw new IllegalArgumentException("Goal not found.");
        }
    }

    /**
     * Deletes a goal for a user.
     *
     * @param userId the ID of the user whose goal is to be deleted
     * @param goalName the name of the goal to delete
     */
    @Override
    public void deleteGoal(Long userId, String goalName) {
        goalRepository.deleteByUserIdAndName(userId, goalName);
    }

    /**
     * Calculates the total balance accumulated towards a user's goal.
     *
     * @param userId the ID of the user whose goal balance is being calculated
     * @param goal the goal for which the balance is being calculated
     * @return the total balance accumulated towards the goal
     */
    @Override
    public BigDecimal calculateTotalBalance(Long userId, Goal goal) {
        return balanceUtils.calculateBalance(userId, goal);
    }
}