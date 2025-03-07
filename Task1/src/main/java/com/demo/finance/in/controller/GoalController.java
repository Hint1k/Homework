package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.usecase.ManageGoalsUseCase;

import java.util.List;
import java.util.Optional;

public class GoalController {
    private final ManageGoalsUseCase manageGoalsUseCase;

    public GoalController(ManageGoalsUseCase manageGoalsUseCase) {
        this.manageGoalsUseCase = manageGoalsUseCase;
    }

    public void createGoal(Long userId, String name, double targetAmount) {
        manageGoalsUseCase.createGoal(userId, name, targetAmount);
    }

    public Optional<Goal> getGoal(Long userId, String name) {
        return manageGoalsUseCase.getGoal(userId, name);
    }

    public List<Goal> getAllGoals(Long userId) {
        return manageGoalsUseCase.getUserGoals(userId);
    }

    public void updateGoalProgress(Long userId, String goalName, double amountSaved) {
        manageGoalsUseCase.addToGoal(userId, goalName, amountSaved);
    }

    public void deleteGoal(Long userId, String goalName) {
        manageGoalsUseCase.deleteGoal(userId, goalName);
    }
}