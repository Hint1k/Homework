package com.demo.finance.out.repository.impl;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.UserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl extends BaseRepository implements UserRepository {

    private static final String INSERT_SQL = "INSERT INTO finance.users "
            + "(name, email, password, blocked, role, version) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE finance.users SET name = ?, email = ?, password = ?, "
            + "blocked = ?, role = ?, version = ? WHERE user_id = ?";
    private static final String DELETE_SQL = "DELETE FROM finance.users WHERE user_id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM finance.users";
    private static final String FIND_ALL_SQL_PAGINATED = "SELECT * FROM finance.users LIMIT ? OFFSET ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM finance.users WHERE user_id = ?";
    private static final String FIND_BY_EMAIL_SQL = "SELECT * FROM finance.users WHERE email = ?";
    private static final String COUNT_SQL = "SELECT COUNT(*) AS total FROM finance.users";

    @Override
    public void save(User user) {
        super.persistEntity(user, INSERT_SQL, stmt -> setUserParameters(stmt, user));
    }

    @Override
    public boolean update(User user) {
        return updateRecord(UPDATE_SQL, stmt -> {
            setUserParameters(stmt, user);
            stmt.setLong(7, user.getUserId());
        });
    }

    @Override
    public boolean delete(Long userId) {
        return updateRecord(DELETE_SQL, stmt -> stmt.setLong(1, userId));
    }

    @Override
    public List<User> findAll(int offset, int size) {
        List<Object> params = new ArrayList<>();
        params.add(size);
        params.add(offset);
        return findAllRecordsByCriteria(FIND_ALL_SQL_PAGINATED, params, this::mapResultSetToUser);
    }

    @Override
    public int getTotalUserCount() {
        return queryDatabase(COUNT_SQL, stmt -> {}, rs -> {
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        });
    }

    @Override
    public User findById(Long userId) {
        return findRecordByCriteria(FIND_BY_ID_SQL, stmt -> stmt.setLong(1, userId),
                this::mapResultSetToUser).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return findRecordByCriteria(FIND_BY_EMAIL_SQL, stmt -> stmt.setString(1, email),
                this::mapResultSetToUser).orElse(null);
    }

    private void setUserParameters(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getName());
        stmt.setString(2, user.getEmail());
        stmt.setString(3, user.getPassword());
        stmt.setBoolean(4, user.isBlocked());
        stmt.setString(5, user.getRole().getName());
        stmt.setLong(6, user.getVersion());
    }

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