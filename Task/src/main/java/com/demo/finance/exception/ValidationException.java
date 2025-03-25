package com.demo.finance.exception;

/**
 * The {@code ValidationException} class extends {@link RuntimeException} and represents an exception
 * that occurs when validation of input data fails. It is used to signal invalid or unexpected data
 * during application processing.
 */
public class ValidationException extends RuntimeException {

    /**
     * Constructs a new {@code ValidationException} with the specified detail message.
     *
     * @param message the detail message describing the validation failure
     */
    public ValidationException(String message) {
        super(message);
    }
}