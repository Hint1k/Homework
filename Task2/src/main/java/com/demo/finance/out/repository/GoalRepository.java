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
     * Updates the specified Goal entity in the repository.
     *
     * @param goal the Goal entity with updated details
     * @return {@code true} if the goal was successfully updated, {@code false} otherwise
     */
    boolean update(Goal goal);

    /**
     * Deletes the Goal entity associated with the specified goal ID.
     *
     * @param goalId the ID of the goal to delete
     * @return {@code true} if the goal was successfully deleted, {@code false} otherwise
     */
    boolean delete(Long goalId);

    /**
     * Retrieves the Goal entity associated with the specified goal ID.
     *
     * @param goalId the ID of the goal to retrieve
     * @return the Goal entity if found, or {@code null} if not found
     */
    Goal findById(Long goalId);

    /**
     * Finds all Goal entities associated with a specific user ID.
     *
     * @param userId the ID of the user
     * @return a list of Goal entities associated with the user
     */
    List<Goal> findByUserId(Long userId);

    /**
     * Finds a Goal entity by its ID and the associated user ID.
     *
     * @param goalId the ID of the goal to retrieve
     * @param userId the ID of the user who owns the goal
     * @return an {@code Optional<Goal>} containing the goal if found,
     * or an empty {@code Optional} if not found
     */
    Optional<Goal> findByUserIdAndGoalId(Long goalId, Long userId);
}