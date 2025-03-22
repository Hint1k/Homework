package com.demo.finance.out.service;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;

import java.util.List;

/**
 * The {@code AdminService} interface defines the contract for managing user-related administrative actions.
 * It provides methods for retrieving all users, updating user roles, and performing actions like blocking,
 * unblocking, and deleting users.
 */
public interface AdminService {

    /**
     * Retrieves a list of all users in the system.
     *
     * @return a {@code List<User>} containing all users
     */
    List<User> getAllUsers();

    /**
     * Updates the role of a user with the specified user ID.
     *
     * @param userId the ID of the user whose role needs to be updated
     * @param newRole the new role to assign to the user
     * @return {@code true} if the role was updated successfully, {@code false} if the user was not found
     */
    boolean updateUserRole(Long userId, Role newRole);

    /**
     * Blocks the user with the specified user ID.
     *
     * @param userId the ID of the user to be blocked
     * @return {@code true} if the user was successfully blocked, {@code false} if the user was not found
     */
    boolean blockUser(Long userId);

    /**
     * Unblocks the user with the specified user ID.
     *
     * @param userId the ID of the user to be unblocked
     * @return {@code true} if the user was successfully unblocked, {@code false} if the user was not found
     */
    boolean unBlockUser(Long userId);

    /**
     * Deletes the user with the specified user ID.
     *
     * @param userId the ID of the user to be deleted
     * @return {@code true} if the user was successfully deleted, {@code false} if the user was not found
     */
    boolean deleteUser(Long userId);
}