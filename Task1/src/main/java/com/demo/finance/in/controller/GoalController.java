package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.out.service.GoalService;

import java.util.List;
import java.util.Optional;

public class GoalController {
    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    public void createGoal(Long userId, String name, double targetAmount, int duration) {
        goalService.createGoal(userId, name, targetAmount, duration);
    }

    public Optional<Goal> getGoal(Long userId, String name) {
        return goalService.getGoal(userId, name);
    }

    public List<Goal> getAllGoals(Long userId) {
        return goalService.getUserGoals(userId);
    }

    public void updateGoal(Long userId, String oldGoalName, String newGoalName, double newTargetAmount,
                           int newDuration) {
        goalService.updateGoal(userId, oldGoalName, newGoalName, newTargetAmount, newDuration);
    }

    public void deleteGoal(Long userId, String goalName) {
        goalService.deleteGoal(userId, goalName);
    }

    public double calculateTotalBalance(Long userId, Goal goal) {
        return goalService.calculateTotalBalance(userId, goal);
    }
}