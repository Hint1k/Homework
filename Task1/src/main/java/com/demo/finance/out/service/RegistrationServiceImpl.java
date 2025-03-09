package com.demo.finance.out.service;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.domain.utils.PasswordUtils;

import java.util.Optional;

/**
 * The {@code RegistrationServiceImpl} class implements the {@code RegistrationService} interface.
 * It handles user registration and authentication, including password hashing and validation.
 */
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final PasswordUtils passwordUtils;

    /**
     * Constructs a new {@code RegistrationServiceImpl} with the provided {@code UserRepository}
     * and {@code PasswordUtils}.
     *
     * @param userRepository the repository to interact with user data
     * @param passwordUtils the utility class for password hashing and validation
     */
    public RegistrationServiceImpl(UserRepository userRepository, PasswordUtils passwordUtils) {
        this.userRepository = userRepository;
        this.passwordUtils = passwordUtils;
    }

    /**
     * Registers a new user if the email is not already in use.
     * The password is hashed before storing the user information.
     *
     * @param name     the name of the user
     * @param email    the email of the user
     * @param password the password for the user
     * @param role     the role of the user (e.g., admin, user)
     * @return {@code true} if the user is successfully registered, {@code false} if the email is already in use
     */
    @Override
    public boolean registerUser(String name, String email, String password, Role role) {
        if (userRepository.findByEmail(email).isPresent()) {
            return false;
        }
        Long userId = userRepository.generateNextId();
        String hashedPassword = passwordUtils.hashPassword(password);
        User newUser = new User(userId, name, email, hashedPassword, false, role);
        userRepository.save(newUser);
        return true;
    }

    /**
     * Authenticates a user by verifying the provided email and password.
     * The password is validated against the stored hashed password.
     *
     * @param email    the email of the user
     * @param password the password provided for authentication
     * @return an {@link Optional} containing the authenticated {@link User} if the credentials are valid,
     *         or {@code Optional.empty()} if authentication fails
     */
    @Override
    public Optional<User> authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordUtils.checkPassword(password, user.getPassword()));
    }
}