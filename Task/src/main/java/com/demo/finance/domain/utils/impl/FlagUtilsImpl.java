package com.demo.finance.domain.utils.impl;

import com.demo.finance.domain.utils.FlagUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * Implementation of the {@link FlagUtils} interface with request scope.
 * <p>
 * This class provides a thread-safe way to control request-specific flags,
 * particularly whether a JWT token should be validated against the database.
 * </p>
 * <p>
 * The request scope ensures that each HTTP request gets its own instance of this class,
 * avoiding cross-request interference and maintaining isolation.
 * </p>
 */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class FlagUtilsImpl implements FlagUtils {

    /**
     * Indicates whether JWT token validation should include checking the user in the database.
     * Defaults to {@code false}.
     */
    private boolean validateWithDatabase = false;

    /**
     * Determines whether the system should validate a JWT token against the database.
     *
     * @return {@code true} if database validation should occur; {@code false} otherwise
     */
    @Override
    public boolean shouldValidateWithDatabase() {
        return validateWithDatabase;
    }

    /**
     * Sets the flag indicating whether the system should validate a JWT token against the database.
     *
     * @param validateWithDatabase {@code true} to enable database validation; {@code false} to skip it
     */
    @Override
    public void setValidateWithDatabase(boolean validateWithDatabase) {
        this.validateWithDatabase = validateWithDatabase;
    }
}
