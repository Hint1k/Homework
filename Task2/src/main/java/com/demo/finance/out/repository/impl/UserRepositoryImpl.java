package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.exception.DatabaseException;
import com.demo.finance.out.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

/**
 * The {@code UserRepositoryImpl} class provides an in-memory implementation of the {@code UserRepository} interface.
 * It manages a collection of {@code User} objects using a concurrent hash map.
 */
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (user_id, name, email, password_hash, role, is_blocked) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, user.getUserId());
                stmt.setString(2, user.getName());
                stmt.setString(3, user.getEmail());
                stmt.setString(4, user.getPassword());
                stmt.setString(5, user.getRole().getName());
                stmt.setBoolean(6, user.isBlocked());

                stmt.executeUpdate();
                conn.commit(); // Commit the transaction if successful
            } catch (SQLException e) {
                conn.rollback(); // Rollback if any exception occurs
                throw new DatabaseException("Error saving user", e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error handling transaction", e);
        }
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, password_hash = ?, role = ?, is_blocked = ? WHERE user_id = ?";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getPassword());
                stmt.setString(4, user.getRole().getName());
                stmt.setBoolean(5, user.isBlocked());
                stmt.setLong(6, user.getUserId());

                boolean result = stmt.executeUpdate() > 0;
                conn.commit(); // Commit the transaction if successful
                return result;
            } catch (SQLException e) {
                conn.rollback(); // Rollback if any exception occurs
                throw new DatabaseException("Error updating user", e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error handling transaction", e);
        }
    }

    @Override
    public boolean delete(Long userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, userId);

                boolean result = stmt.executeUpdate() > 0;
                conn.commit(); // Commit the transaction if successful
                return result;
            } catch (SQLException e) {
                conn.rollback(); // Rollback if any exception occurs
                throw new DatabaseException("Error deleting user", e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error handling transaction", e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
                conn.commit(); // Commit the transaction if successful
            } catch (SQLException e) {
                conn.rollback(); // Rollback if any exception occurs
                throw new DatabaseException("Error retrieving all users", e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error handling transaction", e);
        }
        return users;
    }

    @Override
    public Optional<User> findById(Long userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        conn.commit(); // Commit the transaction if successful
                        return Optional.of(mapResultSetToUser(rs));
                    }
                }
            } catch (SQLException e) {
                conn.rollback(); // Rollback if any exception occurs
                throw new DatabaseException("Error finding user by ID", e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error handling transaction", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        conn.commit(); // Commit the transaction if successful
                        return Optional.of(mapResultSetToUser(rs));
                    }
                }
            } catch (SQLException e) {
                conn.rollback(); // Rollback if any exception occurs
                throw new DatabaseException("Error finding user by email", e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error handling transaction", e);
        }
        return Optional.empty();
    }

    @Override
    public Long generateNextId() {
        String sql = "SELECT COALESCE(MAX(user_id), 0) + 1 FROM users";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                if (rs.next()) {
                    conn.commit(); // Commit the transaction if successful
                    return rs.getLong(1);
                }
            } catch (SQLException e) {
                conn.rollback(); // Rollback if any exception occurs
                throw new DatabaseException("Error generating next user ID", e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error handling transaction", e);
        }
        return 1L;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getBoolean("is_blocked"),
                new Role(rs.getString("role"))
        );
    }
}