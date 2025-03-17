package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract base class providing common database operations and utility methods for repository implementations.
 * It includes transaction management, query execution, and error handling.
 */
public abstract class BaseRepository {

    protected static final Logger log = Logger.getLogger(BaseRepository.class.getName());

    /**
     * Executes a database operation within a transaction. Handles committing or rolling back the transaction
     * based on the success or failure of the operation.
     *
     * @param operation the database operation to execute within the transaction
     * @param <T>       the type of result returned by the operation
     * @return the result of the operation if successful, or {@code null} if an error occurs
     */
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

    /**
     * Executes an update SQL statement using the provided SQL query and parameter setter.
     *
     * @param sql    the SQL update query to execute
     * @param setter the parameter setter to populate the prepared statement
     * @return {@code true} if the update was successful, otherwise {@code false}
     */
    protected boolean executeUpdate(String sql, PreparedStatementSetter setter) {
        return Boolean.TRUE.equals(executeInTransaction(conn -> {
            //noinspection SqlSourceToSinkFlow
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setter.setValues(stmt);
                return stmt.executeUpdate() > 0;
            }
        }));
    }

    /**
     * Finds a single entity in the database based on the provided SQL query, parameter setter, and result mapper.
     *
     * @param sql    the SQL query to execute
     * @param setter the parameter setter to populate the prepared statement
     * @param mapper the result set mapper to convert the result into the desired type
     * @param <T>    the type of the result
     * @return an {@code Optional} containing the mapped result if found, or an empty {@code Optional} if not found
     */
    protected <T> Optional<T> findOneByCriteria(String sql, PreparedStatementSetter setter,
                                                ResultSetMapper<T> mapper) {
        return executeInTransaction(conn -> {
            //noinspection SqlSourceToSinkFlow
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

    /**
     * Executes a query to retrieve multiple entities from the database based on the provided SQL query,
     * parameters, and result mapper. The method binds the parameters to the prepared statement and maps
     * each row of the result set to the desired type using the provided mapper.
     *
     * @param sql    the SQL query to execute
     * @param params the list of parameters to bind to the prepared statement
     * @param mapper the result set mapper to convert each row into the desired type
     * @param <T>    the type of the results
     * @return a list of mapped results retrieved from the database
     */
    protected <T> List<T> findAllByCriteria(String sql, List<Object> params, ResultSetMapper<T> mapper) {
        return executeQuery(sql, params, rs -> {
            List<T> results = new ArrayList<>();
            while (rs.next()) {
                results.add(mapper.map(rs));
            }
            return results;
        });
    }

    /**
     * Helper method to execute a query, bind parameters, and process the ResultSet.
     *
     * @param sql    the SQL query to execute
     * @param params the list of parameters to bind to the prepared statement
     * @param mapper the function to process the ResultSet and return the result
     * @param <T>    the type of the result
     * @return the result of processing the ResultSet
     */
    private <T> T executeQuery(String sql, List<Object> params, ResultSetMapper<T> mapper) {
        return executeInTransaction(conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    return mapper.map(rs);
                }
            }
        });
    }

    /**
     * Retrieves a database connection from the {@code DataSourceManager}.
     *
     * @return a {@code Connection} object for database operations
     */
    protected Connection getConnection() {
        return DataSourceManager.getConnection();
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
}