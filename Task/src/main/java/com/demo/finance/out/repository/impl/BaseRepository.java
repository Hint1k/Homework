package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.domain.utils.GeneratedKey;
import com.demo.finance.exception.DatabaseException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseRepository {

    protected static final Logger log = Logger.getLogger(BaseRepository.class.getName());
    private static final Map<Class<?>, Method> SETTER_METHOD_CACHE = new HashMap<>();

    protected <T> void persistEntity(T entity, String insertSql, PreparedStatementSetter setter) {
        Long generatedId = insertRecord(insertSql, setter);
        if (generatedId != null) {
            setGeneratedId(entity, generatedId);
        }
    }

    protected boolean updateRecord(String sql, PreparedStatementSetter setter) {
        return executeWithinTransaction(conn -> {
            //noinspection SqlSourceToSinkFlow
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setter.setValues(stmt);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        });
    }

    protected Long insertRecord(String sql, PreparedStatementSetter setter) {
        return executeWithinTransaction(conn -> {
            //noinspection SqlSourceToSinkFlow
            try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                setter.setValues(stmt);
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
            return null;
        });
    }

    protected <T> Optional<T> findRecordByCriteria(String sql, PreparedStatementSetter setter,
                                                   ResultSetMapper<T> mapper) {
        return queryDatabase(sql, setter, rs -> {
            if (rs.next()) {
                return Optional.of(mapper.map(rs));
            }
            return Optional.empty();
        });
    }

    protected <T> List<T> findAllRecordsByCriteria(String sql, List<Object> params, ResultSetMapper<T> mapper) {
        return queryDatabase(sql, stmt -> bindParameters(stmt, params), rs -> {
            List<T> results = new ArrayList<>();
            while (rs.next()) {
                results.add(mapper.map(rs));
            }
            return results;
        });
    }

    protected <T> T executeWithinTransaction(TransactionalOperation<T> operation) {
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                T result = operation.execute(conn);
                conn.commit();
                return result;
            } catch (SQLException e) {
                conn.rollback();
                logError("Transaction operation failed", e);
                return null;
            }
        } catch (SQLException e) {
            logError("Failed to obtain database connection", e);
            return null;
        }
    }

    protected <T> T queryDatabase(String sql, PreparedStatementSetter setter, ResultSetHandler<T> resultHandler) {
        return executeWithinTransaction(conn -> {
            //noinspection SqlSourceToSinkFlow
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setter.setValues(stmt);
                try (ResultSet rs = stmt.executeQuery()) {
                    return resultHandler.handle(rs);
                }
            }
        });
    }

    protected <T> void setGeneratedId(T entity, Long generatedId) {
        try {
            Class<?> entityClass = entity.getClass();
            Method setterMethod = SETTER_METHOD_CACHE.get(entityClass);

            if (setterMethod == null) {
                for (Field field : entityClass.getDeclaredFields()) {
                    if (field.isAnnotationPresent(GeneratedKey.class)) {
                        String fieldName = field.getName();
                        String setterName = "set" + fieldName.substring(0, 1).toUpperCase()
                                + fieldName.substring(1);
                        setterMethod = entityClass.getMethod(setterName, Long.class);
                        SETTER_METHOD_CACHE.put(entityClass, setterMethod);
                        break;
                    }
                }
            }
            Objects.requireNonNull(setterMethod).invoke(entity, generatedId);
        } catch (Exception e) {
            logError("Failed to set generated key on entity: " + entity.getClass().getSimpleName(), e);
        }
    }

    protected void bindParameters(PreparedStatement stmt, List<Object> params) {
        try {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
        } catch (SQLException e) {
            logError("Failed to bind parameters", e);
        }
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

    @FunctionalInterface
    protected interface ResultSetHandler<T> {
        T handle(ResultSet rs) throws SQLException;
    }
}