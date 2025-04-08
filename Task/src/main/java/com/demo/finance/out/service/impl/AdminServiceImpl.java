package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.utils.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.exception.custom.OptimisticLockException;
import com.demo.finance.exception.custom.UserNotFoundException;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The {@code AdminServiceImpl} class implements the {@link AdminService} interface
 * and provides concrete implementations for administrative operations related to user management.
 * It interacts with the database through the {@link UserRepository} to handle tasks such as retrieving,
 * updating, blocking/unblocking, and deleting users.
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    /**
     * Retrieves a specific user by their unique user ID.
     *
     * @param userId the unique identifier of the user
     * @return the {@link User} object matching the provided user ID, or {@code null} if not found
     */
    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Updates the role for a specified user.
     * <p>
     * This method performs the following operations:
     * <ol>
     *   <li>Retrieves the user by ID from the repository.</li>
     *   <li>Updates the user's role with the value from the provided {@link UserDto}.</li>
     *   <li>Increments the version field to ensure optimistic locking integrity.</li>
     *   <li>Persists the updated user back to the repository.</li>
     * </ol>
     * If the update fails due to a version mismatch (indicating that the user was modified by another operation),
     * an {@link OptimisticLockException} is thrown to handle the concurrency conflict.
     *
     * @param userId  the ID of the user to update (must not be null).
     * @param userDto the DTO containing the new role information (must contain a valid role).
     * @return true if the update was successful.
     * @throws UserNotFoundException    if no user exists with the specified ID.
     * @throws IllegalArgumentException if either parameter is null or contains invalid data.
     * @throws OptimisticLockException  if the user was modified by another operation, causing a version mismatch.
     */
    @Override
    public boolean updateUserRole(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
        Role newRole = Role.valueOf(userDto.getRole());
        user.setRole(newRole);
        user.setVersion(userDto.getVersion());
        if (!userRepository.update(user)) {
            throw new OptimisticLockException("User with ID " + userId + " was modified by another operation.");
        }
        return true;
    }

    /**
     * Updates the blocked status for a specified user.
     * <p>
     * This method performs the following operations:
     * <ol>
     *   <li>Retrieves the user by ID from the repository.</li>
     *   <li>Updates the user's blocked status with the value from the provided {@link UserDto}.</li>
     *   <li>Increments the version field to ensure optimistic locking integrity.</li>
     *   <li>Persists the updated user back to the repository.</li>
     * </ol>
     * If the update fails due to a version mismatch (indicating that the user was modified by another operation),
     * an {@link OptimisticLockException} is thrown to handle the concurrency conflict.
     *
     * @param userId  the ID of the user to update (must not be null).
     * @param userDto the DTO containing the new blocked status (must contain a valid status).
     * @return true if the update was successful.
     * @throws UserNotFoundException    if no user exists with the specified ID.
     * @throws IllegalArgumentException if either parameter is null or contains invalid data.
     * @throws OptimisticLockException  if the user was modified by another operation, causing a version mismatch.
     */
    @Override
    public boolean blockOrUnblockUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
        user.setBlocked(userDto.isBlocked());
        user.setVersion(user.getVersion());
        if (!userRepository.update(user)) {
            throw new OptimisticLockException("User with ID " + userId + " was modified by another operation.");
        }
        return true;
    }

    /**
     * Deletes a specific user from the system based on their unique user ID.
     *
     * @param userId the unique identifier of the user to delete
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    @Override
    public boolean deleteUser(Long userId) {
        return userRepository.delete(userId);
    }
}