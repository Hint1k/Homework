package com.demo.finance.out.service.impl;

import com.demo.finance.domain.utils.PasswordUtils;
import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.UserService;

/**
 * The {@code UserServiceImpl} class provides the implementation of the {@code UserService} interface.
 * It handles user account updates and deletions.
 */
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordUtils passwordUtils;

    /**
     * Constructor to initialize the {@code UserServiceImpl} with the necessary dependencies.
     *
     * @param userRepository the repository for user data storage and retrieval
     * @param passwordUtils utility for password encryption and verification
     */
    public UserServiceImpl(UserRepository userRepository, PasswordUtils passwordUtils) {
        this.userRepository = userRepository;
        this.passwordUtils = passwordUtils;
    }

    /**
     * Updates the account details of the user performing the action.
     *
     * @param userId the ID of the user whose account is to be updated
     * @param name the new name of the user
     * @param email the new email address of the user
     * @param password the new password for the user
     * @param role the new role assigned to the user
     * @return {@code true} if the account was successfully updated, {@code false} otherwise
     */
    @Override
    public boolean updateOwnAccount(Long userId, String name, String email, String password, Role role,
                                    boolean isPasswordUpdated) {
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        String hashedPassword = isPasswordUpdated ? passwordUtils.hashPassword(password) : existingUser.getPassword();
        User updatedUser = new User(userId, name, email, hashedPassword, false, role);
        return userRepository.update(updatedUser);
    }

    /**
     * Deletes the account of the user performing the action.
     *
     * @param userId the ID of the user whose account is to be deleted
     * @return {@code true} if the account was successfully deleted, {@code false} otherwise
     */
    @Override
    public boolean deleteOwnAccount(Long userId) {
        return userRepository.delete(userId);
    }
}