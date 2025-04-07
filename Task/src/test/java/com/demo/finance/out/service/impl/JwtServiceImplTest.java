package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.model.User;
import com.demo.finance.exception.UserNotFoundException;
import com.demo.finance.out.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.instancio.Instancio;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class JwtServiceImplTest {

    private static final String SECRET_KEY = "jwtSecretSuperSecureKeyThatIsAtLeast64CharactersLongForHS512Algorithm";
    private static final long JWT_EXPIRATION_IN_MS = 60000;
    private JwtServiceImpl jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(SECRET_KEY, userRepository, userMapper);
        setJwtExpiration(jwtService, JWT_EXPIRATION_IN_MS);
        user = Instancio.create(User.class);
        user.setUserId(1L);
        userDto = Instancio.create(UserDto.class);
        userDto.setUserId(1L);
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
    }

    @Test
    @DisplayName("Validate token should return user when token is valid")
    void validateToken_ShouldReturnUser_WhenTokenIsValid() {
        String email = "test@example.com";

        String token = jwtService.generateToken(email, List.of("USER"), 1L);

        when(userRepository.findById(1L)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = jwtService.validateToken(token);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());

        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    @DisplayName("Validate token should throw ExpiredJwtException when token is expired")
    void validateToken_ShouldThrow_WhenTokenIsExpired() {
        setJwtExpiration(jwtService, -1000); // token already expired
        String token = jwtService.generateToken("test@example.com", List.of("USER"), 1L);

        assertThrows(ExpiredJwtException.class, () -> jwtService.validateToken(token));
    }

    @Test
    @DisplayName("Validate token should throw when user not found")
    void validateToken_ShouldThrow_WhenUserNotFound() {
        String token = jwtService.generateToken("nonexistent@example.com", List.of("USER"), 1L);

        when(userRepository.findById(1L)).thenReturn(null);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> jwtService.validateToken(token));

        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Validate token should throw when token is invalid")
    void validateToken_ShouldThrow_WhenTokenIsInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> jwtService.validateToken("invalid.token.here"));
    }

    @Test
    @DisplayName("Token with empty roles should be handled")
    void validateToken_ShouldHandleEmptyRoles() {
        String email = "test@example.com";
        String token = jwtService.generateToken(email, List.of(), 1L);

        when(userRepository.findById(1L)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = jwtService.validateToken(token);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).toDto(user);
    }
}