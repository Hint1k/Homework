package com.demo.finance.domain.utils;

/**
 * The {@code FlagUtils} interface provides an abstraction for managing request-scoped flags
 * used during application logic execution.
 * <p>
 * It is primarily used to control whether or not certain behaviors should be executed,
 * such as deciding whether a JWT token should be validated against the database.
 * </p>
 * <p>
 * This interface is typically implemented with request-scoped components to ensure
 * thread safety and avoid shared state across different user requests.
 * </p>
 */
public interface FlagUtils {

    /**
     * Determines whether the system should validate a JWT token against the database.
     *
     * @return {@code true} if database validation should occur; {@code false} otherwise
     */
    boolean shouldValidateWithDatabase();

    /**
     * Sets the flag indicating whether the system should validate a JWT token against the database.
     *
     * @param validateWithDatabase {@code true} to enable database validation; {@code false} to skip it
     */
    void setValidateWithDatabase(boolean validateWithDatabase);
}
