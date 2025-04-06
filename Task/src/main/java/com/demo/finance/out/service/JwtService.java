package com.demo.finance.out.service;

import com.demo.finance.domain.dto.UserDto;

import java.util.List;

/**
 * Interface for the JWT (JSON Web Token) service that provides methods for generating and validating JWT tokens.
 * <p>
 * This service is responsible for generating JWT tokens for user authentication and authorization. It also validates
 * JWT tokens to authenticate the user and extract relevant user information.
 * </p>
 */
public interface JwtService {

    /**
     * Generates a JWT token for the specified user.
     * <p>
     * This method generates a JWT token that includes the user's email, roles, and user ID, and sets an expiration
     * time based on the application's configuration.
     * </p>
     *
     * @param email  the email of the user to generate the token for
     * @param roles  a list of roles assigned to the user, which will be included in the token
     * @param userId the unique identifier of the user, which will be included in the token
     * @return a JWT token as a string
     */
    String generateToken(String email, List<String> roles, Long userId);

    /**
     * Validates the provided JWT token and returns the corresponding user details.
     * <p>
     * This method parses the JWT token, checks for expiration, and retrieves the user associated with the token.
     * If the token is invalid or the user does not exist, an exception will be thrown.
     * </p>
     *
     * @param token the JWT token to validate
     * @return a {@link UserDto} object containing the user details associated with the token
     * @throws IllegalArgumentException if the token is invalid or the user is not found
     */
    UserDto validateToken(String token);
}