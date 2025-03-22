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

/**
 * Implementation of the {@link GoalRepository} interface for managing goal-related database operations.
 * This class provides methods to save, update, delete, and retrieve goal records from the database.
 */
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

    /**
     * Saves a new goal record in the database.
     *
     * @param goal the goal to be saved
     */
    @Override
    public void save(Goal goal) {
        super.persistEntity(goal, INSERT_SQL, stmt -> setGoalParameters(stmt, goal));
    }

    /**
     * Updates an existing goal record in the database.
     *
     * @param goal the goal to be updated
     * @return {@code true} if the goal was successfully updated, otherwise {@code false}
     */
    @Override
    public boolean update(Goal goal) {
        return updateRecord(UPDATE_SQL, stmt -> {
            setGoalParameters(stmt, goal);
            stmt.setLong(7, goal.getGoalId());
        });
    }

    /**
     * Deletes a goal record from the database by its ID.
     *
     * @param goalId the ID of the goal to delete
     * @return {@code true} if the goal was successfully deleted, otherwise {@code false}
     */
    @Override
    public boolean delete(Long goalId) {
        return updateRecord(DELETE_SQL, stmt -> stmt.setLong(1, goalId));
    }

    /**
     * Retrieves a goal record from the database by its ID.
     *
     * @param goalId the ID of the goal to retrieve
     * @return the goal if found, or {@code null} if not found
     */
    @Override
    public Goal findById(Long goalId) {
        return findRecordByCriteria(FIND_BY_ID_SQL, stmt -> stmt.setLong(1, goalId),
                this::mapResultSetToGoal).orElse(null);
    }

    /**
     * Retrieves all goal records associated with a specific user ID.
     *
     * @param userId the ID of the user whose goals are to be retrieved
     * @return a list of goals associated with the specified user
     */
    @Override
    public List<Goal> findByUserId(Long userId) {
        return findAllRecordsByCriteria(FIND_BY_USER_ID_SQL, List.of(userId), this::mapResultSetToGoal);
    }

    /**
     * Retrieves a goal record from the database by its ID and user ID.
     *
     * @param goalId the ID of the goal to retrieve
     * @param userId the ID of the user who owns the goal
     * @return an {@code Optional} containing the goal if found, or an empty {@code Optional} if not found
     */
    @Override
    public Optional<Goal> findByUserIdAndGoalId(Long userId, Long goalId) {
        return findRecordByCriteria(FIND_BY_USER_AND_GOAL_SQL, stmt -> {
            stmt.setLong(1, goalId);
            stmt.setLong(2, userId);
        }, this::mapResultSetToGoal);
    }

    @Override
    public List<Goal> findPaginatedGoals(Long userId, int offset, int size) {
        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(size);
        params.add(offset);
        return findAllRecordsByCriteria(FIND_BY_USER_ID_SQL_PAGINATED, params, this::mapResultSetToGoal);
    }

    @Override
    public int countAllGoals(Long userId) {
        return queryDatabase(COUNT_SQL, stmt -> {
            stmt.setLong(1, userId);
        }, rs -> {
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        });
    }

    /**
     * Sets the parameters of a prepared statement for inserting or updating a goal record.
     *
     * @param stmt the prepared statement to populate
     * @param goal the goal object providing the parameter values
     * @throws SQLException if an SQL error occurs while setting the parameters
     */
    private void setGoalParameters(PreparedStatement stmt, Goal goal) throws SQLException {
        stmt.setLong(1, goal.getUserId());
        stmt.setString(2, goal.getGoalName());
        stmt.setBigDecimal(3, goal.getTargetAmount());
        stmt.setBigDecimal(4, goal.getSavedAmount());
        stmt.setInt(5, goal.getDuration());
        stmt.setDate(6, Date.valueOf(goal.getStartTime()));
    }

    /**
     * Maps a result set row to a {@code Goal} object.
     *
     * @param rs the result set containing the goal data
     * @return a {@code Goal} object populated with the data from the result set
     * @throws SQLException if an SQL error occurs while accessing the result set
     */
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