package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.service.RegistrationService;
import com.demo.finance.out.service.UserService;

import java.util.Optional;

/**
 * The {@code UserController} class handles incoming requests related to user registration and account management.
 * It provides methods to register, authenticate, update, and delete user accounts.
 */
public class UserController {
    private final RegistrationService registrationService;
    private final UserService userService;

    /**
     * Constructs a {@code UserController} with the specified {@code RegistrationService} and {@code UserService}.
     *
     * @param registrationService the {@code RegistrationService} used for user registration and authentication
     * @param userService the {@code UserService} used for managing user accounts
     */
    public UserController(RegistrationService registrationService, UserService userService) {
        this.registrationService = registrationService;
        this.userService = userService;
    }

    /**
     * Registers a new user with the specified information.
     *
     * @param name the name of the user
     * @param email the email of the user
     * @param password the password of the user
     * @param role the role assigned to the user
     * @return {@code true} if the user was successfully registered, {@code false} otherwise
     */
    public boolean registerUser(String name, String email, String password, Role role) {
        return registrationService.registerUser(name, email, password, role);
    }

    /**
     * Authenticates a user based on their email and password.
     *
     * @param email the email of the user
     * @param password the password of the user
     * @return an {@code Optional<User>} object containing the authenticated user if successful,
     *         or an empty {@code Optional} if authentication fails
     */
    public Optional<User> authenticateUser(String email, String password) {
        return registrationService.authenticate(email, password);
    }

    /**
     * Updates the account details of the specified user.
     *
     * @param userId the ID of the user whose account is to be updated
     * @param name the updated name of the user
     * @param email the updated email of the user
     * @param password the updated password of the user
     * @param role the updated role of the user
     * @return {@code true} if the account was successfully updated, {@code false} otherwise
     */
    public boolean updateOwnAccount(Long userId, String name, String email, String password, Role role) {
        return userService.updateOwnAccount(userId, name, email, password, role);
    }

    /**
     * Deletes the account of the specified user.
     *
     * @param userId the ID of the user whose account is to be deleted
     * @return {@code true} if the account was successfully deleted, {@code false} otherwise
     */
    public boolean deleteOwnAccount(Long userId) {
        return userService.deleteOwnAccount(userId);
    }
}