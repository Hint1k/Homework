package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Budget;

import java.util.Optional;

/**
 * The {@code BudgetRepository} interface defines the contract for interacting with the storage
 * of user budgets in the system. It provides methods to save a budget and retrieve a budget
 * based on a user ID.
 */
public interface BudgetRepository {

    /**
     * Saves the specified {@code Budget} object.
     *
     * @param budget the {@code Budget} object to be saved
     * @return {@code true} if the budget was successfully saved, {@code false} otherwise
     */
    boolean save(Budget budget);

    /**
     * Retrieves the {@code Budget} associated with the specified user ID.
     *
     * @param userId the ID of the user whose budget is to be retrieved
     * @return an {@code Optional<Budget>} containing the user's budget if it exists,
     * or an empty {@code Optional} if no budget is found
     */
    Optional<Budget> findByUserId(Long userId);

    boolean delete(Long budgetId);

    boolean update(Budget updatedBudget);

    Optional<Budget> findById(Long budgetId);
}