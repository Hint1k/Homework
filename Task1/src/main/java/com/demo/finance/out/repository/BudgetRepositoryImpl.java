package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Budget;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@code BudgetRepositoryImpl} class provides an in-memory implementation of the {@code BudgetRepository}.
 * It uses a {@code ConcurrentHashMap} to store budgets, indexed by the user ID.
 * This implementation provides methods to save and retrieve budgets for users.
 */
public class BudgetRepositoryImpl implements BudgetRepository {

    // In-memory storage for user budgets
    private final Map<Long, Budget> budgets = new ConcurrentHashMap<>();

    /**
     * Saves the specified {@code Budget} object in the repository.
     * This implementation stores the budget in an in-memory map, indexed by the user ID.
     *
     * @param budget the {@code Budget} object to be saved
     * @return {@code true} indicating that the budget was successfully saved
     */
    @Override
    public boolean save(Budget budget) {
        budgets.put(budget.getUserId(), budget);
        return true;
    }

    /**
     * Retrieves the {@code Budget} associated with the specified user ID.
     * This implementation looks up the user ID in the in-memory map and returns an {@code Optional<Budget>}.
     *
     * @param userId the ID of the user whose budget is to be retrieved
     * @return an {@code Optional<Budget>} containing the user's budget if it exists,
     *         or an empty {@code Optional} if no budget is found for the given user ID
     */
    @Override
    public Optional<Budget> findByUserId(Long userId) {
        return Optional.ofNullable(budgets.get(userId));
    }
}