package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.domain.model.Budget;
import com.demo.finance.exception.DatabaseException;
import com.demo.finance.out.repository.BudgetRepository;

import java.util.Optional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The {@code BudgetRepositoryImpl} class provides an in-memory implementation of the {@code BudgetRepository}.
 * It uses a {@code ConcurrentHashMap} to store budgets, indexed by the user ID.
 * This implementation provides methods to save and retrieve budgets for users.
 */
public class BudgetRepositoryImpl implements BudgetRepository {

    @Override
    public boolean save(Budget budget) {
        String sql = "INSERT INTO budgets (budget_id, user_id, monthly_limit, current_expenses) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);  // Start transaction
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, budget.getBudgetId());
                stmt.setLong(2, budget.getUserId());
                stmt.setBigDecimal(3, budget.getMonthlyLimit());
                stmt.setBigDecimal(4, budget.getCurrentExpenses());

                int rowsInserted = stmt.executeUpdate();
                conn.commit();  // Commit transaction
                return rowsInserted > 0;
            } catch (SQLException e) {
                conn.rollback();  // Rollback transaction in case of error
                throw new DatabaseException("Error saving budget", e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error establishing connection or committing transaction", e);
        }
    }

    @Override
    public Optional<Budget> findByUserId(Long userId) {
        String sql = "SELECT * FROM budgets WHERE user_id = ?";
        try (Connection conn = DataSourceManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBudget(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding budget by user ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Budget> findById(Long budgetId) {
        String sql = "SELECT * FROM budgets WHERE budget_id = ?";
        try (Connection conn = DataSourceManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, budgetId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBudget(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding budget by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Long budgetId, Budget updatedBudget) {
        String sql = "UPDATE budgets SET user_id = ?, monthly_limit = ?, current_expenses = ? WHERE budget_id = ?";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);  // Start transaction
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, updatedBudget.getUserId());
                stmt.setBigDecimal(2, updatedBudget.getMonthlyLimit());
                stmt.setBigDecimal(3, updatedBudget.getCurrentExpenses());
                stmt.setLong(4, budgetId);

                int rowsUpdated = stmt.executeUpdate();
                conn.commit();  // Commit transaction
                return rowsUpdated > 0;
            } catch (SQLException e) {
                conn.rollback();  // Rollback transaction in case of error
                throw new DatabaseException("Error updating budget", e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error establishing connection or committing transaction", e);
        }
    }

    @Override
    public boolean delete(Long budgetId) {
        String sql = "DELETE FROM budgets WHERE budget_id = ?";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);  // Start transaction
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, budgetId);
                int rowsDeleted = stmt.executeUpdate();
                conn.commit();  // Commit transaction
                return rowsDeleted > 0;
            } catch (SQLException e) {
                conn.rollback();  // Rollback transaction in case of error
                throw new DatabaseException("Error deleting budget", e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error establishing connection or committing transaction", e);
        }
    }

    private Budget mapResultSetToBudget(ResultSet rs) throws SQLException {
        return new Budget(
                rs.getLong("budget_id"),
                rs.getLong("user_id"),
                rs.getBigDecimal("monthly_limit"),
                rs.getBigDecimal("current_expenses")
        );
    }
}