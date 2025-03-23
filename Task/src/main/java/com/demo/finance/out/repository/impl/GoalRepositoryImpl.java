package com.demo.finance.out.repository.impl;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.out.repository.GoalRepository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GoalRepositoryImpl extends BaseRepository implements GoalRepository {

    private static final String INSERT_SQL = "INSERT INTO finance.goals (user_id, goal_name, target_amount, "
            + "saved_amount, duration, start_time) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE finance.goals SET user_id = ?, goal_name = ?, target_amount = ?, "
            + " saved_amount = ?, duration = ?, start_time = ? WHERE goal_id = ?";
    private static final String DELETE_SQL = "DELETE FROM finance.goals WHERE goal_id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM finance.goals WHERE goal_id = ?";
    private static final String FIND_BY_USER_ID_SQL = "SELECT * FROM finance.goals WHERE user_id = ?";
    private static final String FIND_BY_USER_ID_SQL_PAGINATED = "SELECT * FROM finance.goals WHERE user_id = ? "
            + " LIMIT ? OFFSET ?";
    private static final String FIND_BY_USER_AND_GOAL_SQL = "SELECT * FROM finance.goals WHERE "
            + "goal_id = ? AND user_id = ?";
    private static final String COUNT_SQL = "SELECT COUNT(*) AS total FROM finance.users WHERE user_id = ?";

    @Override
    public Long save(Goal goal) {
        return insertRecord(INSERT_SQL, stmt -> setGoalParameters(stmt, goal));
    }

    @Override
    public boolean update(Goal goal) {
        return updateRecord(UPDATE_SQL, stmt -> {
            setGoalParameters(stmt, goal);
            stmt.setLong(7, goal.getGoalId());
        });
    }

    @Override
    public boolean delete(Long goalId) {
        return updateRecord(DELETE_SQL, stmt -> stmt.setLong(1, goalId));
    }

    @Override
    public Goal findById(Long goalId) {
        return findRecordByCriteria(FIND_BY_ID_SQL, stmt -> stmt.setLong(1, goalId),
                this::mapResultSetToGoal).orElse(null);
    }

    @Override
    public List<Goal> findByUserId(Long userId) {
        return findAllRecordsByCriteria(FIND_BY_USER_ID_SQL, List.of(userId), this::mapResultSetToGoal);
    }

    @Override
    public List<Goal> findByUserId(Long userId, int offset, int size) {
        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(size);
        params.add(offset);
        return findAllRecordsByCriteria(FIND_BY_USER_ID_SQL_PAGINATED, params, this::mapResultSetToGoal);
    }

    @Override
    public Goal findByUserIdAndGoalId(Long userId, Long goalId) {
        return findRecordByCriteria(FIND_BY_USER_AND_GOAL_SQL, stmt -> {
            stmt.setLong(1, goalId);
            stmt.setLong(2, userId);
        }, this::mapResultSetToGoal).orElse(null);
    }

    @Override
    public int getTotalGoalCountForUser(Long userId) {
        return queryDatabase(COUNT_SQL, stmt -> stmt.setLong(1, userId), rs -> {
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        });
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