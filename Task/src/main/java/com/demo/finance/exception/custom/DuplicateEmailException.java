package com.demo.finance.exception.custom;

/**
 * Exception indicating that a user registration attempt failed because the email address
 * is already associated with an existing account.
 *
 * <p>This exception is typically thrown during user registration when the system detects
 * that the provided email address already exists in the database.</p>
 */
public class DuplicateEmailException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message (which should typically include the duplicate email)
     */
    public DuplicateEmailException(String message) {
        super(message);
    }
}