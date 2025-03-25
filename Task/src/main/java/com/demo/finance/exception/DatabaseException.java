package com.demo.finance.exception;

/**
 * Custom exception class for general database-related errors.
 * This exception is thrown when there is an issue performing database operations, such as queries or updates.
 */
public class DatabaseException extends RuntimeException {

    /**
     * Constructs a new {@code DatabaseException} with the specified detail message and cause.
     *
     * @param message the detail message describing the exception
     * @param cause   the underlying cause of the exception (can be null)
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}