package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.usecase.GoalsUseCase;

import java.util.List;
import java.util.Optional;

public class GoalController {
    private final GoalsUseCase goalsUseCase;

    public GoalController(GoalsUseCase goalsUseCase) {
        this.goalsUseCase = goalsUseCase;
    }

    public void createGoal(Long userId, String name, double targetAmount) {
        goalsUseCase.createGoal(userId, name, targetAmount);
    }

    public Optional<Goal> getGoal(Long userId, String name) {
        return goalsUseCase.getGoal(userId, name);
    }

    public List<Goal> getAllGoals(Long userId) {
        return goalsUseCase.getUserGoals(userId);
    }

    public void updateGoalProgress(Long userId, String goalName, double amountSaved) {
        goalsUseCase.addToGoal(userId, goalName, amountSaved);
    }

    public void deleteGoal(Long userId, String goalName) {
        goalsUseCase.deleteGoal(userId, goalName);
    }
}