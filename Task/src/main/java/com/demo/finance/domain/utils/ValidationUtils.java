package com.demo.finance.domain.utils;

import com.demo.finance.domain.dto.GoalDto;
import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.dto.UserDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface ValidationUtils {

    UserDto validateUserJson(String json, Mode mode);

    UserDto validateUserJson(String json, Mode mode, String userId);

    UserDto validateUserJson(String json, Mode mode, Long userId);

    Long parseUserId(String userId, Mode mode);

    PaginationParams validatePaginationParams(String page, String size);

    Map<String, LocalDate> validateReport(String json, Mode mode, Long userId);

    BigDecimal validateBudgetJson(String json, Mode mode, Long userId);

    TransactionDto validateTransactionJson(String json, Mode mode);

    TransactionDto validateTransactionJson(String json, Mode mode, String transactionId);

    GoalDto validateGoalJson(String json, Mode mode);

    GoalDto validateGoalJson(String json, Mode mode, String goalId);

    Long parseTransactionId(String transactionIdString, Mode mode);

    Long parseGoalId(String goalIdString, Mode mode);
}