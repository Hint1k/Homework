package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Goal;

import java.util.List;
import java.util.Optional;

/**
 * The GoalRepository interface defines the methods for managing Goal entities in the repository.
 * It provides CRUD operations and query methods for Goal objects.
 */
public interface GoalRepository {

    /**
     * Saves a Goal entity to the repository.
     *
     * @param goal the Goal entity to be saved
     */
    void save(Goal goal);

    /**
     * Finds a Goal entity by the user ID and goal name.
     *
     * @param userId the ID of the user associated with the goal
     * @param goalName the name of the goal
     * @return an Optional containing the Goal entity if found, otherwise an empty Optional
     */
    Optional<Goal> findByUserIdAndName(Long userId, String goalName);

    /**
     * Finds all Goal entities associated with a specific user ID.
     *
     * @param userId the ID of the user
     * @return a list of Goal entities associated with the user
     */
    List<Goal> findByUserId(Long userId);

    /**
     * Updates an existing Goal entity with new details.
     *
     * @param userId the ID of the user associated with the goal
     * @param oldGoalName the current name of the goal to be updated
     * @param updatedGoal the updated Goal entity
     */
    void updateGoal(Long userId, String oldGoalName, Goal updatedGoal);

    /**
     * Deletes a Goal entity by the user ID and goal name.
     *
     * @param userId the ID of the user associated with the goal
     * @param goalName the name of the goal to be deleted
     */
    void deleteByUserIdAndName(Long userId, String goalName);
}