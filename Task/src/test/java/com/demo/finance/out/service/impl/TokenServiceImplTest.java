package com.demo.finance.out.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {

    @Mock
    private CacheManager cacheManager;
    @Mock
    private Cache tokensCache;
    @Mock
    private Cache invalidTokensCache;
    @InjectMocks
    private TokenServiceImpl tokenService;

    private static final Long USER_ID = 123L;
    private static final String VALID_TOKEN = "valid.token.here";
    private static final String INVALID_TOKEN = "invalid.token.here";

    @BeforeEach
    void setUp() {
        lenient().when(cacheManager.getCache("tokens")).thenReturn(tokensCache);
        lenient().when(cacheManager.getCache("invalidTokens")).thenReturn(invalidTokensCache);
    }

    @Test
    @DisplayName("Should store a new token and blacklist the old one")
    void storeTokenForUser_ShouldStoreNewTokenAndBlacklistOldOne() {
        when(tokensCache.get(USER_ID, String.class)).thenReturn(VALID_TOKEN);

        tokenService.storeTokenForUser(USER_ID, INVALID_TOKEN);

        verify(invalidTokensCache, times(1)).put(VALID_TOKEN, true);
        verify(tokensCache, times(1)).put(USER_ID, INVALID_TOKEN);
    }

    @Test
    @DisplayName("Should return false for blacklisted token")
    void isTokenValid_ShouldReturnFalseForBlacklistedToken() {
        Cache.ValueWrapper valueWrapper = mock(Cache.ValueWrapper.class);
        when(invalidTokensCache.get(INVALID_TOKEN)).thenReturn(valueWrapper);

        boolean isValid = tokenService.isTokenValid(INVALID_TOKEN);

        assert !isValid;
        verify(invalidTokensCache, times(1)).get(INVALID_TOKEN);
    }

    @Test
    @DisplayName("Should return true for valid token")
    void isTokenValid_ShouldReturnTrueForValidToken() {
        when(invalidTokensCache.get(VALID_TOKEN)).thenReturn(null);

        boolean isValid = tokenService.isTokenValid(VALID_TOKEN);

        assert isValid;
        verify(invalidTokensCache, times(1)).get(VALID_TOKEN);
    }

    @Test
    @DisplayName("Should invalidate the token for the user")
    void invalidateUserToken_ShouldInvalidateUserToken() {
        when(tokensCache.get(USER_ID, String.class)).thenReturn(VALID_TOKEN);

        tokenService.invalidateUserToken(USER_ID);

        verify(invalidTokensCache, times(1)).put(VALID_TOKEN, true);
        verify(tokensCache, times(1)).evict(USER_ID);
    }

    @Test
    @DisplayName("Should handle null userId when storing token")
    void storeTokenForUser_ShouldHandleNullUserId() {
        tokenService.storeTokenForUser(null, VALID_TOKEN);
        verify(tokensCache, never()).put(any(), any());
        verify(invalidTokensCache, never()).put(any(), any());
    }

    @Test
    @DisplayName("Should handle null token when storing token")
    void storeTokenForUser_ShouldHandleNullToken() {
        tokenService.storeTokenForUser(USER_ID, null);
        verify(tokensCache, never()).put(USER_ID, null);
        verify(invalidTokensCache, never()).put(any(), any());
    }

    @Test
    @DisplayName("Should return false when validating null token")
    void isTokenValid_ShouldReturnFalseForNullToken() {
        boolean isValid = tokenService.isTokenValid(null);
        assert !isValid;
        verify(invalidTokensCache, never()).get(any());
    }

    @Test
    @DisplayName("Should return false when validating empty token")
    void isTokenValid_ShouldReturnFalseForEmptyToken() {
        boolean isValid = tokenService.isTokenValid("");
        assert !isValid;
        verify(invalidTokensCache, never()).get(any());
    }
}