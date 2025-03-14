package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.exception.DatabaseException;
import com.demo.finance.out.repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code UserRepositoryImpl} class provides an in-memory implementation of the {@code UserRepository} interface.
 * It manages a collection of {@code User} objects using a concurrent hash map.
 */
public class UserRepositoryImpl implements UserRepository {

    private static final Logger log = Logger.getLogger(UserRepositoryImpl.class.getName());

    private static final String INSERT_USER_SQL = "INSERT INTO finance.users (name, email, password, blocked, role) "
            + "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_USER_SQL = "UPDATE finance.users "
            + "SET name = ?, email = ?, password = ?, blocked = ?, role = ? WHERE user_id = ?";
    private static final String DELETE_USER_SQL = "DELETE FROM finance.users WHERE user_id = ?";
    private static final String FIND_ALL_USERS_SQL = "SELECT * FROM finance.users";
    private static final String FIND_USER_BY_ID_SQL = "SELECT * FROM finance.users WHERE user_id = ?";
    private static final String FIND_USER_BY_EMAIL_SQL = "SELECT * FROM finance.users WHERE email = ?";

    @Override
    public void save(User user) {
        executeInTransaction(getConnection(), conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {
                setUserParameters(stmt, user);
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserId(rs.getLong(1));
                    }
                }
            }
            return null;
        });
    }

    @Override
    public boolean update(User user) {
        return executeUpdate(UPDATE_USER_SQL, stmt -> {
            setUserParameters(stmt, user);
            stmt.setLong(6, user.getUserId());
        });
    }

    @Override
    public boolean delete(Long userId) {
        return executeUpdate(DELETE_USER_SQL, stmt -> stmt.setLong(1, userId));
    }

    @Override
    public List<User> findAll() {
        return executeInTransaction(getConnection(), conn -> {
            List<User> users = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(FIND_ALL_USERS_SQL);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
            return users;
        });
    }

    @Override
    public Optional<User> findById(Long userId) {
        return findUserByCriteria(FIND_USER_BY_ID_SQL, stmt -> stmt.setLong(1, userId));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findUserByCriteria(FIND_USER_BY_EMAIL_SQL, stmt ->
                stmt.setString(1, email));
    }

    private void setUserParameters(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getName());
        stmt.setString(2, user.getEmail());
        stmt.setString(3, user.getPassword());
        stmt.setBoolean(4, user.isBlocked());
        stmt.setString(5, user.getRole().getName());
    }

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

    private boolean executeUpdate(String sql, PreparedStatementSetter setter) {
        return Boolean.TRUE.equals(executeInTransaction(getConnection(), conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setter.setValues(stmt);
                return stmt.executeUpdate() > 0;
            }
        }));
    }

    private Optional<User> findUserByCriteria(String sql, PreparedStatementSetter setter) {
        return executeInTransaction(getConnection(), conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setter.setValues(stmt);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapResultSetToUser(rs));
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