package com.demo.finance.out.service;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.User;

/**
 * The {@code AdminService} interface defines the contract for administrative operations related to user management.
 * It provides methods for retrieving, updating, blocking/unblocking, and deleting users in the system.
 */
public interface AdminService {

    /**
     * Retrieves a specific user by their unique user ID.
     *
     * @param userId the unique identifier of the user
     * @return the {@link User} object matching the provided user ID
     */
    User getUser(Long userId);

    /**
     * Updates the role of the user with the specified user ID.
     * This method retrieves the user, sets the new role from the provided {@link UserDto},
     * increments the version, and saves the updated user. Returns true if successful.
     *
     * @param userId  the unique identifier of the user whose role is being updated
     * @param userDto the {@link UserDto} containing the new role information
     * @return true if the role is successfully updated, false otherwise
     */
    boolean updateUserRole(Long userId, UserDto userDto);

    /**
     * Blocks or unblocks the user with the specified user ID.
     * This method retrieves the user, updates the blocked status from the provided {@link UserDto},
     * increments the version, and saves the updated user. Returns true if successful.
     *
     * @param userId  the unique identifier of the user to block or unblock
     * @param userDto the {@link UserDto} containing the blocked status
     * @return true if the user's blocked status is successfully updated, false otherwise
     */
    boolean blockOrUnblockUser(Long userId, UserDto userDto);

    /**
     * Deletes a specific user from the system based on their unique user ID.
     *
     * @param userId the unique identifier of the user to delete
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    boolean deleteUser(Long userId);
}