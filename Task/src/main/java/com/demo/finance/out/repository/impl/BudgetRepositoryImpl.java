package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.domain.model.Budget;
import com.demo.finance.out.repository.BudgetRepository;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The {@code BudgetRepositoryImpl} class implements the {@link BudgetRepository} interface
 * and provides concrete implementations for budget data persistence operations.
 * It interacts directly with the database using SQL queries to perform CRUD operations on budget data.
 */
@Repository
public class BudgetRepositoryImpl extends BaseRepository implements BudgetRepository {

    private static final String INSERT_SQL = "INSERT INTO finance.budgets (user_id, monthly_limit, current_expenses) "
            + "VALUES (?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE finance.budgets SET user_id = ?, monthly_limit = ?, "
            + "current_expenses = ? WHERE budget_id = ?";
    private static final String DELETE_SQL = "DELETE FROM finance.budgets WHERE budget_id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM finance.budgets WHERE budget_id = ?";
    private static final String FIND_BY_USER_ID_SQL = "SELECT * FROM finance.budgets WHERE user_id = ?";

    /**
     * Constructs a new {@code BudgetRepositoryImpl} instance with the required dependency
     * for managing database connections.
     *
     * @param dataSourceManager the manager responsible for providing database connections
     */
    public BudgetRepositoryImpl(DataSourceManager dataSourceManager) {
        super(dataSourceManager);
    }

    /**
     * Saves a new budget to the database by executing the corresponding SQL insert query.
     *
     * @param budget the {@link Budget} object to be saved
     * @return {@code true} if the save operation was successful, {@code false} otherwise
     */
    @Override
    public boolean save(Budget budget) {
        Long generatedId = insertRecord(INSERT_SQL, stmt -> setBudgetParameters(stmt, budget));
        if (generatedId != null) {
            setGeneratedId(budget, generatedId);
            return true;
        }
        return false;
    }

    /**
     * Updates an existing budget in the database by executing the corresponding SQL update query.
     *
     * @param budget the {@link Budget} object containing updated information
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    @Override
    public boolean update(Budget budget) {
        return updateRecord(UPDATE_SQL, stmt -> {
            setBudgetParameters(stmt, budget);
            stmt.setLong(4, budget.getBudgetId());
        });
    }

    /**
     * Deletes a budget from the database based on its unique budget ID.
     *
     * @param budgetId the unique identifier of the budget to delete
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    @Override
    public boolean delete(Long budgetId) {
        return updateRecord(DELETE_SQL, stmt -> stmt.setLong(1, budgetId));
    }

    /**
     * Retrieves a specific budget by its unique budget ID.
     *
     * @param budgetId the unique identifier of the budget
     * @return the {@link Budget} object matching the provided budget ID, or {@code null} if not found
     */
    @Override
    public Budget findById(Long budgetId) {
        return findRecordByCriteria(FIND_BY_ID_SQL, stmt -> stmt.setLong(1, budgetId),
                this::mapResultSetToBudget).orElse(null);
    }

    /**
     * Retrieves a specific budget associated with a user by their user ID.
     *
     * @param userId the unique identifier of the user
     * @return the {@link Budget} object matching the provided user ID, or {@code null} if not found
     */
    @Override
    public Budget findByUserId(Long userId) {
        return findRecordByCriteria(FIND_BY_USER_ID_SQL, stmt -> stmt.setLong(1, userId),
                this::mapResultSetToBudget).orElse(null);
    }

    /**
     * Sets the parameters for a prepared SQL statement based on the provided budget data.
     *
     * @param stmt   the {@link PreparedStatement} to populate with parameters
     * @param budget the {@link Budget} object containing the data to set
     * @throws SQLException if an error occurs while setting parameters
     */
    private void setBudgetParameters(PreparedStatement stmt, Budget budget) throws SQLException {
        stmt.setLong(1, budget.getUserId());
        stmt.setBigDecimal(2, budget.getMonthlyLimit());
        stmt.setBigDecimal(3, budget.getCurrentExpenses());
    }

    /**
     * Maps a database result set row to a {@link Budget} object.
     *
     * @param rs the {@link ResultSet} containing the budget data
     * @return a {@link Budget} object populated with data from the result set
     * @throws SQLException if an error occurs while accessing the result set
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