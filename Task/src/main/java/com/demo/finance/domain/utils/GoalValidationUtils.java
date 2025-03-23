package com.demo.finance.domain.utils;

import com.demo.finance.domain.dto.GoalDto;

public interface GoalValidationUtils {

    GoalDto validateGoalJson(String json, Mode mode);
    GoalDto validateGoalJson(String json, Mode mode, String goalId);
    Long parseGoalId(String goalIdString, Mode mode);
    PaginationParams validatePaginationParams(String page, String size);
}