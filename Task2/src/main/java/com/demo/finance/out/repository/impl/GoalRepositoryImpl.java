package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.domain.model.Goal;
import com.demo.finance.exception.DatabaseException;
import com.demo.finance.out.repository.GoalRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GoalRepositoryImpl implements GoalRepository {

    private static final Logger log = Logger.getLogger(GoalRepositoryImpl.class.getName());

    @Override
    public void saveGoal(Goal goal) {
        String sql = "INSERT INTO finance.goals "
                + "(user_id, goal_name, target_amount, saved_amount, duration, start_time) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, goal.getUserId());
                stmt.setString(2, goal.getGoalName());
                stmt.setBigDecimal(3, goal.getTargetAmount());
                stmt.setBigDecimal(4, goal.getSavedAmount());
                stmt.setInt(5, goal.getDuration());
                stmt.setDate(6, Date.valueOf(goal.getStartTime()));

                stmt.executeUpdate();

                // Retrieve and set the generated ID
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        goal.setGoalId(rs.getLong(1));
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                log.log(Level.SEVERE, "Error saving goal", e);
                throw new DatabaseException("Error saving goal", e);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error saving goal", e);
            throw new DatabaseException("Error establishing connection or committing transaction", e);
        }
    }

    @Override
    public Optional<Goal> findGoalById(Long goalId) {
        String sql = "SELECT * FROM finance.goals WHERE goal_id = ?";
        try (Connection conn = DataSourceManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, goalId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGoal(rs));
                }
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error finding goal by id", e);
            throw new DatabaseException("Error finding goal by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Goal> findGoalByUserId(Long userId) {
        String sql = "SELECT * FROM finance.goals WHERE user_id = ?";
        List<Goal> goals = new ArrayList<>();
        try (Connection conn = DataSourceManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    goals.add(mapResultSetToGoal(rs));
                }
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error finding goal by user id", e);
            throw new DatabaseException("Error finding goals by user ID", e);
        }
        return goals;
    }

    @Override
    public void updateGoal(Goal updatedGoal) {
        String sql = "UPDATE finance.goals "
                + "SET user_id = ?, goal_name = ?, target_amount = ?, saved_amount = ?, duration = ?, start_time = ? "
                + "WHERE goal_id = ?";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, updatedGoal.getUserId());
                stmt.setString(2, updatedGoal.getGoalName());
                stmt.setBigDecimal(3, updatedGoal.getTargetAmount());
                stmt.setBigDecimal(4, updatedGoal.getSavedAmount());
                stmt.setInt(5, updatedGoal.getDuration());
                stmt.setDate(6, Date.valueOf(updatedGoal.getStartTime()));
                stmt.setLong(7, updatedGoal.getGoalId());

                stmt.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                log.log(Level.SEVERE, "Error updating goal", e);
                throw new DatabaseException("Error updating goal", e);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error updating goal", e);
            throw new DatabaseException("Error establishing connection or committing transaction", e);
        }
    }

    @Override
    public void deleteGoal(Long goalId) {
        String sql = "DELETE FROM finance.goals WHERE goal_id = ?";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, goalId);
                stmt.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                log.log(Level.SEVERE, "Error deleting goal", e);
                throw new DatabaseException("Error deleting goal by ID", e);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error deleting goal", e);
            throw new DatabaseException("Error establishing connection or committing transaction", e);
        }
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
}