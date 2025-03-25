package com.demo.finance.out.service;

import com.demo.finance.domain.dto.GoalDto;
import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.utils.PaginatedResponse;

/**
 * The {@code GoalService} interface defines the contract for operations related to goal management.
 * It provides methods for creating, retrieving, updating, deleting, and paginating goals for users.
 */
public interface GoalService {

    /**
     * Creates a new financial goal in the system based on the provided goal data.
     * <p>
     * This method maps the provided {@link GoalDto} to a {@link Goal} entity, associates the goal
     * with the specified user ID, initializes the saved amount to zero, and saves it to the database.
     * The goal details typically include the target amount, duration, start time, and goal name.
     * <p>
     * Example request payload for creating a goal:
     *
     * @param goalDto the {@link GoalDto} object containing the details of the goal to create
     * @param userId  the unique identifier of the user associated with the goal
     * @return the unique identifier ({@code Long}) of the newly created goal
     * @throws IllegalArgumentException if the provided goal data is invalid or incomplete
     */
    Long createGoal(GoalDto goalDto, Long userId);

    /**
     * Retrieves a specific goal by its unique goal ID.
     *
     * @param goalId the unique identifier of the goal
     * @return the {@link Goal} object matching the provided goal ID
     */
    Goal getGoal(Long goalId);

    /**
     * Retrieves a specific goal associated with a user by their user ID and goal ID.
     *
     * @param userId the unique identifier of the user
     * @param goalId the unique identifier of the goal
     * @return the {@link Goal} object matching the provided user ID and goal ID
     */
    Goal getGoalByUserIdAndGoalId(Long userId, Long goalId);

    /**
     * Updates an existing goal in the system based on the provided goal data.
     *
     * @param goalDto the {@link GoalDto} object containing updated goal details
     * @param userId  the unique identifier of the user who owns the goal
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    boolean updateGoal(GoalDto goalDto, Long userId);

    /**
     * Deletes a goal from the system based on the provided user ID and goal ID.
     *
     * @param userId the unique identifier of the user
     * @param goalId the unique identifier of the goal
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    boolean deleteGoal(Long userId, Long goalId);

    /**
     * Retrieves a paginated list of goals associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @param page   the page number to retrieve (zero-based index)
     * @param size   the number of goals to include per page
     * @return a {@link PaginatedResponse} object containing a paginated list of {@link GoalDto} objects
     */
    PaginatedResponse<GoalDto> getPaginatedGoalsForUser(Long userId, int page, int size);
}