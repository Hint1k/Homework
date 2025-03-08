package com.demo.finance.domain.utils;

import com.demo.finance.domain.model.Goal;

public interface BalanceUtils {

    double calculateBalance(Long userId, Goal goal);
}