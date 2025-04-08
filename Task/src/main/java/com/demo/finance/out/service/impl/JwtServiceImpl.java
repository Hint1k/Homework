package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.FlagUtils;
import com.demo.finance.exception.custom.UserNotFoundException;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.JwtService;
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
 * Implementation of the JwtService interface for managing JWT token generation and validation.
 * <p>
 * This service is responsible for generating JWT tokens for authenticated users and validating incoming
 * tokens by extracting user details and roles from the token's claims. The service uses a secret key to sign and
 * verify JWTs and checks for token expiration.
 * </p>
 * <p>
 * This implementation relies on the {@link UserRepository} to fetch user data and {@link UserMapper} to map the
 * user entity to a {@link UserDto}.
 * </p>
 */
@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final SecretKey secretKey;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FlagUtils flagUtils;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    /**
     * Constructs a JwtServiceImpl instance with the specified secret key, user repository, user mapper,
     * and flag utility.
     * <p>
     * This constructor initializes the JwtServiceImpl class by generating a {@link SecretKey} instance from the
     * provided secret key string, and assigns the provided {@link UserRepository}, {@link UserMapper},
     * and {@link FlagUtils}.
     * </p>
     *
     * @param secretKey      the secret key used for signing and verifying JWT tokens, retrieved from
     *                       the application configuration
     * @param userRepository the repository used to fetch user data from the database
     * @param userMapper     the mapper used to convert user entities to DTOs
     * @param flagUtils      utility used to determine if token validation should be performed against the database
     */
    public JwtServiceImpl(@Value("${jwt.secret}") String secretKey,
                          UserRepository userRepository, UserMapper userMapper, FlagUtils flagUtils) {
        // Generate a SecretKey instance from the provided string
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.flagUtils = flagUtils;
    }

    /**
     * Generates a JWT token for the user with the specified email, roles, and user ID.
     * <p>
     * The generated token contains the user's email, roles, and user ID as claims, as well as an issued timestamp
     * and an expiration time defined by the {@code jwt.expiration} property. The token is signed using a secret key.
     * </p>
     *
     * @param email  the email of the user
     * @param roles  the roles assigned to the user (must be non-null and correspond to valid enum values)
     * @param userId the ID of the user
     * @return a JWT token string
     */
    @Override
    public String generateToken(String email, List<String> roles, Long userId) {
        return Jwts.builder().subject(email).claim("roles", roles).claim("userId", userId).issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(secretKey, Jwts.SIG.HS512).compact();
    }

    /**
     * Validates the provided JWT token by parsing it and checking the expiration.
     * <p>
     * This method verifies the JWT token's signature and checks if the token is expired. If valid, it extracts
     * the user's ID from the token, retrieves the corresponding user from the database, and returns a
     * {@link UserDto} with the user's details.
     * </p>
     *
     * @param token the JWT token to validate
     * @return a {@link UserDto} representing the authenticated user
     * @throws IllegalArgumentException if the token is invalid or expired
     * @throws UserNotFoundException    if no user is found with the ID in the token
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
            if (flagUtils.shouldValidateWithDatabase()) {
                User user = userRepository.findById(userId);
                if (user == null) {
                    throw new UserNotFoundException("User not found with ID: " + userId);
                }
                return userMapper.toDto(user);
            } else {
                UserDto userDto = new UserDto();
                userDto.setUserId(userId);
                userDto.setEmail(claims.getSubject());
                userDto.setRole((String) claims.get("roles", List.class).get(0));
                return userDto;
            }
        } catch (ExpiredJwtException | UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token: ", e);
        }
    }

    /**
     * Checks whether the provided JWT token has expired.
     * <p>
     * This method extracts the expiration date from the token and compares it with the current date to determine
     * if the token has expired.
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
     * Extracts a specific claim from the JWT token.
     * <p>
     * This method uses a claim resolver function to retrieve a particular claim (such as expiration date or roles)
     * from the token's claims.
     * </p>
     *
     * @param token          the JWT token from which to extract the claim
     * @param claimsResolver a function that extracts a specific claim from the claims
     * @param <T>            the type of the claim to extract
     * @return the value of the extracted claim
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token.
     * <p>
     * This method parses the signed JWT token and retrieves all the claims associated with the token.
     * </p>
     *
     * @param token the JWT token from which to extract the claims
     * @return the claims extracted from the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }
}