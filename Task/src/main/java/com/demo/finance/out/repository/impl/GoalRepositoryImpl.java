package com.demo.finance.out.repository.impl;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.out.repository.GoalRepository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code GoalRepositoryImpl} class implements the {@link GoalRepository} interface
 * and provides concrete implementations for goal data persistence operations.
 * It interacts directly with the database using SQL queries to perform CRUD operations on goal data.
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
     * Saves a new goal to the database by executing the corresponding SQL insert query.
     *
     * @param goal the {@link Goal} object to be saved
     * @return the unique identifier ({@code Long}) of the newly saved goal
     */
    @Override
    public Long save(Goal goal) {
        return insertRecord(INSERT_SQL, stmt -> setGoalParameters(stmt, goal));
    }

    /**
     * Updates an existing goal in the database by executing the corresponding SQL update query.
     *
     * @param goal the {@link Goal} object containing updated information
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    @Override
    public boolean update(Goal goal) {
        return updateRecord(UPDATE_SQL, stmt -> {
            setGoalParameters(stmt, goal);
            stmt.setLong(7, goal.getGoalId());
        });
    }

    /**
     * Deletes a goal from the database based on its unique goal ID.
     *
     * @param goalId the unique identifier of the goal to delete
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    @Override
    public boolean delete(Long goalId) {
        return updateRecord(DELETE_SQL, stmt -> stmt.setLong(1, goalId));
    }

    /**
     * Retrieves a specific goal by its unique goal ID.
     *
     * @param goalId the unique identifier of the goal
     * @return the {@link Goal} object matching the provided goal ID, or {@code null} if not found
     */
    @Override
    public Goal findById(Long goalId) {
        return findRecordByCriteria(FIND_BY_ID_SQL, stmt -> stmt.setLong(1, goalId),
                this::mapResultSetToGoal).orElse(null);
    }

    /**
     * Retrieves all goals associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @return a {@link List} of {@link Goal} objects associated with the user
     */
    @Override
    public List<Goal> findByUserId(Long userId) {
        return findAllRecordsByCriteria(FIND_BY_USER_ID_SQL, List.of(userId), this::mapResultSetToGoal);
    }

    /**
     * Retrieves a paginated list of goals associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @param offset the starting index for pagination (zero-based)
     * @param size   the maximum number of goals to retrieve
     * @return a {@link List} of {@link Goal} objects representing the paginated results
     */
    @Override
    public List<Goal> findByUserId(Long userId, int offset, int size) {
        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(size);
        params.add(offset);
        return findAllRecordsByCriteria(FIND_BY_USER_ID_SQL_PAGINATED, params, this::mapResultSetToGoal);
    }

    /**
     * Retrieves a specific goal associated with a user by their user ID and goal ID.
     *
     * @param userId the unique identifier of the user
     * @param goalId the unique identifier of the goal
     * @return the {@link Goal} object matching the provided user ID and goal ID, or {@code null} if not found
     */
    @Override
    public Goal findByUserIdAndGoalId(Long userId, Long goalId) {
        return findRecordByCriteria(FIND_BY_USER_AND_GOAL_SQL, stmt -> {
            stmt.setLong(1, goalId);
            stmt.setLong(2, userId);
        }, this::mapResultSetToGoal).orElse(null);
    }

    /**
     * Retrieves the total count of goals associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @return the total number of goals as an integer
     */
    @Override
    public int getTotalGoalCountForUser(Long userId) {
        return queryDatabase(COUNT_SQL, stmt -> stmt.setLong(1, userId), rs -> {
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        });
    }

    /**
     * Sets the parameters for a prepared SQL statement based on the provided goal data.
     *
     * @param stmt the {@link PreparedStatement} to populate with parameters
     * @param goal the {@link Goal} object containing the data to set
     * @throws SQLException if an error occurs while setting parameters
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
     * Maps a database result set row to a {@link Goal} object.
     *
     * @param rs the {@link ResultSet} containing the goal data
     * @return a {@link Goal} object populated with data from the result set
     * @throws SQLException if an error occurs while accessing the result set
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