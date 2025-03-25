package com.demo.finance.out.service;

import com.demo.finance.domain.dto.UserDto;

/**
 * The {@code RegistrationService} interface defines the contract for user registration and authentication operations.
 * It provides methods to register new users and authenticate existing users in the system.
 */
public interface RegistrationService {

    /**
     * Registers a new user in the system based on the provided user data.
     *
     * @param userDto the {@link UserDto} object containing the details of the user to register
     * @return {@code true} if the registration was successful, {@code false} otherwise
     */
    boolean registerUser(UserDto userDto);

    /**
     * Authenticates a user by verifying their credentials against the system's records.
     *
     * @param userDto the {@link UserDto} object containing the user's authentication details (e.g., username and password)
     * @return {@code true} if the authentication was successful, {@code false} otherwise
     */
    boolean authenticate(UserDto userDto);
}