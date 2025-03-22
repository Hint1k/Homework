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

/**
 * Abstract base class providing common database operations and utility methods for repository implementations.
 * It includes transaction management, query execution, and error handling.
 */
public abstract class BaseRepository {

    protected static final Logger log = Logger.getLogger(BaseRepository.class.getName());
    private static final Map<Class<?>, Method> SETTER_METHOD_CACHE = new HashMap<>();

    /**
     * Persists an entity to the database by executing the provided SQL INSERT statement.
     * If the operation generates a key (e.g., auto-incremented ID), it assigns the key to the entity.
     *
     * @param entity    the entity to persist
     * @param insertSql the SQL INSERT statement to execute
     * @param setter    the parameter setter to populate the prepared statement
     * @param <T>       the type of the entity
     */
    protected <T> void persistEntity(T entity, String insertSql, PreparedStatementSetter setter) {
        Long generatedId = insertRecord(insertSql, setter);
        if (generatedId != null) {
            setGeneratedId(entity, generatedId);
        }
    }

    /**
     * Updates a record in the database by executing the provided SQL UPDATE statement.
     *
     * @param sql    the SQL UPDATE statement to execute
     * @param setter the parameter setter to populate the prepared statement
     * @return {@code true} if the update was successful, otherwise {@code false}
     */
    protected boolean updateRecord(String sql, PreparedStatementSetter setter) {
        try {
            executeStatement(sql, setter, null);
            return true;
        } catch (Exception e) {
            logError("Update record failed", e);
            return false;
        }
    }

    /**
     * Inserts a record into the database by executing the provided SQL INSERT statement.
     * If the operation generates a key (e.g., auto-incremented ID), it returns the generated key.
     *
     * @param sql    the SQL INSERT statement to execute
     * @param setter the parameter setter to populate the prepared statement
     * @return the generated key if successful, or {@code null} if no key was generated
     */
    protected Long insertRecord(String sql, PreparedStatementSetter setter) {
        return executeStatement(sql, setter, rs -> {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return null;
        });
    }

    /**
     * Finds a single record in the database based on the provided SQL query, parameter setter, and result mapper.
     *
     * @param sql    the SQL query to execute
     * @param setter the parameter setter to populate the prepared statement
     * @param mapper the result set mapper to convert the result into the desired type
     * @param <T>    the type of the result
     * @return an {@code Optional} containing the mapped result if found, or an empty {@code Optional} if not found
     */
    protected <T> Optional<T> findRecordByCriteria(String sql, PreparedStatementSetter setter,
                                                   ResultSetMapper<T> mapper) {
        return queryDatabase(sql, setter, rs -> {
            if (rs.next()) {
                return Optional.of(mapper.map(rs));
            }
            return Optional.empty();
        });
    }

    /**
     * Finds multiple records in the database based on the provided SQL query, parameters, and result mapper.
     * The method binds the parameters to the prepared statement and maps each row of the result
     * set to the desired type.
     *
     * @param sql    the SQL query to execute
     * @param params the list of parameters to bind to the prepared statement
     * @param mapper the result set mapper to convert each row into the desired type
     * @param <T>    the type of the results
     * @return a list of mapped results retrieved from the database
     */
    protected <T> List<T> findAllRecordsByCriteria(String sql, List<Object> params, ResultSetMapper<T> mapper) {
        return queryDatabase(sql, stmt -> bindParameters(stmt, params), rs -> {
            List<T> results = new ArrayList<>();
            while (rs.next()) {
                results.add(mapper.map(rs));
            }
            return results;
        });
    }

    /**
     * Executes a database operation within a transaction. Handles committing or rolling back the transaction
     * based on the success or failure of the operation.
     *
     * @param operation the database operation to execute within the transaction
     * @param <T>       the type of result returned by the operation
     * @return the result of the operation if successful, or {@code null} if an error occurs
     */
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

    /**
     * Executes a query against the database, binds parameters, and processes the ResultSet.
     *
     * @param sql           the SQL query to execute
     * @param setter        the parameter setter to populate the prepared statement
     * @param resultHandler the function to process the ResultSet and return the result
     * @param <T>           the type of the result
     * @return the result of processing the ResultSet
     */
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

    /**
     * Executes an SQL statement (e.g., INSERT, UPDATE) and optionally processes the generated keys.
     *
     * @param sql           the SQL statement to execute
     * @param setter        the parameter setter to populate the prepared statement
     * @param resultHandler the function to process the generated keys, or {@code null} if not needed
     * @param <T>           the type of the result
     * @return the result of processing the generated keys, or {@code null} if no result handler is provided
     */
    protected <T> T executeStatement(String sql, PreparedStatementSetter setter, ResultSetHandler<T> resultHandler) {
        return executeWithinTransaction(conn -> {
            //noinspection SqlSourceToSinkFlow
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setter.setValues(stmt);
                stmt.executeUpdate();
                if (resultHandler != null) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        return resultHandler.handle(rs);
                    }
                }
                return null;
            }
        });
    }

    /**
     * Assigns a generated ID to an entity by invoking the appropriate setter method.
     * The setter method is determined using reflection and cached for future use.
     *
     * @param entity      the entity to assign the generated ID to
     * @param generatedId the generated ID to assign
     * @param <T>         the type of the entity
     * @throws RuntimeException if the setter method cannot be found or invoked
     */
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

    /**
     * Binds a list of parameters to a PreparedStatement.
     *
     * @param stmt   the PreparedStatement to bind parameters to
     * @param params the list of parameters to bind
     * @throws RuntimeException if an error occurs while binding the parameters
     */
    protected void bindParameters(PreparedStatement stmt, List<Object> params) {
        try {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
        } catch (SQLException e) {
            logError("Failed to bind parameters", e);
        }
    }

    /**
     * Logs an error message and wraps the exception in a {@code DatabaseException}.
     *
     * @param message the error message to log
     * @param e       the exception that caused the error
     */
    protected void logError(String message, Exception e) {
        log.log(Level.SEVERE, message + ": " + e.getMessage(), e);
        throw new DatabaseException(message, e);
    }

    /**
     * Functional interface representing a database operation that can be executed within a transaction.
     *
     * @param <T> the type of result returned by the operation
     */
    @FunctionalInterface
    protected interface TransactionalOperation<T> {
        T execute(Connection conn) throws SQLException;
    }

    /**
     * Functional interface for setting parameters on a prepared statement.
     */
    @FunctionalInterface
    protected interface PreparedStatementSetter {
        void setValues(PreparedStatement stmt) throws SQLException;
    }

    /**
     * Functional interface for mapping a result set to a specific type.
     *
     * @param <T> the type of the result
     */
    @FunctionalInterface
    protected interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    /**
     * Functional interface for processing a ResultSet and returning a result.
     *
     * @param <T> the type of the result
     */
    @FunctionalInterface
    protected interface ResultSetHandler<T> {
        T handle(ResultSet rs) throws SQLException;
    }
}