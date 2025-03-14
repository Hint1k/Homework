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
        goalRepository.saveGoal(new Goal(userId, goalName, targetAmount, duration));
    }

    @Override
    public Optional<Goal> getGoal(Long goalId) {
        return goalRepository.findGoalById(goalId);
    }

    /**
     * Retrieves all goals associated with a user.
     *
     * @param userId the ID of the user whose goals are being retrieved
     * @return a list of goals associated with the user
     */
    @Override
    public List<Goal> getGoalsByUserId(Long userId) {
        return goalRepository.findGoalByUserId(userId);
    }

    @Override
    public void updateGoal(Goal updatedGoal) {
        Optional<Goal> existingGoal = goalRepository.findGoalById(updatedGoal.getGoalId());
        if (existingGoal.isPresent()) {
            goalRepository.updateGoal(updatedGoal);
        } else {
            throw new IllegalArgumentException("Goal not found.");
        }
    }

    @Override
    public void deleteGoal(Long goalId) {
        goalRepository.deleteGoal(goalId);
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