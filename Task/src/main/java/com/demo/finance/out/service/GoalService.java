package com.demo.finance.out.service;

import com.demo.finance.domain.dto.GoalDto;
import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.utils.PaginatedResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * {@code GoalService} defines the contract for services related to managing user financial goals.
 * It includes methods for creating, retrieving, updating, and deleting goals, as well as calculating
 * the total balance for a specific goal.
 */
public interface GoalService {

    Long createGoal(GoalDto goalDto);

    /**
     * Retrieves a goal by its ID.
     *
     * @param goalId the ID of the goal to retrieve
     * @return the goal if found, or {@code null} if not found
     */
    Goal getGoal(Long goalId);

    Goal getGoalByUserIdAndGoalId(Long userId, Long goalId);

    /**
     * Retrieves all goals associated with a user.
     *
     * @param userId the ID of the user whose goals are being retrieved
     * @return a list of goals associated with the user
     */
    List<Goal> getGoalsByUserId(Long userId);

    boolean updateGoal(GoalDto goalDto, Long userId);

    /**
     * Deletes a goal by its ID.
     *
     * @param userId the ID of the user who owns the goal
     * @param goalId the ID of the goal to delete
     * @return {@code true} if the goal was successfully deleted, {@code false} otherwise
     */
    boolean deleteGoal(Long userId, Long goalId);

    /**
     * Calculates the total balance accumulated towards a user's goal.
     *
     * @param userId the ID of the user whose goal balance is being calculated
     * @param goal   the goal for which the balance is being calculated
     * @return the total balance accumulated towards the goal
     */
    BigDecimal calculateTotalBalance(Long userId, Goal goal);

    /**
     * Retrieves a paginated list of goals.
     *
     * @param offset the starting index for pagination
     * @param size   the number of goals to retrieve
     * @return a list of paginated goals
     */
    List<Goal> getPaginatedGoals(Long userId, int offset, int size);

    /**
     * Retrieves the total count of goals in the system.
     *
     * @return the total number of goals
     */
    int getTotalGoalCount(Long userId);

    PaginatedResponse<GoalDto> getPaginatedGoalsForUser(Long userId, int page, int size);
}