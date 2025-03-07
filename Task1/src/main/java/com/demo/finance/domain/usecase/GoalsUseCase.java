package com.demo.finance.domain.usecase;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.utils.BalanceUtils;
import com.demo.finance.out.repository.GoalRepository;
import com.demo.finance.out.repository.TransactionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class GoalsUseCase {

    private final GoalRepository goalRepository;
    private final TransactionRepository transactionRepository;

    public GoalsUseCase(GoalRepository goalRepository, TransactionRepository transactionRepository) {
        this.goalRepository = goalRepository;
        this.transactionRepository = transactionRepository;
    }

    public void createGoal(Long userId, String goalName, double targetAmount, int duration) {
        goalRepository.save(new Goal(userId, goalName, targetAmount, duration));
    }

    public Optional<Goal> getGoal(Long userId, String goalName) {
        return goalRepository.findByUserIdAndName(userId, goalName);
    }

    public List<Goal> getUserGoals(Long userId) {
        return goalRepository.findByUserId(userId);
    }

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

    public void deleteGoal(Long userId, String goalName) {
        goalRepository.deleteByUserIdAndName(userId, goalName);
    }

    public double calculateTotalBalance(Long userId, Goal goal) {
        LocalDate startDate = goal.getStartTime();
        LocalDate endDate = startDate.plusMonths(goal.getDuration());

        return BalanceUtils.calculateTotalBalance(userId, startDate, endDate, transactionRepository);
    }
}