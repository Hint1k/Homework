package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.utils.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.exception.custom.OptimisticLockException;
import com.demo.finance.exception.custom.UserNotFoundException;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.AdminService;
import com.demo.finance.out.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final TokenService tokenService;

    /**
     * Retrieves a specific user by their unique user ID.
     *
     * @param userId the unique identifier of the user
     * @return the {@link User} object matching the provided user ID, or {@code null} if not found
     */
    @Override
    @Cacheable(value = "users", key = "#userId")
    public User getUser(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Updates the role of a specified user.
     * <p>
     * This method performs the following steps:
     * <ol>
     *   <li>Retrieves the user by ID from the repository.</li>
     *   <li>Updates the user's role using the value from the provided {@link UserDto}.</li>
     *   <li>Sets the version field to support optimistic locking.</li>
     *   <li>Persists the updated user entity back to the repository.</li>
     *   <li>Invalidates the user's existing JWT token to ensure the new role takes effect immediately.</li>
     * </ol>
     * If the update fails due to a version mismatch (indicating concurrent modification),
     * an {@link OptimisticLockException} is thrown to handle the conflict.
     *
     * @param userId  the ID of the user to update (must not be null).
     * @param userDto the DTO containing the new role and version information.
     * @return {@code true} if the update was successful.
     * @throws UserNotFoundException    if no user exists with the specified ID.
     * @throws IllegalArgumentException if input parameters are null or invalid.
     * @throws OptimisticLockException  if a version conflict is detected during update.
     */
    @Override
    @CacheEvict(value = "users", key = "#userId", allEntries = true)
    public boolean updateUserRole(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
        Role newRole = Role.valueOf(userDto.getRole());
        user.setRole(newRole);
        user.setVersion(userDto.getVersion());
        if (!userRepository.update(user)) {
            throw new OptimisticLockException("User with ID " + userId + " was modified. Check version number.");
        }
        tokenService.invalidateUserToken(userId);
        return true;
    }

    /**
     * Updates the blocked status of a specified user.
     * <p>
     * This method performs the following steps:
     * <ol>
     *   <li>Retrieves the user by ID from the repository.</li>
     *   <li>Updates the user's blocked status using the value from the provided {@link UserDto}.</li>
     *   <li>Sets the version field to support optimistic locking.</li>
     *   <li>Persists the updated user entity back to the repository.</li>
     *   <li>Invalidates the user's JWT token to prevent access if the account was blocked.</li>
     * </ol>
     * If the update fails due to a version mismatch (indicating concurrent modification),
     * an {@link OptimisticLockException} is thrown to handle the conflict.
     *
     * @param userId  the ID of the user to update (must not be null).
     * @param userDto the DTO containing the new blocked status and version information.
     * @return {@code true} if the update was successful.
     * @throws UserNotFoundException    if no user exists with the specified ID.
     * @throws IllegalArgumentException if input parameters are null or invalid.
     * @throws OptimisticLockException  if a version conflict is detected during update.
     */
    @Override
    @CacheEvict(value = "users", key = "#userId", allEntries = true)
    public boolean blockOrUnblockUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
        user.setBlocked(userDto.isBlocked());
        user.setVersion(userDto.getVersion());
        if (!userRepository.update(user)) {
            throw new OptimisticLockException("User with ID " + userId + " was modified. Check version number.");
        }
        tokenService.invalidateUserToken(userId);
        return true;
    }

    /**
     * Deletes a specific user from the system based on their unique user ID.
     * If deletion is successful, the user's JWT token is also invalidated to revoke access immediately.
     *
     * @param userId the unique identifier of the user to delete
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    @Override
    @CacheEvict(value = "users", key = "#userId", allEntries = true)
    public boolean deleteUser(Long userId) {
        boolean deleted = userRepository.delete(userId);
        if (deleted) {
            tokenService.invalidateUserToken(userId);
        }
        return deleted;
    }
}