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
     * Updates the role of a specific user based on the provided user data.
     *
     * @param userDto the {@link UserDto} object containing updated role information for the user
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    boolean updateUserRole(UserDto userDto);

    /**
     * Blocks or unblocks a specific user in the system.
     *
     * @param userId  the unique identifier of the user
     * @param blocked a boolean flag indicating whether to block ({@code true}) or unblock ({@code false}) the user
     * @return {@code true} if the operation was successful, {@code false} otherwise
     */
    boolean blockOrUnblockUser(Long userId, boolean blocked);

    /**
     * Deletes a specific user from the system based on their unique user ID.
     *
     * @param userId the unique identifier of the user to delete
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    boolean deleteUser(Long userId);
}