package com.demo.finance.exception;

/**
 * Custom exception thrown when the maximum number of retries has been reached.
 * This can be used in scenarios where an action (e.g., authentication) exceeds
 * the allowed number of retry attempts.
 */
public class MaxRetriesReachedException extends RuntimeException {

    /**
     * Constructs a new {@code MaxRetriesReachedException} with the specified detail message.
     *
     * @param message The detail message to be provided with the exception.
     */
    public MaxRetriesReachedException(String message) {
        super(message);
    }
}