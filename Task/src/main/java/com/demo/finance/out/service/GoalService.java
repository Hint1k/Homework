package com.demo.finance.out.service;

import com.demo.finance.domain.dto.GoalDto;
import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.utils.PaginatedResponse;

public interface GoalService {

    Long createGoal(GoalDto goalDto);

    Goal getGoal(Long goalId);

    Goal getGoalByUserIdAndGoalId(Long userId, Long goalId);

    boolean updateGoal(GoalDto goalDto, Long userId);

     boolean deleteGoal(Long userId, Long goalId);

    PaginatedResponse<GoalDto> getPaginatedGoalsForUser(Long userId, int page, int size);
}