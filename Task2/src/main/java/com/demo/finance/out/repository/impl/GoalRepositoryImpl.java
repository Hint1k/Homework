package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.domain.model.Goal;
import com.demo.finance.exception.DatabaseException;
import com.demo.finance.out.repository.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GoalRepositoryImpl implements GoalRepository {

    @Override
    public void save(Goal goal) {
        String sql = "INSERT INTO goals (goal_id, user_id, goal_name, target_amount, saved_amount, duration, start_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);  // Start transaction
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, goal.getGoalId());
                stmt.setLong(2, goal.getUserId());
                stmt.setString(3, goal.getGoalName());
                stmt.setBigDecimal(4, goal.getTargetAmount());
                stmt.setBigDecimal(5, goal.getSavedAmount());
                stmt.setInt(6, goal.getDuration());
                stmt.setDate(7, Date.valueOf(goal.getStartTime()));

                stmt.executeUpdate();
                conn.commit();  // Commit transaction
            } catch (SQLException e) {
                conn.rollback();  // Rollback transaction in case of error
                throw new DatabaseException("Error saving goal", e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error establishing connection or committing transaction", e);
        }
    }

    @Override
    public Optional<Goal> findById(Long goalId) {
        String sql = "SELECT * FROM goals WHERE goal_id = ?";
        try (Connection conn = DataSourceManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, goalId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGoal(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding goal by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Goal> findByUserId(Long userId) {
        String sql = "SELECT * FROM goals WHERE user_id = ?";
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
            throw new DatabaseException("Error finding goals by user ID", e);
        }
        return goals;
    }

    @Override
    public void update(Long goalId, Goal updatedGoal) {
        String sql = "UPDATE goals SET user_id = ?, goal_name = ?, target_amount = ?, saved_amount = ?, duration = ?, start_time = ? WHERE goal_id = ?";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);  // Start transaction
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, updatedGoal.getUserId());
                stmt.setString(2, updatedGoal.getGoalName());
                stmt.setBigDecimal(3, updatedGoal.getTargetAmount());
                stmt.setBigDecimal(4, updatedGoal.getSavedAmount());
                stmt.setInt(5, updatedGoal.getDuration());
                stmt.setDate(6, Date.valueOf(updatedGoal.getStartTime()));
                stmt.setLong(7, goalId);

                stmt.executeUpdate();
                conn.commit();  // Commit transaction
            } catch (SQLException e) {
                conn.rollback();  // Rollback transaction in case of error
                throw new DatabaseException("Error updating goal", e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error establishing connection or committing transaction", e);
        }
    }

    @Override
    public void delete(Long goalId) {
        String sql = "DELETE FROM goals WHERE goal_id = ?";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);  // Start transaction
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, goalId);
                stmt.executeUpdate();
                conn.commit();  // Commit transaction
            } catch (SQLException e) {
                conn.rollback();  // Rollback transaction in case of error
                throw new DatabaseException("Error deleting goal by ID", e);
            }
        } catch (SQLException e) {
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