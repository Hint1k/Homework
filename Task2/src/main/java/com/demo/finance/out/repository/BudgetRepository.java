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
     * Deletes the budget associated with the specified budget ID.
     *
     * @param budgetId the ID of the budget to delete
     * @return {@code true} if the budget was successfully deleted, {@code false} otherwise
     */
    boolean delete(Long budgetId);

    /**
     * Updates the specified {@code Budget} object.
     *
     * @param updatedBudget the {@code Budget} object with updated details
     * @return {@code true} if the budget was successfully updated, {@code false} otherwise
     */
    boolean update(Budget updatedBudget);

    /**
     * Retrieves the {@code Budget} associated with the specified budget ID.
     *
     * @param budgetId the ID of the budget to retrieve
     * @return an {@code Optional<Budget>} containing the budget if found,
     * or an empty {@code Optional} if no budget is found
     */
    Optional<Budget> findById(Long budgetId);

    /**
     * Retrieves the {@code Budget} associated with the specified user ID.
     *
     * @param userId the ID of the user whose budget is to be retrieved
     * @return an {@code Optional<Budget>} containing the user's budget if it exists,
     * or an empty {@code Optional} if no budget is found
     */
    Optional<Budget> findByUserId(Long userId);
}