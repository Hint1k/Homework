package com.demo.finance.domain.utils;

import com.demo.finance.domain.model.Goal;

import java.math.BigDecimal;

/**
 * Interface for utilities related to balance calculations for financial goals.
 * This interface provides methods to calculate the balance towards a specific goal.
 */
public interface BalanceUtils {

    /**
     * Calculates the balance for a given user and goal.
     *
     * @param userId The ID of the user whose balance is to be calculated.
     * @param goal The goal for which the balance is to be calculated.
     * @return The balance towards the specified goal.
     */
    BigDecimal calculateBalance(Long userId, Goal goal);
}