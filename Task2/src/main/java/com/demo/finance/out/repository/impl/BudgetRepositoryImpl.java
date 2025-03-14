package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.domain.model.Budget;
import com.demo.finance.exception.DatabaseException;
import com.demo.finance.out.repository.BudgetRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BudgetRepositoryImpl implements BudgetRepository {

    private static final Logger log = Logger.getLogger(BudgetRepositoryImpl.class.getName());

    private static final String INSERT_BUDGET_SQL = "INSERT INTO finance.budgets "
            + "(user_id, monthly_limit, current_expenses)  VALUES (?, ?, ?)";
    private static final String UPDATE_BUDGET_SQL = "UPDATE finance.budgets "
            + "SET user_id = ?, monthly_limit = ?, current_expenses = ? WHERE budget_id = ?";
    private static final String DELETE_BUDGET_SQL = "DELETE FROM finance.budgets WHERE budget_id = ?";
    private static final String FIND_BUDGET_BY_ID_SQL = "SELECT * FROM finance.budgets WHERE budget_id = ?";
    private static final String FIND_BUDGET_BY_USER_ID_SQL = "SELECT * FROM finance.budgets WHERE user_id = ?";

    @Override
    public boolean save(Budget budget) {
        return Boolean.TRUE.equals(executeInTransaction(getConnection(), conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_BUDGET_SQL,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                setBudgetParameters(stmt, budget);
                int rowsInserted = stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        budget.setBudgetId(rs.getLong(1));
                    }
                }
                return rowsInserted > 0;
            }
        }));
    }

    @Override
    public boolean update(Budget updatedBudget) {
        return executeUpdate(UPDATE_BUDGET_SQL, stmt -> {
            setBudgetParameters(stmt, updatedBudget);
            stmt.setLong(4, updatedBudget.getBudgetId());
        });
    }

    @Override
    public boolean delete(Long budgetId) {
        return executeUpdate(DELETE_BUDGET_SQL, stmt -> stmt.setLong(1, budgetId));
    }

    @Override
    public Optional<Budget> findById(Long budgetId) {
        return findBudgetByCriteria(FIND_BUDGET_BY_ID_SQL, stmt ->
                stmt.setLong(1, budgetId));
    }

    @Override
    public Optional<Budget> findByUserId(Long userId) {
        return findBudgetByCriteria(FIND_BUDGET_BY_USER_ID_SQL, stmt ->
                stmt.setLong(1, userId));
    }

    private void setBudgetParameters(PreparedStatement stmt, Budget budget) throws SQLException {
        stmt.setLong(1, budget.getUserId());
        stmt.setBigDecimal(2, budget.getMonthlyLimit());
        stmt.setBigDecimal(3, budget.getCurrentExpenses());
    }

    private <T> T executeInTransaction(Connection conn, TransactionalOperation<T> operation) {
        try {
            conn.setAutoCommit(false);
            T result = operation.execute(conn);
            conn.commit();
            return result;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                logError("Rollback failed", rollbackEx);
            }
            logError("Transaction failed", e);
            return null; // Or throw an exception if appropriate
        }
    }

    private boolean executeUpdate(String sql, PreparedStatementSetter setter) {
        return Boolean.TRUE.equals(executeInTransaction(getConnection(), conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setter.setValues(stmt);
                return stmt.executeUpdate() > 0;
            }
        }));
    }

    private Optional<Budget> findBudgetByCriteria(String sql, PreparedStatementSetter setter) {
        return executeInTransaction(getConnection(), conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setter.setValues(stmt);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapResultSetToBudget(rs));
                    }
                }
            }
            return Optional.empty();
        });
    }

    private Budget mapResultSetToBudget(ResultSet rs) throws SQLException {
        return new Budget(
                rs.getLong("budget_id"),
                rs.getLong("user_id"),
                rs.getBigDecimal("monthly_limit"),
                rs.getBigDecimal("current_expenses")
        );
    }

    private Connection getConnection() {
        try {
            return DataSourceManager.getConnection();
        } catch (SQLException e) {
            logError("Failed to obtain database connection", e);
            throw new DatabaseException("Error obtaining database connection", e);
        }
    }

    @FunctionalInterface
    private interface TransactionalOperation<T> {
        T execute(Connection conn) throws SQLException;
    }

    @FunctionalInterface
    private interface PreparedStatementSetter {
        void setValues(PreparedStatement stmt) throws SQLException;
    }

    private void logError(String message, Exception e) {
        log.log(Level.SEVERE, message + ": " + e.getMessage(), e);
        throw new DatabaseException(message, e);
    }
}