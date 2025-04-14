package com.demo.finance.out.service;

/**
 * Service interface for managing JWT tokens, including storing, validating, and invalidating tokens.
 */
public interface TokenService {

    /**
     * Stores a token for the specified user. If an existing token is present, it may be invalidated
     * depending on the implementation.
     *
     * @param userId the ID of the user
     * @param token  the token to store
     */
    void storeTokenForUser(Long userId, String token);

    /**
     * Validates the provided token.
     *
     * @param token the token to validate
     * @return true if the token is valid and not blacklisted, false otherwise
     */
    boolean isTokenValid(String token);

    /**
     * Sets the current token in the thread-local context.
     * This is typically used for tracking the token associated with the current request.
     *
     * @param token the token to set
     */
    void setCurrentToken(String token);

    /**
     * Clears the current token from the thread-local context.
     */
    void clearCurrentToken();

    /**
     * Invalidates the token associated with the given user ID, if one exists.
     *
     * @param userId the ID of the user whose token should be invalidated
     */
    void invalidateUserToken(Long userId);

    /**
     * Invalidates the current token (from thread-local context) associated with the specified user ID.
     *
     * @param userId the ID of the user
     */
    void invalidateCurrentToken(Long userId);
}
