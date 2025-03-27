package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.GoalDto;
import com.demo.finance.domain.mapper.GoalMapper;
import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.out.repository.GoalRepository;
import com.demo.finance.out.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * The {@code GoalServiceImpl} class implements the {@link GoalService} interface
 * and provides concrete implementations for goal-related operations.
 * It interacts with the database through the {@link GoalRepository} and handles logic for creating,
 * retrieving, updating, deleting, and paginating goals for users.
 */
@Service
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;

    @Autowired
    public GoalServiceImpl(GoalRepository goalRepository, GoalMapper goalMapper) {
        this.goalRepository = goalRepository;
        this.goalMapper = goalMapper;
    }

    /**
     * Creates a new financial goal in the system based on the provided goal data.
     * <p>
     * This method maps the provided {@link GoalDto} to a {@link Goal} entity, associates the goal
     * with the specified user ID, initializes the saved amount to zero, and saves it to the database.
     * The goal details include the target amount, duration, start time, and goal name.
     * <p>
     *
     * @param goalDto the {@link GoalDto} object containing the details of the goal to create
     * @param userId  the unique identifier of the user associated with the goal
     * @return the unique identifier ({@code Long}) of the newly created goal
     * @throws IllegalArgumentException if the provided goal data is invalid or incomplete
     */
    @Override
    public Long createGoal(GoalDto goalDto, Long userId) {
        Goal goal = goalMapper.toEntity(goalDto);
        goal.setUserId(userId);
        goal.setSavedAmount(BigDecimal.ZERO);
        return goalRepository.save(goal);
    }

    /**
     * Retrieves a specific goal by its unique goal ID.
     *
     * @param goalId the unique identifier of the goal
     * @return the {@link Goal} object matching the provided goal ID
     */
    @Override
    public Goal getGoal(Long goalId) {
        return goalRepository.findById(goalId);
    }

    /**
     * Retrieves a specific goal associated with a user by their user ID and goal ID.
     *
     * @param userId the unique identifier of the user
     * @param goalId the unique identifier of the goal
     * @return the {@link Goal} object matching the provided user ID and goal ID
     */
    @Override
    public Goal getGoalByUserIdAndGoalId(Long userId, Long goalId) {
        return goalRepository.findByUserIdAndGoalId(userId, goalId);
    }

    /**
     * Updates an existing goal in the system based on the provided goal data.
     *
     * @param goalDto the {@link GoalDto} object containing updated goal details
     * @param userId  the unique identifier of the user who owns the goal
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    @Override
    public boolean updateGoal(GoalDto goalDto, Long userId) {
        Long goalId = goalDto.getGoalId();
        Goal goal = goalRepository.findByUserIdAndGoalId(userId, goalId);
        if (goal != null) {
            goal.setGoalName(goalDto.getGoalName());
            goal.setTargetAmount(goalDto.getTargetAmount());
            goal.setDuration(goalDto.getDuration());
            goalRepository.update(goal);
            return true;
        }
        return false;
    }

    /**
     * Deletes a goal from the system based on the provided user ID and goal ID.
     *
     * @param userId the unique identifier of the user
     * @param goalId the unique identifier of the goal
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    @Override
    public boolean deleteGoal(Long userId, Long goalId) {
        Goal goal = goalRepository.findByUserIdAndGoalId(userId, goalId);
        if (goal != null) {
            return goalRepository.delete(goalId);
        }
        return false;
    }

    /**
     * Retrieves a paginated list of goals associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @param page   the page number to retrieve (one-based index)
     * @param size   the number of goals to include per page
     * @return a {@link PaginatedResponse} object containing a paginated list of {@link GoalDto} objects
     */
    @Override
    public PaginatedResponse<GoalDto> getPaginatedGoalsForUser(Long userId, int page, int size) {
        int offset = (page - 1) * size;
        List<Goal> goals = goalRepository.findByUserId(userId, offset, size);
        int totalGoals = goalRepository.getTotalGoalCountForUser(userId);
        List<GoalDto> dtoList = goals.stream().map(goalMapper::toDto).toList();
        return new PaginatedResponse<>(dtoList, totalGoals, (int) Math.ceil((double) totalGoals / size),
                page, size);
    }
}