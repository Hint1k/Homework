package com.demo.finance.out.repository.impl;

import com.demo.finance.domain.model.Budget;
import com.demo.finance.out.repository.BudgetRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Implementation of the {@link BudgetRepository} interface for managing budget-related database operations.
 * This class provides methods to save, update, delete, and retrieve budget records from the database.
 */
public class BudgetRepositoryImpl extends BaseRepository implements BudgetRepository {

    private static final String INSERT_SQL = "INSERT INTO finance.budgets (user_id, monthly_limit, current_expenses) "
            + "VALUES (?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE finance.budgets SET user_id = ?, monthly_limit = ?, "
            + "current_expenses = ? WHERE budget_id = ?";
    private static final String DELETE_SQL = "DELETE FROM finance.budgets WHERE budget_id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM finance.budgets WHERE budget_id = ?";
    private static final String FIND_BY_USER_ID_SQL = "SELECT * FROM finance.budgets WHERE user_id = ?";

    /**
     * Saves a new budget record in the database.
     *
     * @param budget the budget to be saved
     */
    @Override
    public void save(Budget budget) {
        super.persistEntity(budget, INSERT_SQL, stmt -> setBudgetParameters(stmt, budget));
    }

    /**
     * Updates an existing budget record in the database.
     *
     * @param budget the budget to be updated
     * @return {@code true} if the budget was successfully updated, otherwise {@code false}
     */
    @Override
    public boolean update(Budget budget) {
        return updateRecord(UPDATE_SQL, stmt -> {
            setBudgetParameters(stmt, budget);
            stmt.setLong(4, budget.getBudgetId());
        });
    }

    /**
     * Deletes a budget record from the database by its ID.
     *
     * @param budgetId the ID of the budget to delete
     * @return {@code true} if the budget was successfully deleted, otherwise {@code false}
     */
    @Override
    public boolean delete(Long budgetId) {
        return updateRecord(DELETE_SQL, stmt -> stmt.setLong(1, budgetId));
    }

    /**
     * Retrieves a budget record from the database by its ID.
     *
     * @param budgetId the ID of the budget to retrieve
     * @return an {@code Optional} containing the budget if found, or an empty {@code Optional} if not found
     */
    @Override
    public Optional<Budget> findById(Long budgetId) {
        return findRecordByCriteria(FIND_BY_ID_SQL, stmt -> stmt.setLong(1, budgetId),
                this::mapResultSetToBudget);
    }

    /**
     * Retrieves a budget record from the database by the user ID.
     *
     * @param userId the ID of the user whose budget is to be retrieved
     * @return an {@code Optional} containing the budget if found, or an empty {@code Optional} if not found
     */
    @Override
    public Optional<Budget> findByUserId(Long userId) {
        return findRecordByCriteria(FIND_BY_USER_ID_SQL, stmt -> stmt.setLong(1, userId),
                this::mapResultSetToBudget);
    }

    /**
     * Sets the parameters of a prepared statement for inserting or updating a budget record.
     *
     * @param stmt   the prepared statement to populate
     * @param budget the budget object providing the parameter values
     * @throws SQLException if an SQL error occurs while setting the parameters
     */
    private void setBudgetParameters(PreparedStatement stmt, Budget budget) throws SQLException {
        stmt.setLong(1, budget.getUserId());
        stmt.setBigDecimal(2, budget.getMonthlyLimit());
        stmt.setBigDecimal(3, budget.getCurrentExpenses());
    }

    /**
     * Maps a result set row to a {@code Budget} object.
     *
     * @param rs the result set containing the budget data
     * @return a {@code Budget} object populated with the data from the result set
     * @throws SQLException if an SQL error occurs while accessing the result set
     */
    private Budget mapResultSetToBudget(ResultSet rs) throws SQLException {
        return new Budget(
                rs.getLong("budget_id"),
                rs.getLong("user_id"),
                rs.getBigDecimal("monthly_limit"),
                rs.getBigDecimal("current_expenses")
        );
    }
}