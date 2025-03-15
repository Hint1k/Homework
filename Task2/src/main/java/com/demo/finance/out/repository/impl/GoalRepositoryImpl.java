package com.demo.finance.out.repository.impl;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.out.repository.GoalRepository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GoalRepositoryImpl extends BaseRepository implements GoalRepository {

    private static final String INSERT_SQL = "INSERT INTO finance.goals (user_id, goal_name, target_amount, "
            + "saved_amount, duration, start_time) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE finance.goals SET user_id = ?, goal_name = ?, target_amount = ?, "
            + " saved_amount = ?, duration = ?, start_time = ? WHERE goal_id = ?";
    private static final String DELETE_SQL = "DELETE FROM finance.goals WHERE goal_id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM finance.goals WHERE goal_id = ?";
    private static final String FIND_BY_USER_ID_SQL = "SELECT * FROM finance.goals WHERE user_id = ?";
    private static final String FIND_BY_USER_AND_GOAL_SQL = "SELECT * FROM finance.goals WHERE "
            + "goal_id = ? AND user_id = ?";

    @Override
    public void save(Goal goal) {
        executeInTransaction(conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                setGoalParameters(stmt, goal);
                stmt.executeUpdate();
                return null;
            }
        });
    }

    @Override
    public boolean update(Goal goal) {
        return executeUpdate(UPDATE_SQL, stmt -> {
            setGoalParameters(stmt, goal);
            stmt.setLong(7, goal.getGoalId());
        });
    }

    @Override
    public boolean delete(Long goalId) {
        return executeUpdate(DELETE_SQL, stmt -> stmt.setLong(1, goalId));
    }

    @Override
    public Goal findById(Long goalId) {
        return findByCriteria(FIND_BY_ID_SQL, stmt -> stmt.setLong(1, goalId),
                this::mapResultSetToGoal).orElse(null);
    }

    @Override
    public List<Goal> findByUserId(Long userId) {
        return executeInTransaction(conn -> {
            List<Goal> goals = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(FIND_BY_USER_ID_SQL)) {
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

    @Override
    public Optional<Goal> findByUserIdAndGoalId(Long goalId, Long userId) {
        return findByCriteria(FIND_BY_USER_AND_GOAL_SQL, stmt -> {
            stmt.setLong(1, goalId);
            stmt.setLong(2, userId);
        }, this::mapResultSetToGoal);
    }

    private void setGoalParameters(PreparedStatement stmt, Goal goal) throws SQLException {
        stmt.setLong(1, goal.getUserId());
        stmt.setString(2, goal.getGoalName());
        stmt.setBigDecimal(3, goal.getTargetAmount());
        stmt.setBigDecimal(4, goal.getSavedAmount());
        stmt.setInt(5, goal.getDuration());
        stmt.setDate(6, Date.valueOf(goal.getStartTime()));
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