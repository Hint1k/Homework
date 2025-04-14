package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Budget;

/**
 * The {@code BudgetRepository} interface defines the contract for operations related to budget data persistence.
 * It provides methods for saving, updating and retrieving budgets from the database.
 */
public interface BudgetRepository {

    /**
     * Saves a new budget to the database.
     *
     * @param budget the {@link Budget} object to be saved
     * @return {@code true} if the save operation was successful, {@code false} otherwise
     */
    boolean save(Budget budget);

    /**
     * Updates an existing budget in the database.
     *
     * @param updatedBudget the {@link Budget} object containing updated information
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    boolean update(Budget updatedBudget);

    /**
     * Retrieves a specific budget associated with a user by their user ID.
     *
     * @param userId the unique identifier of the user
     * @return the {@link Budget} object matching the provided user ID, or {@code null} if not found
     */
    Budget findByUserId(Long userId);
}