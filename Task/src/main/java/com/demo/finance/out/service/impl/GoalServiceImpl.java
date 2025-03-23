package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.GoalDto;
import com.demo.finance.domain.mapper.GoalMapper;
import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.utils.BalanceUtils;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.out.repository.GoalRepository;
import com.demo.finance.out.service.GoalService;

import java.math.BigDecimal;
import java.util.List;

/**
 * {@code GoalServiceImpl} implements the {@code GoalService} interface.
 * This service provides methods for creating, retrieving, updating, and deleting financial goals.
 * It also calculates the total balance accumulated towards a goal.
 */
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final BalanceUtils balanceUtils;

    /**
     * Constructs a new {@code GoalServiceImpl} instance with the provided repository and utility classes.
     *
     * @param goalRepository the repository for accessing and modifying goals
     * @param balanceUtils   the utility class for calculating balance towards goals
     */
    public GoalServiceImpl(GoalRepository goalRepository, BalanceUtils balanceUtils) {
        this.goalRepository = goalRepository;
        this.balanceUtils = balanceUtils;
    }

    @Override
    public Long createGoal(GoalDto goalDto) {
        Goal goal = GoalMapper.INSTANCE.toEntity(goalDto);
        goal.setSavedAmount(BigDecimal.ZERO);
        return goalRepository.save(goal);
    }

    /**
     * Retrieves a goal by its ID.
     *
     * @param goalId the ID of the goal to retrieve
     * @return the goal if found, or {@code null} if not found
     */
    @Override
    public Goal getGoal(Long goalId) {
        return goalRepository.findById(goalId);
    }

    @Override
    public Goal getGoalByUserIdAndGoalId(Long userId, Long goalId) {
        return goalRepository.findByUserIdAndGoalId(userId, goalId);
    }

    /**
     * Retrieves all goals associated with a user.
     *
     * @param userId the ID of the user whose goals are being retrieved
     * @return a list of goals associated with the user
     */
    @Override
    public List<Goal> getGoalsByUserId(Long userId) {
        return goalRepository.findByUserId(userId);
    }

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
     * Deletes a goal by its ID.
     *
     * @param userId the ID of the user who owns the goal
     * @param goalId the ID of the goal to delete
     * @return {@code true} if the goal was successfully deleted, {@code false} otherwise
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
     * Calculates the total balance accumulated towards a user's goal.
     *
     * @param userId the ID of the user whose goal balance is being calculated
     * @param goal   the goal for which the balance is being calculated
     * @return the total balance accumulated towards the goal
     */
    @Override
    public BigDecimal calculateTotalBalance(Long userId, Goal goal) {
        return balanceUtils.calculateBalance(userId, goal);
    }

    @Override
    public List<Goal> getPaginatedGoals(Long userId, int offset, int size) {
        return goalRepository.findPaginatedGoals(userId, offset, size);
    }

    @Override
    public int getTotalGoalCount(Long userId) {
        return goalRepository.getTotalGoalCountForUser(userId);
    }

    @Override
    public PaginatedResponse<GoalDto> getPaginatedGoalsForUser(Long userId, int page, int size) {
        int offset = (page - 1) * size;
        List<Goal> goals = goalRepository.findByUserId(userId, offset, size);
        int totalGoals = goalRepository.getTotalGoalCountForUser(userId);
        List<GoalDto> dtoList = goals.stream().map(GoalMapper.INSTANCE::toDto).toList();
        return new PaginatedResponse<>(dtoList, totalGoals, (int) Math.ceil((double) totalGoals / size),
                page, size);
    }
}