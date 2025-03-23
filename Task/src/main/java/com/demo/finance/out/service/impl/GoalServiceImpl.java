package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.GoalDto;
import com.demo.finance.domain.mapper.GoalMapper;
import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.out.repository.GoalRepository;
import com.demo.finance.out.service.GoalService;

import java.math.BigDecimal;
import java.util.List;

public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;

    public GoalServiceImpl(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    @Override
    public Long createGoal(GoalDto goalDto) {
        Goal goal = GoalMapper.INSTANCE.toEntity(goalDto);
        goal.setSavedAmount(BigDecimal.ZERO);
        return goalRepository.save(goal);
    }

    @Override
    public Goal getGoal(Long goalId) {
        return goalRepository.findById(goalId);
    }

    @Override
    public Goal getGoalByUserIdAndGoalId(Long userId, Long goalId) {
        return goalRepository.findByUserIdAndGoalId(userId, goalId);
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

    @Override
    public boolean deleteGoal(Long userId, Long goalId) {
        Goal goal = goalRepository.findByUserIdAndGoalId(userId, goalId);
        if (goal != null) {
            return goalRepository.delete(goalId);
        }
        return false;
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