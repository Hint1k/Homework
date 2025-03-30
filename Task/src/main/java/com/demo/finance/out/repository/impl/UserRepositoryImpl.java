package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code UserRepositoryImpl} class implements the {@link UserRepository} interface
 * and provides concrete implementations for user data persistence operations.
 * It interacts directly with the database using SQL queries to perform CRUD operations on user data.
 */
@Repository
public class UserRepositoryImpl extends BaseRepository implements UserRepository {

    private static final String INSERT_SQL = "INSERT INTO finance.users "
            + "(name, email, password, blocked, role, version) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE finance.users SET name = ?, email = ?, password = ?, "
            + "blocked = ?, role = ?, version = ? WHERE user_id = ?";
    private static final String DELETE_SQL = "DELETE FROM finance.users WHERE user_id = ?";
    private static final String FIND_ALL_SQL_PAGINATED = "SELECT * FROM finance.users LIMIT ? OFFSET ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM finance.users WHERE user_id = ?";
    private static final String FIND_BY_EMAIL_SQL = "SELECT * FROM finance.users WHERE email = ?";
    private static final String COUNT_SQL = "SELECT COUNT(*) AS total FROM finance.users";

    /**
     * Constructs a new {@code UserRepositoryImpl} instance with the required dependency
     * for managing database connections.
     *
     * @param dataSourceManager the manager responsible for providing database connections
     */
    @Autowired
    public UserRepositoryImpl(DataSourceManager dataSourceManager) {
        super(dataSourceManager);
    }

    /**
     * Saves a new user to the database by executing the corresponding SQL insert query.
     *
     * @param user the {@link User} object to be saved
     */
    @Override
    public void save(User user) {
        super.persistEntity(user, INSERT_SQL, stmt -> setUserParameters(stmt, user));
    }

    /**
     * Updates an existing user in the database by executing the corresponding SQL update query.
     *
     * @param user the {@link User} object containing updated information
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    @Override
    public boolean update(User user) {
        return updateRecord(UPDATE_SQL, stmt -> {
            setUserParameters(stmt, user);
            stmt.setLong(7, user.getUserId());
        });
    }

    /**
     * Deletes a user from the database based on their unique user ID.
     *
     * @param userId the unique identifier of the user to delete
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    @Override
    public boolean delete(Long userId) {
        return updateRecord(DELETE_SQL, stmt -> stmt.setLong(1, userId));
    }

    /**
     * Retrieves a paginated list of users from the database.
     *
     * @param offset the starting index for pagination (zero-based)
     * @param size   the maximum number of users to retrieve
     * @return a {@link List} of {@link User} objects representing the paginated results
     */
    @Override
    public List<User> findAll(int offset, int size) {
        List<Object> params = new ArrayList<>();
        params.add(size);
        params.add(offset);
        return findAllRecordsByCriteria(FIND_ALL_SQL_PAGINATED, params, this::mapResultSetToUser);
    }

    /**
     * Retrieves the total count of users in the database.
     *
     * @return the total number of users as an integer
     */
    @Override
    public int getTotalUserCount() {
        return queryDatabase(COUNT_SQL, stmt -> {
        }, rs -> {
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        });
    }

    /**
     * Retrieves a specific user by their unique user ID.
     *
     * @param userId the unique identifier of the user
     * @return the {@link User} object matching the provided user ID, or {@code null} if not found
     */
    @Override
    public User findById(Long userId) {
        return findRecordByCriteria(FIND_BY_ID_SQL, stmt -> stmt.setLong(1, userId),
                this::mapResultSetToUser).orElse(null);
    }

    /**
     * Retrieves a specific user by their email address.
     *
     * @param email the email address of the user
     * @return the {@link User} object matching the provided email, or {@code null} if not found
     */
    @Override
    public User findByEmail(String email) {
        return findRecordByCriteria(FIND_BY_EMAIL_SQL, stmt -> stmt.setString(1, email),
                this::mapResultSetToUser).orElse(null);
    }

    /**
     * Sets the parameters for a prepared SQL statement based on the provided user data.
     *
     * @param stmt the {@link PreparedStatement} to populate with parameters
     * @param user the {@link User} object containing the data to set
     * @throws SQLException if an error occurs while setting parameters
     */
    private void setUserParameters(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getName());
        stmt.setString(2, user.getEmail());
        stmt.setString(3, user.getPassword());
        stmt.setBoolean(4, user.isBlocked());
        stmt.setString(5, user.getRole().getName());
        stmt.setLong(6, user.getVersion());
    }

    /**
     * Maps a database result set row to a {@link User} object.
     *
     * @param rs the {@link ResultSet} containing the user data
     * @return a {@link User} object populated with data from the result set
     * @throws SQLException if an error occurs while accessing the result set
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getBoolean("blocked"),
                new Role(rs.getString("role")),
                rs.getLong("version")
        );
    }
}