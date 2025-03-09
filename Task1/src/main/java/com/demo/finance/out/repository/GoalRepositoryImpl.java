package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Goal;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * The GoalRepositoryImpl class provides an implementation of the GoalRepository interface.
 * It manages Goal entities using an in-memory CopyOnWriteArrayList for storage.
 */
public class GoalRepositoryImpl implements GoalRepository {
    private final List<Goal> goals = new CopyOnWriteArrayList<>();

    /**
     * Saves a Goal entity to the repository.
     *
     * @param goal the Goal entity to be saved
     */
    @Override
    public void save(Goal goal) {
        goals.add(goal);
    }

    /**
     * Finds a Goal entity by the user ID and goal name.
     *
     * @param userId the ID of the user associated with the goal
     * @param goalName the name of the goal
     * @return an Optional containing the Goal entity if found, otherwise an empty Optional
     */
    @Override
    public Optional<Goal> findByUserIdAndName(Long userId, String goalName) {
        return goals.stream()
                .filter(goal -> goal.getUserId().equals(userId) && goal.getGoalName().equals(goalName))
                .findFirst();
    }

    /**
     * Finds all Goal entities associated with a specific user ID.
     *
     * @param userId the ID of the user
     * @return a list of Goal entities associated with the user
     */
    @Override
    public List<Goal> findByUserId(Long userId) {
        return goals.stream()
                .filter(goal -> goal.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing Goal entity with new details.
     *
     * @param userId the ID of the user associated with the goal
     * @param oldGoalName the current name of the goal to be updated
     * @param updatedGoal the updated Goal entity
     */
    @Override
    public void updateGoal(Long userId, String oldGoalName, Goal updatedGoal) {
        goals.removeIf(goal -> goal.getUserId().equals(userId) && goal.getGoalName().equals(oldGoalName));
        goals.add(updatedGoal);
    }

    /**
     * Deletes a Goal entity by the user ID and goal name.
     *
     * @param userId the ID of the user associated with the goal
     * @param goalName the name of the goal to be deleted
     */
    @Override
    public void deleteByUserIdAndName(Long userId, String goalName) {
        goals.removeIf(goal -> goal.getUserId().equals(userId) && goal.getGoalName().equals(goalName));
    }
}