package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Goal;

import java.util.List;

/**
 * The {@code GoalRepository} interface defines the contract for operations related to goal data persistence.
 * It provides methods for saving, updating, deleting, and retrieving goals from the database.
 */
public interface GoalRepository {

    /**
     * Saves a new goal to the database.
     *
     * @param goal the {@link Goal} object to be saved
     * @return the unique identifier ({@code Long}) of the newly saved goal
     */
    Long save(Goal goal);

    /**
     * Updates an existing goal in the database.
     *
     * @param goal the {@link Goal} object containing updated information
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    boolean update(Goal goal);

    /**
     * Deletes a goal from the database based on its unique goal ID.
     *
     * @param goalId the unique identifier of the goal to delete
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    boolean delete(Long goalId);

    /**
     * Retrieves a specific goal by its unique goal ID.
     *
     * @param goalId the unique identifier of the goal
     * @return the {@link Goal} object matching the provided goal ID, or {@code null} if not found
     */
    Goal findById(Long goalId);

    /**
     * Retrieves all goals associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @return a {@link List} of {@link Goal} objects associated with the user
     */
    List<Goal> findByUserId(Long userId);

    /**
     * Retrieves a paginated list of goals associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @param offset the starting index for pagination (zero-based)
     * @param size   the maximum number of goals to retrieve
     * @return a {@link List} of {@link Goal} objects representing the paginated results
     */
    List<Goal> findByUserId(Long userId, int offset, int size);

    /**
     * Retrieves a specific goal associated with a user by their user ID and goal ID.
     *
     * @param userId the unique identifier of the user
     * @param goalId the unique identifier of the goal
     * @return the {@link Goal} object matching the provided user ID and goal ID, or {@code null} if not found
     */
    Goal findByUserIdAndGoalId(Long userId, Long goalId);

    /**
     * Retrieves the total count of goals associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @return the total number of goals as an integer
     */
    int getTotalGoalCountForUser(Long userId);
}