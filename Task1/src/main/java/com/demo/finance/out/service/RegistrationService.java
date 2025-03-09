package com.demo.finance.out.service;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;

import java.util.Optional;

/**
 * The {@code RegistrationService} interface defines the methods required for user registration
 * and authentication services.
 */
public interface RegistrationService {

    /**
     * Registers a new user with the provided details.
     *
     * @param name     the name of the user
     * @param email    the email of the user
     * @param password the password for the user
     * @param role     the role assigned to the user
     * @return {@code true} if the user was successfully registered, {@code false} otherwise
     */
    boolean registerUser(String name, String email, String password, Role role);

    /**
     * Authenticates a user based on the provided email and password.
     *
     * @param email    the email of the user
     * @param password the password for the user
     * @return an {@link Optional} containing the authenticated {@link User} if authentication is successful,
     *         or {@code Optional.empty()} if authentication fails
     */
    Optional<User> authenticate(String email, String password);
}