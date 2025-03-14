package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.domain.model.Goal;
import com.demo.finance.exception.DatabaseException;
import com.demo.finance.out.repository.GoalRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GoalRepositoryImpl implements GoalRepository {

    private static final Logger log = Logger.getLogger(GoalRepositoryImpl.class.getName());

    private static final String INSERT_GOAL_SQL = "INSERT INTO finance.goals "
            + "(user_id, goal_name, target_amount, saved_amount, duration, start_time) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_GOAL_SQL = "UPDATE finance.goals "
            + "SET user_id = ?, goal_name = ?, target_amount = ?, saved_amount = ?, duration = ?, start_time = ? "
            + "WHERE goal_id = ?";
    private static final String DELETE_GOAL_SQL = "DELETE FROM finance.goals WHERE goal_id = ?";
    private static final String FIND_GOAL_BY_ID_SQL = "SELECT * FROM finance.goals WHERE goal_id = ?";
    private static final String FIND_GOALS_BY_USER_ID_SQL = "SELECT * FROM finance.goals WHERE user_id = ?";

    @Override
    public void save(Goal goal) {
        executeInTransaction(getConnection(), conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_GOAL_SQL,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                setGoalParameters(stmt, goal);
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        goal.setGoalId(rs.getLong(1));
                    }
                }
            }
            return null;
        });
    }

    @Override
    public void update(Goal updatedGoal) {
        executeUpdate(UPDATE_GOAL_SQL, stmt -> {
            setGoalParameters(stmt, updatedGoal);
            stmt.setLong(7, updatedGoal.getGoalId());
        });
    }

    @Override
    public void delete(Long goalId) {
        executeUpdate(DELETE_GOAL_SQL, stmt -> stmt.setLong(1, goalId));
    }

    @Override
    public Optional<Goal> findById(Long goalId) {
        return findGoalByCriteria(FIND_GOAL_BY_ID_SQL, stmt -> stmt.setLong(1, goalId));
    }

    @Override
    public List<Goal> findByUserId(Long userId) {
        return executeInTransaction(getConnection(), conn -> {
            List<Goal> goals = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(FIND_GOALS_BY_USER_ID_SQL)) {
                stmt.setLong(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        goals.add(mapResultSetToGoal(rs));
                    }
                }
            }
            return goals;
        });
    }

    private Goal mapResultSetToGoal(ResultSet rs) throws SQLException {
        return new Goal(
                rs.getLong("goal_id"),
                rs.getLong("user_id"),
                rs.getString("goal_name"),
                rs.getBigDecimal("target_amount"),
                rs.getBigDecimal("saved_amount"),
                rs.getInt("duration"),
                rs.getDate("start_time").toLocalDate()
        );
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
            return null;
        }
    }

    private void setGoalParameters(PreparedStatement stmt, Goal goal) throws SQLException {
        stmt.setLong(1, goal.getUserId());
        stmt.setString(2, goal.getGoalName());
        stmt.setBigDecimal(3, goal.getTargetAmount());
        stmt.setBigDecimal(4, goal.getSavedAmount());
        stmt.setInt(5, goal.getDuration());
        stmt.setDate(6, Date.valueOf(goal.getStartTime()));
    }

    private void executeUpdate(String sql, PreparedStatementSetter setter) {
        executeInTransaction(getConnection(), conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setter.setValues(stmt);
                return stmt.executeUpdate() > 0;
            }
        });
    }

    private Optional<Goal> findGoalByCriteria(String sql, PreparedStatementSetter setter) {
        return executeInTransaction(getConnection(), conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setter.setValues(stmt);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapResultSetToGoal(rs));
                    }
                }
            }
            return Optional.empty();
        });
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