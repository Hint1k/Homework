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

public class UserRepositoryImpl extends BaseRepository implements UserRepository {

    private static final String INSERT_SQL = "INSERT INTO finance.users (name, email, password, blocked, role) "
            + "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE finance.users SET name = ?, email = ?, password = ?, "
            + "blocked = ?, role = ? WHERE user_id = ?";
    private static final String DELETE_SQL = "DELETE FROM finance.users WHERE user_id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM finance.users";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM finance.users WHERE user_id = ?";
    private static final String FIND_BY_EMAIL_SQL = "SELECT * FROM finance.users WHERE email = ?";

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

    @Override
    public boolean update(User user) {
        return executeUpdate(UPDATE_SQL, stmt -> {
            setUserParameters(stmt, user);
            stmt.setLong(6, user.getUserId());
        });
    }

    @Override
    public boolean delete(Long userId) {
        return executeUpdate(DELETE_SQL, stmt -> stmt.setLong(1, userId));
    }

    @Override
    public List<User> findAll() {
        return executeInTransaction(conn -> {
            List<User> users = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(FIND_ALL_SQL);
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
        return findByCriteria(FIND_BY_ID_SQL, stmt -> stmt.setLong(1, userId),
                this::mapResultSetToUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findByCriteria(FIND_BY_EMAIL_SQL, stmt -> stmt.setString(1, email),
                this::mapResultSetToUser);
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
}