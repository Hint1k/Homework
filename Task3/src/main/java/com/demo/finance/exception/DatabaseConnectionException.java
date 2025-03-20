package com.demo.finance.exception;

/**
 * Custom exception class for database connection-related errors.
 * This exception is thrown when there is an issue establishing or maintaining a connection to the database.
 */
public class DatabaseConnectionException extends RuntimeException {

    /**
     * Constructs a new {@code DatabaseConnectionException} with the specified detail message and cause.
     *
     * @param message the detail message describing the exception
     * @param cause   the underlying cause of the exception (can be null)
     */
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}