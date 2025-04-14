package com.demo.finance.exception.custom;

/**
 * Custom exception class representing an optimistic locking conflict.
 * This exception is typically thrown when an attempt to update or delete
 * a record in the database fails due to a version mismatch, indicating
 * that the record has been modified by another transaction since it was read.
 * Optimistic locking is a concurrency control mechanism used to ensure data
 * integrity without locking the database rows. Instead, it relies on a version
 * field (e.g., a timestamp or incrementing number) to detect concurrent modifications.
 *
 * @see java.lang.RuntimeException
 */
public class OptimisticLockException extends RuntimeException {

    /**
     * Constructs a new {@code OptimisticLockException} with the specified detail message.
     *
     * @param message the detail message describing the cause of the exception.
     *                This message is retrieved later using the {@link #getMessage()} method.
     */
    public OptimisticLockException(String message) {
        super(message);
    }
}