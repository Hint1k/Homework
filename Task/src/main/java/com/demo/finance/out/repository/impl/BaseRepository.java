package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.domain.utils.GeneratedKey;
import com.demo.finance.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;

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
 * The {@code BaseRepository} class serves as an abstract base class for all repository implementations.
 * It provides common database operations such as saving, updating, querying, and handling transactions,
 * leveraging JDBC to interact with the database. This class also includes utility methods for parameter binding,
 * result set mapping, and exception handling.
 */
public abstract class BaseRepository {

    /**
     * Logger instance for logging events and errors in the {@code BaseRepository} class.
     */
    protected static final Logger log = Logger.getLogger(BaseRepository.class.getName());

    /**
     * Cache for storing setter methods of entities annotated with {@link GeneratedKey}.
     * Used to optimize setting generated keys on entities.
     */
    private static final Map<Class<?>, Method> SETTER_METHOD_CACHE = new HashMap<>();

    protected final DataSourceManager dataSourceManager;

    @Autowired
    protected BaseRepository(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    /**
     * Persists a new entity to the database by executing the provided SQL insert query.
     *
     * @param entity      the entity object to be persisted
     * @param insertSql   the SQL insert query to execute
     * @param setter      the callback interface to set parameters on the prepared statement
     */
    protected <T> void persistEntity(T entity, String insertSql, PreparedStatementSetter setter) {
        Long generatedId = insertRecord(insertSql, setter);
        if (generatedId != null) {
            setGeneratedId(entity, generatedId);
        }
    }

    /**
     * Updates a record in the database by executing the provided SQL update query.
     *
     * @param sql    the SQL update query to execute
     * @param setter the callback interface to set parameters on the prepared statement
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
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

    /**
     * Inserts a new record into the database by executing the provided SQL insert query.
     *
     * @param sql    the SQL insert query to execute
     * @param setter the callback interface to set parameters on the prepared statement
     * @return the generated key ({@code Long}) of the newly inserted record, or {@code null} if no key is generated
     */
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

    /**
     * Finds a single record in the database based on the provided SQL query and criteria.
     *
     * @param sql    the SQL query to execute
     * @param setter the callback interface to set parameters on the prepared statement
     * @param mapper the callback interface to map the result set to an entity
     * @param <T>    the type of the entity to retrieve
     * @return an {@link Optional} containing the mapped entity, or {@code Optional.empty()} if no record is found
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
     * Finds all records matching the provided SQL query and criteria.
     *
     * @param sql     the SQL query to execute
     * @param params  the list of parameters to bind to the query
     * @param mapper  the callback interface to map the result set to an entity
     * @param <T>     the type of the entities to retrieve
     * @return a {@link List} of mapped entities
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
     * Executes a database operation within a transactional context.
     *
     * @param operation the transactional operation to execute
     * @param <T>       the type of the result returned by the operation
     * @return the result of the transactional operation, or {@code null} if an error occurs
     */
    protected <T> T executeWithinTransaction(TransactionalOperation<T> operation) {
        try (Connection conn = dataSourceManager.getConnection()) {
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
     * Executes a query on the database and processes the result set using the provided handler.
     *
     * @param sql          the SQL query to execute
     * @param setter       the callback interface to set parameters on the prepared statement
     * @param resultHandler the callback interface to process the result set
     * @param <T>          the type of the result returned by the handler
     * @return the result of processing the result set
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
     * Sets the generated key on an entity by invoking the appropriate setter method.
     *
     * @param entity      the entity object to set the generated key on
     * @param generatedId the generated key value to set
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
     * Binds a list of parameters to a prepared statement.
     *
     * @param stmt   the prepared statement to bind parameters to
     * @param params the list of parameters to bind
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
     * Logs an error message and throws a {@link DatabaseException}.
     *
     * @param message the error message to log
     * @param e       the exception that caused the error
     */
    protected void logError(String message, Exception e) {
        log.log(Level.SEVERE, message + ": " + e.getMessage(), e);
        throw new DatabaseException(message, e);
    }

    /**
     * Functional interface representing a transactional database operation.
     *
     * @param <T> the type of the result returned by the operation
     */
    @FunctionalInterface
    protected interface TransactionalOperation<T> {
        /**
         * Executes the transactional operation using the provided database connection.
         *
         * @param conn the database connection to use
         * @return the result of the operation
         * @throws SQLException if a database access error occurs
         */
        T execute(Connection conn) throws SQLException;
    }

    /**
     * Functional interface for setting parameters on a prepared statement.
     */
    @FunctionalInterface
    protected interface PreparedStatementSetter {
        /**
         * Sets values on the provided prepared statement.
         *
         * @param stmt the prepared statement to set values on
         * @throws SQLException if a database access error occurs
         */
        void setValues(PreparedStatement stmt) throws SQLException;
    }

    /**
     * Functional interface for mapping a result set row to an entity.
     *
     * @param <T> the type of the entity to map
     */
    @FunctionalInterface
    protected interface ResultSetMapper<T> {
        /**
         * Maps a result set row to an entity.
         *
         * @param rs the result set containing the data
         * @return the mapped entity
         * @throws SQLException if a database access error occurs
         */
        T map(ResultSet rs) throws SQLException;
    }

    /**
     * Functional interface for handling a result set and returning a result.
     *
     * @param <T> the type of the result to return
     */
    @FunctionalInterface
    protected interface ResultSetHandler<T> {
        /**
         * Handles the provided result set and returns a result.
         *
         * @param rs the result set to process
         * @return the result of processing the result set
         * @throws SQLException if a database access error occurs
         */
        T handle(ResultSet rs) throws SQLException;
    }
}