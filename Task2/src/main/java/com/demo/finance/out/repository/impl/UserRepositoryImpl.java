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

    @Override
    public void save(User user) {
        String sql = "INSERT INTO finance.users (name, email, password, blocked, role) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getPassword());
                stmt.setBoolean(4, user.isBlocked());
                stmt.setString(5, user.getRole().getName());

                stmt.executeUpdate();

                // Retrieve and set the generated ID
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserId(rs.getLong(1));
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                log.log(Level.SEVERE, "User save failed", e);
                throw new DatabaseException("Error saving user", e);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "User save failed", e);
            throw new DatabaseException("Error handling transaction", e);
        }
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE finance.users SET name = ?, email = ?, password = ?, blocked = ?, role = ? "
                + "WHERE id = ?";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getPassword());
                stmt.setBoolean(4, user.isBlocked());
                stmt.setString(5, user.getRole().getName());
                stmt.setLong(6, user.getUserId());

                boolean result = stmt.executeUpdate() > 0;
                conn.commit();
                return result;
            } catch (SQLException e) {
                conn.rollback();
                log.log(Level.SEVERE, "User update failed", e);
                throw new DatabaseException("Error updating user", e);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "User update failed", e);
            throw new DatabaseException("Error handling transaction", e);
        }
    }

    @Override
    public boolean delete(Long userId) {
        String sql = "DELETE FROM finance.users WHERE id = ?";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, userId);

                boolean result = stmt.executeUpdate() > 0;
                conn.commit();
                return result;
            } catch (SQLException e) {
                conn.rollback();
                log.log(Level.SEVERE, "User delete failed", e);
                throw new DatabaseException("Error deleting user", e);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "User delete failed", e);
            throw new DatabaseException("Error handling transaction", e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM finance.users";
        List<User> users = new ArrayList<>();
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                log.log(Level.SEVERE, "User findAll failed", e);
                throw new DatabaseException("Error retrieving all users", e);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "User findAll failed", e);
            throw new DatabaseException("Error handling transaction", e);
        }
        return users;
    }

    @Override
    public Optional<User> findById(Long userId) {
        String sql = "SELECT * FROM finance.users WHERE id = ?";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        conn.commit();
                        return Optional.of(mapResultSetToUser(rs));
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                log.log(Level.SEVERE, "User findById failed", e);
                throw new DatabaseException("Error finding user by ID", e);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "User findById failed", e);
            throw new DatabaseException("Error handling transaction", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM finance.users WHERE email = ?";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        conn.commit();
                        return Optional.of(mapResultSetToUser(rs));
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                log.log(Level.SEVERE, "User findByEmail failed", e);
                throw new DatabaseException("Error finding user by email", e);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "User findByEmail failed", e);
            throw new DatabaseException("Error handling transaction", e);
        }
        return Optional.empty();
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
//                rs.getLong("user_id"), // TODO change later to user_id instead
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getBoolean("blocked"),
                new Role(rs.getString("role"))
        );
    }
}