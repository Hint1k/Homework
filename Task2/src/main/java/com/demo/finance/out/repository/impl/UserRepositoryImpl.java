package com.demo.finance.out.repository.impl;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.UserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link UserRepository} interface for managing user-related database operations.
 * This class provides methods to save, update, delete, and retrieve user records from the database.
 */
public class UserRepositoryImpl extends BaseRepository implements UserRepository {

    private static final String INSERT_SQL = "INSERT INTO finance.users (name, email, password, blocked, role) "
            + "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE finance.users SET name = ?, email = ?, password = ?, "
            + "blocked = ?, role = ? WHERE user_id = ?";
    private static final String DELETE_SQL = "DELETE FROM finance.users WHERE user_id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM finance.users";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM finance.users WHERE user_id = ?";
    private static final String FIND_BY_EMAIL_SQL = "SELECT * FROM finance.users WHERE email = ?";

    /**
     * Saves a new user record in the database.
     *
     * @param user the user to be saved
     */
    @Override
    public void save(User user) {
        executeInTransaction(conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                setUserParameters(stmt, user);
                stmt.executeUpdate();
                return null;
            }
        });
    }

    /**
     * Updates an existing user record in the database.
     *
     * @param user the user to be updated
     * @return {@code true} if the user was successfully updated, otherwise {@code false}
     */
    @Override
    public boolean update(User user) {
        return executeUpdate(UPDATE_SQL, stmt -> {
            setUserParameters(stmt, user);
            stmt.setLong(6, user.getUserId());
        });
    }

    /**
     * Deletes a user record from the database by their ID.
     *
     * @param userId the ID of the user to delete
     * @return {@code true} if the user was successfully deleted, otherwise {@code false}
     */
    @Override
    public boolean delete(Long userId) {
        return executeUpdate(DELETE_SQL, stmt -> stmt.setLong(1, userId));
    }

    /**
     * Retrieves all user records from the database.
     *
     * @return a list of all users in the database
     */
    @Override
    public List<User> findAll() {
        return findAllByCriteria(FIND_ALL_SQL, new ArrayList<>(), this::mapResultSetToUser);
    }

    /**
     * Retrieves a user record from the database by their ID.
     *
     * @param userId the ID of the user to retrieve
     * @return an {@code Optional} containing the user if found, or an empty {@code Optional} if not found
     */
    @Override
    public Optional<User> findById(Long userId) {
        return findOneByCriteria(FIND_BY_ID_SQL, stmt -> stmt.setLong(1, userId),
                this::mapResultSetToUser);
    }

    /**
     * Retrieves a user record from the database by their email address.
     *
     * @param email the email address of the user to retrieve
     * @return an {@code Optional} containing the user if found, or an empty {@code Optional} if not found
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return findOneByCriteria(FIND_BY_EMAIL_SQL, stmt -> stmt.setString(1, email),
                this::mapResultSetToUser);
    }

    /**
     * Sets the parameters of a prepared statement for inserting or updating a user record.
     *
     * @param stmt the prepared statement to populate
     * @param user the user object providing the parameter values
     * @throws SQLException if an SQL error occurs while setting the parameters
     */
    private void setUserParameters(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getName());
        stmt.setString(2, user.getEmail());
        stmt.setString(3, user.getPassword());
        stmt.setBoolean(4, user.isBlocked());
        stmt.setString(5, user.getRole().getName());
    }

    /**
     * Maps a result set row to a {@code User} object.
     *
     * @param rs the result set containing the user data
     * @return a {@code User} object populated with the data from the result set
     * @throws SQLException if an SQL error occurs while accessing the result set
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getBoolean("blocked"),
                new Role(rs.getString("role"))
        );
    }
}