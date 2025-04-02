package com.demo.finance.exception;

/**
 * Exception thrown when a requested user entity is not found in the system.
 * <p>
 * This typically results in an HTTP 404 (Not Found) response when handled by Spring controllers.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message (should include the missing user identifier)
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}