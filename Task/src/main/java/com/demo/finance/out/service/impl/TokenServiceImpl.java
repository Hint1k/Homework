package com.demo.finance.out.service.impl;

import com.demo.finance.out.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * Service implementation responsible for managing JWT tokens in cache.
 * Supports storing, validating, and invalidating tokens, ensuring each user can have only one active token at a time.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private static final ThreadLocal<String> currentToken = new ThreadLocal<>();
    private final CacheManager cacheManager;
    private static final String TOKENS_CACHE = "tokens";
    private static final String INVALID_TOKENS_CACHE = "invalidTokens";

    /**
     * Stores a token for the given user. If a previous token exists, it is blacklisted before the new one is stored.
     *
     * @param userId the ID of the user
     * @param token  the new token to store
     */
    @Override
    public void storeTokenForUser(Long userId, String token) {
        Cache tokensCache = cacheManager.getCache(TOKENS_CACHE);
        Cache invalidTokensCache = cacheManager.getCache(INVALID_TOKENS_CACHE);
        if (tokensCache != null && invalidTokensCache != null && userId != null && token != null) {
            String oldToken = tokensCache.get(userId, String.class);
            if (oldToken != null) {
                invalidTokensCache.put(oldToken, true);
            }
            tokensCache.put(userId, token);
        }
    }

    /**
     * Validates the provided token by checking if it has been blacklisted.
     *
     * @param token the token to validate
     * @return true if token is not blacklisted and tokens cache is available, false otherwise
     */
    @Override
    public boolean isTokenValid(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        boolean blacklisted = isTokenBlacklisted(token);
        if (blacklisted) {
            return false;
        }
        Cache tokensCache = cacheManager.getCache(TOKENS_CACHE);
        return tokensCache != null;
    }

    /**
     * Sets the current token for the executing thread.
     *
     * @param token the token to set
     */
    @Override
    public void setCurrentToken(String token) {
        currentToken.set(token);
    }

    /**
     * Clears the current token for the executing thread.
     */
    @Override
    public void clearCurrentToken() {
        currentToken.remove();
    }

    /**
     * Invalidates the current token associated with the executing thread and the given user ID.
     *
     * @param userId the ID of the user whose current token should be invalidated
     */
    @Override
    public void invalidateCurrentToken(Long userId) {
        String token = getCurrentToken();
        if (token != null) {
            invalidateToken(token, userId);
        }
    }

    /**
     * Invalidates the token associated with the given user ID, if present in the cache.
     *
     * @param userId the ID of the user whose token should be invalidated
     */
    @Override
    public void invalidateUserToken(Long userId) {
        if (userId == null) {
            return;
        }
        Cache tokensCache = cacheManager.getCache(TOKENS_CACHE);
        Cache invalidTokensCache = cacheManager.getCache(INVALID_TOKENS_CACHE);
        if (tokensCache == null || invalidTokensCache == null) {
            return;
        }
        String userToken = tokensCache.get(userId, String.class);
        if (userToken != null) {
            invalidTokensCache.put(userToken, true);
            tokensCache.evict(userId);
        }
    }

    /**
     * Checks if the given token is blacklisted.
     *
     * @param token the token to check
     * @return true if token is blacklisted, false otherwise
     */
    private boolean isTokenBlacklisted(String token) {
        try {
            Cache invalidCache = cacheManager.getCache(INVALID_TOKENS_CACHE);
            return invalidCache != null && invalidCache.get(token) != null;
        } catch (Exception e) {
            log.error("Error checking token blacklist", e);
            return true;
        }
    }

    /**
     * Retrieves the current token for the executing thread.
     *
     * @return the current token, or null if none is set
     */
    private String getCurrentToken() {
        return currentToken.get();
    }

    /**
     * Invalidates the specified token and removes the token entry for the given user ID from the tokens cache.
     *
     * @param token  the token to blacklist
     * @param userId the ID of the user whose token should be removed from the cache
     */
    private void invalidateToken(String token, Long userId) {
        Cache invalidCache = cacheManager.getCache(INVALID_TOKENS_CACHE);
        if (invalidCache != null) {
            invalidCache.put(token, true);
        }
        Cache tokensCache = cacheManager.getCache(TOKENS_CACHE);
        if (tokensCache != null) {
            tokensCache.evict(userId);
        }
    }
}