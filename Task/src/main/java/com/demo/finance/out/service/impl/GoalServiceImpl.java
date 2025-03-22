package com.demo.finance.out.service.impl;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.utils.BalanceUtils;
import com.demo.finance.out.repository.GoalRepository;
import com.demo.finance.out.service.GoalService;

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
     * @param balanceUtils   the utility class for calculating balance towards goals
     */
    public GoalServiceImpl(GoalRepository goalRepository, BalanceUtils balanceUtils) {
        this.goalRepository = goalRepository;
        this.balanceUtils = balanceUtils;
    }

    /**
     * Creates a new goal for a user.
     *
     * @param userId       the ID of the user for whom the goal is being created
     * @param goalName     the name of the goal
     * @param targetAmount the target amount to be saved for the goal
     * @param duration     the duration (in months) to achieve the goal
     */
    @Override
    public void createGoal(Long userId, String goalName, BigDecimal targetAmount, int duration) {
        goalRepository.save(new Goal(userId, goalName, targetAmount, duration));
    }

    /**
     * Retrieves a goal by its ID.
     *
     * @param goalId the ID of the goal to retrieve
     * @return the goal if found, or {@code null} if not found
     */
    @Override
    public Goal getGoal(Long goalId) {
        return goalRepository.findById(goalId);
    }

    /**
     * Retrieves all goals associated with a user.
     *
     * @param userId the ID of the user whose goals are being retrieved
     * @return a list of goals associated with the user
     */
    @Override
    public List<Goal> getGoalsByUserId(Long userId) {
        return goalRepository.findByUserId(userId);
    }

    /**
     * Updates an existing goal with new details.
     *
     * @param goalId          the ID of the goal to update
     * @param userId          the ID of the user who owns the goal
     * @param newGoalName     the new name for the goal
     * @param newTargetAmount the new target amount for the goal
     * @param newDuration     the new duration (in months) to achieve the goal
     * @return {@code true} if the goal was successfully updated, {@code false} otherwise
     */
    @Override
    public boolean updateGoal(Long goalId, Long userId, String newGoalName, BigDecimal newTargetAmount,
                              int newDuration) {
        Optional<Goal> goal = goalRepository.findByUserIdAndGoalId(goalId, userId);
        if (goal.isPresent()) {
            Goal updatedGoal = goal.get();
            updatedGoal.setGoalName(newGoalName);
            updatedGoal.setTargetAmount(newTargetAmount);
            updatedGoal.setDuration(newDuration);
            goalRepository.update(updatedGoal);
            return true;
        }
        return false;
    }

    /**
     * Deletes a goal by its ID.
     *
     * @param userId the ID of the user who owns the goal
     * @param goalId the ID of the goal to delete
     * @return {@code true} if the goal was successfully deleted, {@code false} otherwise
     */
    @Override
    public boolean deleteGoal(Long userId, Long goalId) {
        Optional<Goal> goal = goalRepository.findByUserIdAndGoalId(userId, goalId);
        if (goal.isPresent()) {
            return goalRepository.delete(goalId);
        }
        return false;
    }

    /**
     * Calculates the total balance accumulated towards a user's goal.
     *
     * @param userId the ID of the user whose goal balance is being calculated
     * @param goal   the goal for which the balance is being calculated
     * @return the total balance accumulated towards the goal
     */
    @Override
    public BigDecimal calculateTotalBalance(Long userId, Goal goal) {
        return balanceUtils.calculateBalance(userId, goal);
    }

    @Override
    public List<Goal> getPaginatedGoals(Long userId, int offset, int size) {
        return goalRepository.findPaginatedGoals(userId, offset, size);
    }

    @Override
    public int getTotalGoalCount(Long userId) {
        return goalRepository.countAllGoals(userId);
    }
}