package com.demo.finance.out.service;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.utils.BalanceUtils;
import com.demo.finance.out.repository.GoalRepository;

import java.util.List;
import java.util.Optional;

public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final BalanceUtils balanceUtils;

    public GoalServiceImpl(GoalRepository goalRepository, BalanceUtils balanceUtils) {
        this.goalRepository = goalRepository;
        this.balanceUtils = balanceUtils;
    }

    @Override
    public void createGoal(Long userId, String goalName, double targetAmount, int duration) {
        goalRepository.save(new Goal(userId, goalName, targetAmount, duration));
    }

    @Override
    public Optional<Goal> getGoal(Long userId, String goalName) {
        return goalRepository.findByUserIdAndName(userId, goalName);
    }

    @Override
    public List<Goal> getUserGoals(Long userId) {
        return goalRepository.findByUserId(userId);
    }

    @Override
    public void updateGoal(Long userId, String oldGoalName, String newGoalName, double newTargetAmount,
                           int newDuration) {
        Optional<Goal> existingGoal = goalRepository.findByUserIdAndName(userId, oldGoalName);
        if (existingGoal.isPresent()) {
            Goal updatedGoal = new Goal(userId, newGoalName, newTargetAmount, newDuration);
            goalRepository.updateGoal(userId, oldGoalName, updatedGoal);
        } else {
            throw new IllegalArgumentException("Goal not found.");
        }
    }

    @Override
    public void deleteGoal(Long userId, String goalName) {
        goalRepository.deleteByUserIdAndName(userId, goalName);
    }

    @Override
    public double calculateTotalBalance(Long userId, Goal goal) {
        return balanceUtils.calculateBalance(userId, goal);
    }
}