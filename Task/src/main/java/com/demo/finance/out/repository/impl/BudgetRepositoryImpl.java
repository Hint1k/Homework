package com.demo.finance.out.repository.impl;

import com.demo.finance.domain.model.Budget;
import com.demo.finance.out.repository.BudgetRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        Long generatedId = insertRecord(INSERT_SQL, stmt -> setBudgetParameters(stmt, budget));
        if (generatedId != null) {
            setGeneratedId(budget, generatedId);
            return true;
        }
        return false;
    }

    @Override
    public boolean update(Budget budget) {
        return updateRecord(UPDATE_SQL, stmt -> {
            setBudgetParameters(stmt, budget);
            stmt.setLong(4, budget.getBudgetId());
        });
    }

    @Override
    public boolean delete(Long budgetId) {
        return updateRecord(DELETE_SQL, stmt -> stmt.setLong(1, budgetId));
    }

    @Override
    public Budget findById(Long budgetId) {
        return findRecordByCriteria(FIND_BY_ID_SQL, stmt -> stmt.setLong(1, budgetId),
                this::mapResultSetToBudget).orElse(null);
    }

    @Override
    public Budget findByUserId(Long userId) {
        return findRecordByCriteria(FIND_BY_USER_ID_SQL, stmt -> stmt.setLong(1, userId),
                this::mapResultSetToBudget).orElse(null);
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