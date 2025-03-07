package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Goal;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class GoalRepositoryImpl implements GoalRepository {
    private final List<Goal> goals = new CopyOnWriteArrayList<>();

    @Override
    public void save(Goal goal) {
        goals.add(goal);
    }

    @Override
    public Optional<Goal> findByUserIdAndName(Long userId, String goalName) {
        return goals.stream()
                .filter(goal -> goal.getUserId().equals(userId) && goal.getGoalName().equals(goalName))
                .findFirst();
    }

    @Override
    public List<Goal> findByUserId(Long userId) {
        return goals.stream()
                .filter(goal -> goal.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void updateGoal(Long userId, String oldGoalName, Goal updatedGoal) {
        goals.removeIf(goal -> goal.getUserId().equals(userId) && goal.getGoalName().equals(oldGoalName));
        goals.add(updatedGoal);
    }

    @Override
    public void deleteByUserIdAndName(Long userId, String goalName) {
        goals.removeIf(goal -> goal.getUserId().equals(userId) && goal.getGoalName().equals(goalName));
    }
}