package com.demo.finance.out.service;

import com.demo.finance.domain.model.Goal;

import java.util.List;
import java.util.Optional;

public interface GoalService {

    void createGoal(Long userId, String goalName, double targetAmount, int duration);

    Optional<Goal> getGoal(Long userId, String goalName);

    List<Goal> getUserGoals(Long userId);

    void updateGoal(Long userId, String oldGoalName, String newGoalName, double newTargetAmount, int newDuration);

    void deleteGoal(Long userId, String goalName);

    double calculateTotalBalance(Long userId, Goal goal);
}