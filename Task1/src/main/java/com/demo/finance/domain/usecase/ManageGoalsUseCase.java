package com.demo.finance.domain.usecase;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.out.repository.GoalRepository;

import java.util.List;
import java.util.Optional;

public class ManageGoalsUseCase {
    private final GoalRepository goalRepository;

    public ManageGoalsUseCase(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public void createGoal(String userId, String goalName, double targetAmount) {
        goalRepository.save(new Goal(userId, goalName, targetAmount));
    }

    public Optional<Goal> getGoal(String userId, String goalName) {
        return goalRepository.findByUserIdAndName(userId, goalName);
    }

    public List<Goal> getUserGoals(String userId) {
        return goalRepository.findByUserId(userId);
    }

    public void addToGoal(String userId, String goalName, double amount) {
        goalRepository.findByUserIdAndName(userId, goalName).ifPresent(goal -> goal.addSavings(amount));
    }

    public boolean isGoalAchieved(String userId, String goalName) {
        return goalRepository.findByUserIdAndName(userId, goalName)
                .map(Goal::isAchieved)
                .orElse(false);
    }
}