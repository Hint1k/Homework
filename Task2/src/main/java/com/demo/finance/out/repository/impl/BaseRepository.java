package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseRepository {

    protected static final Logger log = Logger.getLogger(BaseRepository.class.getName());

    protected <T> T executeInTransaction(TransactionalOperation<T> operation) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                T result = operation.execute(conn);
                conn.commit();
                return result;
            } catch (SQLException e) {
                conn.rollback();
                logError("Transaction failed", e);
                return null;
            }
        } catch (SQLException e) {
            logError("Failed to obtain database connection", e);
            throw new DatabaseException("Error obtaining database connection", e);
        }
    }

    protected boolean executeUpdate(String sql, PreparedStatementSetter setter) {
        return Boolean.TRUE.equals(executeInTransaction(conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setter.setValues(stmt);
                return stmt.executeUpdate() > 0;
            }
        }));
    }

    protected <T> Optional<T> findByCriteria(String sql, PreparedStatementSetter setter, ResultSetMapper<T> mapper) {
        return executeInTransaction(conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setter.setValues(stmt);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapper.map(rs));
                    }
                }
            }
            return Optional.empty();
        });
    }

    protected Connection getConnection() {
        return DataSourceManager.getConnection();
    }

    protected void logError(String message, Exception e) {
        log.log(Level.SEVERE, message + ": " + e.getMessage(), e);
        throw new DatabaseException(message, e);
    }

    @FunctionalInterface
    protected interface TransactionalOperation<T> {
        T execute(Connection conn) throws SQLException;
    }

    @FunctionalInterface
    protected interface PreparedStatementSetter {
        void setValues(PreparedStatement stmt) throws SQLException;
    }

    @FunctionalInterface
    protected interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}