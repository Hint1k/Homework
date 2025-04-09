package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.exception.custom.UserNotFoundException;
import com.demo.finance.out.service.JwtService;
import com.demo.finance.out.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * Implementation of the {@link JwtService} interface for managing JWT token generation and validation.
 * <p>
 * This service is responsible for generating, validating, and parsing JWT tokens. Tokens include
 * claims for user ID, email, and roles, and are signed using a secure HMAC key. The service also handles
 * token expiration and interacts with the {@link TokenService} to manage token lifecycle and enforce
 * single-token-per-user constraints.
 * </p>
 */
@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final SecretKey secretKey;
    private final TokenService tokenService;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    /**
     * Constructs the service with the provided secret and token service dependency.
     *
     * @param secretKey    the secret key used to sign JWT tokens, provided via configuration
     * @param tokenService the token service used for storing and validating tokens
     */
    public JwtServiceImpl(@Value("${jwt.secret}") String secretKey, TokenService tokenService) {
        // Generate a SecretKey instance from the provided string
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.tokenService = tokenService;
    }

    /**
     * Generates a JWT token for the user with the specified email, roles, and user ID.
     * <p>
     * The generated token contains user-specific claims and is signed using a secure HMAC key.
     * It also sets the issued timestamp and an expiration time as configured.
     * The generated token is stored using {@link TokenService} to support single active token per user.
     * </p>
     *
     * @param email  the email of the user
     * @param roles  the roles assigned to the user (must not be null and should be valid)
     * @param userId the ID of the user
     * @return a JWT token string
     */
    @Override
    public String generateToken(String email, List<String> roles, Long userId) {
        String token = Jwts.builder().subject(email).claim("roles", roles).claim("userId", userId)
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(secretKey, Jwts.SIG.HS512).compact();
        tokenService.storeTokenForUser(userId, token);
        return token;
    }

    /**
     * Validates the given JWT token and extracts user information if valid.
     * <p>
     * The method performs the following:
     * <ul>
     *   <li>Parses the token and extracts claims.</li>
     *   <li>Checks if the token has expired.</li>
     *   <li>Checks token validity through {@link TokenService} to detect revocation or replacement.</li>
     *   <li>Maps the claims to a {@link UserDto} object.</li>
     * </ul>
     *
     * @param token the JWT token to validate
     * @return a {@link UserDto} extracted from token claims
     * @throws ExpiredJwtException      if the token has expired
     * @throws UserNotFoundException    if the token is no longer valid (e.g., replaced)
     * @throws IllegalArgumentException if the token is malformed or invalid
     */
    @Override
    public UserDto validateToken(String token) {
        try {
            JwtParser parser = Jwts.parser().verifyWith(secretKey).build();
            Claims claims = parser.parseSignedClaims(token).getPayload();
            Long userId = claims.get("userId", Long.class);
            if (isTokenExpired(token)) {
                throw new ExpiredJwtException(null, null, "JWT Token expired for user ID: " + userId);
            }
            if (!tokenService.isTokenValid(token)) {
                throw new UserNotFoundException("Your account was modified. You have to authenticate again.");
            }
            return createUserDtoFromClaims(claims);
        } catch (ExpiredJwtException | UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token: ", e);
        }
    }

    /**
     * Checks whether the provided JWT token has expired.
     * <p>
     * Extracts the expiration claim from the token and compares it to the current system time.
     * </p>
     *
     * @param token the JWT token to check for expiration
     * @return {@code true} if the token is expired, {@code false} otherwise
     */
    private boolean isTokenExpired(String token) {
        try {
            return extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Extracts a specific claim from the JWT token using a resolver function.
     *
     * @param token          the JWT token from which to extract the claim
     * @param claimsResolver a function that maps the {@link Claims} object to the desired claim
     * @param <T>            the type of the extracted claim
     * @return the extracted claim value
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the JWT token and retrieves all embedded claims.
     *
     * @param token the JWT token to parse
     * @return the {@link Claims} object containing token data
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    /**
     * Maps extracted JWT claims to a {@link UserDto} instance.
     * <p>
     * The method pulls user ID, email (subject), and roles from the claims and populates a {@code UserDto}.
     * Only the first role from the roles list is used in this mapping.
     * </p>
     *
     * @param claims the claims object containing user-related data
     * @return a populated {@link UserDto}
     */
    private UserDto createUserDtoFromClaims(Claims claims) {
        UserDto userDto = new UserDto();
        userDto.setUserId(claims.get("userId", Long.class));
        userDto.setEmail(claims.getSubject());
        userDto.setRole((String) claims.get("roles", List.class).get(0));
        return userDto;
    }
}