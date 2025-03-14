package com.demo.finance.out.repository.impl;

import com.demo.finance.domain.model.Budget;
import com.demo.finance.out.repository.BudgetRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class BudgetRepositoryImpl extends BaseRepository implements BudgetRepository {

    private static final String INSERT_SQL = "INSERT INTO finance.budgets (user_id, monthly_limit, current_expenses) "
            + "VALUES (?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE finance.budgets SET user_id = ?, monthly_limit = ?, "
            + "current_expenses = ? WHERE budget_id = ?";
    private static final String DELETE_SQL = "DELETE FROM finance.budgets WHERE budget_id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM finance.budgets WHERE budget_id = ?";
    private static final String FIND_BY_USER_ID_SQL = "SELECT * FROM finance.budgets WHERE user_id = ?";

    @Override
    public boolean save(Budget budget) {
        return Boolean.TRUE.equals(executeInTransaction(conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                setBudgetParameters(stmt, budget);
                int rowsInserted = stmt.executeUpdate();
                return rowsInserted > 0;
            }
        }));
    }

    @Override
    public boolean update(Budget budget) {
        return executeUpdate(UPDATE_SQL, stmt -> {
            setBudgetParameters(stmt, budget);
            stmt.setLong(4, budget.getBudgetId());
        });
    }

    @Override
    public boolean delete(Long budgetId) {
        return executeUpdate(DELETE_SQL, stmt -> stmt.setLong(1, budgetId));
    }

    @Override
    public Optional<Budget> findById(Long budgetId) {
        return findByCriteria(FIND_BY_ID_SQL, stmt -> stmt.setLong(1, budgetId),
                this::mapResultSetToBudget);
    }

    @Override
    public Optional<Budget> findByUserId(Long userId) {
        return findByCriteria(FIND_BY_USER_ID_SQL, stmt -> stmt.setLong(1, userId),
                this::mapResultSetToBudget);
    }

    private void setBudgetParameters(PreparedStatement stmt, Budget budget) throws SQLException {
        stmt.setLong(1, budget.getUserId());
        stmt.setBigDecimal(2, budget.getMonthlyLimit());
        stmt.setBigDecimal(3, budget.getCurrentExpenses());
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