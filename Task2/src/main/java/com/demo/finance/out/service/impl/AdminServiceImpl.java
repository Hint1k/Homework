package com.demo.finance.out.service.impl;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.AdminService;

import java.util.List;

/**
 * {@code AdminServiceImpl} is an implementation of the {@code AdminService} interface.
 * It provides the functionality to perform administrative actions such as retrieving users,
 * updating roles, blocking/unblocking, and deleting users.
 */
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    /**
     * Constructs an {@code AdminServiceImpl} instance with the specified {@code UserRepository}.
     *
     * @param userRepository the repository to interact with user data
     */
    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves a list of all users from the repository.
     *
     * @return a {@code List<User>} containing all users
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Updates the role of a user with the specified user ID.
     *
     * @param userId  the ID of the user whose role needs to be updated
     * @param newRole the new role to assign to the user
     * @return {@code true} if the role was updated successfully, {@code false} if the user was not found
     */
    @Override
    public boolean updateUserRole(Long userId, Role newRole) {
        return userRepository.findById(userId).map(user -> {
            user.setRole(newRole);
            return userRepository.update(user);
        }).orElse(false);
    }

    /**
     * Blocks the user with the specified user ID.
     *
     * @param userId the ID of the user to be blocked
     * @return {@code true} if the user was successfully blocked, {@code false} if the user was not found
     */
    @Override
    public boolean blockUser(Long userId) {
        return userRepository.findById(userId).map(user -> {
            user.setBlocked(true);
            return userRepository.update(user);
        }).orElse(false);
    }

    /**
     * Unblocks the user with the specified user ID.
     *
     * @param userId the ID of the user to be unblocked
     * @return {@code true} if the user was successfully unblocked, {@code false} if the user was not found
     */
    @Override
    public boolean unBlockUser(Long userId) {
        return userRepository.findById(userId).map(user -> {
            user.setBlocked(false);
            return userRepository.update(user);
        }).orElse(false);
    }

    /**
     * Deletes the user with the specified user ID.
     *
     * @param userId the ID of the user to be deleted
     * @return {@code true} if the user was successfully deleted, {@code false} if the user was not found
     */
    @Override
    public boolean deleteUser(Long userId) {
        return userRepository.delete(userId);
    }
}