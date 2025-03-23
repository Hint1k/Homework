package com.demo.finance.out.service;

import com.demo.finance.domain.model.Budget;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

public interface BudgetService {

    Budget setMonthlyBudget(Long userId, BigDecimal limit);

    Budget getBudget(Long userId);

    BigDecimal calculateExpensesForMonth(Long userId, YearMonth currentMonth);

    Map<String, Object> getBudgetData(Long userId);
}