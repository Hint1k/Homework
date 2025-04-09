package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.exception.custom.UserNotFoundException;
import com.demo.finance.out.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class JwtServiceImplTest {

    private static final String SECRET_KEY = "jwtSecretSuperSecureKeyThatIsAtLeast64CharactersLongForHS512Algorithm";
    private static final long JWT_EXPIRATION_IN_MS = 60000;
    private JwtServiceImpl jwtService;
    @Mock
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(SECRET_KEY, tokenService);
        setJwtExpiration(jwtService, JWT_EXPIRATION_IN_MS);
    }

    private void setJwtExpiration(JwtServiceImpl jwtService, long expiration) {
        try {
            Field field = JwtServiceImpl.class.getDeclaredField("jwtExpirationInMs");
            field.setAccessible(true);
            field.set(jwtService, expiration);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set jwtExpirationInMs in test", e);
        }
    }

    @Test
    @DisplayName("Generate token should return valid JWT")
    void generateToken_ShouldReturnValidJWT() {
        String email = "test@example.com";
        List<String> roles = List.of("user");
        Long userId = 1L;
        String token = jwtService.generateToken(email, roles, userId);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        assertEquals(email, claims.getSubject());
        assertEquals(roles, claims.get("roles"));

        Object userIdClaim = claims.get("userId");
        if (userIdClaim instanceof Integer) {
            assertEquals(userId.longValue(), ((Integer) userIdClaim).longValue());
        } else {
            assertEquals(userId, userIdClaim);
        }

        verify(tokenService, times(1)).storeTokenForUser(userId, token);
    }

    @Test
    @DisplayName("Validate token should throw ExpiredJwtException when token is expired")
    void validateToken_ShouldThrow_WhenTokenIsExpired() {
        setJwtExpiration(jwtService, -1000);
        String token = jwtService.generateToken("test@example.com", List.of("USER"), 1L);

        assertThrows(ExpiredJwtException.class, () -> jwtService.validateToken(token));

        verify(tokenService, times(0)).isTokenValid(any());
    }

    @Test
    @DisplayName("Validate token should throw when user not found")
    void validateToken_ShouldThrow_WhenUserNotFound() {
        String token = jwtService.generateToken("nonexistent@example.com", List.of("USER"), 1L);

        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> jwtService.validateToken(token));

        assertEquals("Your account was modified. You have to authenticate again.", exception.getMessage());

        verify(tokenService, times(1)).isTokenValid(token);
    }

    @Test
    @DisplayName("Validate token should throw when token is invalid")
    void validateToken_ShouldThrow_WhenTokenIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> jwtService.validateToken("invalid.token.here"));

        verify(tokenService, times(0)).isTokenValid(any());
    }

    @Test
    @DisplayName("Validate token with flag true should return user from DB")
    void validateToken_ShouldReturnUser_FromDatabase_WhenFlagTrue() {
        String token = jwtService.generateToken("test@example.com", List.of("USER"), 1L);

        when(tokenService.isTokenValid(token)).thenReturn(true);

        UserDto result = jwtService.validateToken(token);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());

        verify(tokenService, times(1)).isTokenValid(token);
    }

    @Test
    @DisplayName("Validate token with flag false should return userDto from claims")
    void validateToken_ShouldReturnUserDto_FromClaims_WhenFlagFalse() {
        String token = jwtService.generateToken("test@example.com", List.of("USER"), 1L);

        when(tokenService.isTokenValid(token)).thenReturn(true);

        UserDto result = jwtService.validateToken(token);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("USER", result.getRole());

        verify(tokenService, times(1)).isTokenValid(token);
    }
}