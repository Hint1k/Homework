package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The {@code AdminServiceImpl} class implements the {@link AdminService} interface
 * and provides concrete implementations for administrative operations related to user management.
 * It interacts with the database through the {@link UserRepository} to handle tasks such as retrieving,
 * updating, blocking/unblocking, and deleting users.
 */
@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    /**
     * Constructs a new instance of {@code AdminServiceImpl} with the provided repository.
     *
     * @param userRepository the repository used to interact with user data in the database
     */
    @Autowired
    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
     * Updates the role of the user with the specified user ID.
     * This method retrieves the user, sets the new role from the provided {@link UserDto},
     * increments the version, and saves the updated user. Returns true if successful.
     *
     * @param userId  the unique identifier of the user whose role is being updated
     * @param userDto the {@link UserDto} containing the new role information
     * @return true if the role is successfully updated, false otherwise
     */
    @Override
    public boolean updateUserRole(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return false;
        }
        Role newRole = userDto.getRole();
        user.setRole(newRole);
        user.setVersion(user.getVersion() + 1);
        return userRepository.update(user);
    }

    /**
     * Blocks or unblocks the user with the specified user ID.
     * This method retrieves the user, updates the blocked status from the provided {@link UserDto},
     * increments the version, and saves the updated user. Returns true if successful.
     *
     * @param userId  the unique identifier of the user to block or unblock
     * @param userDto the {@link UserDto} containing the blocked status
     * @return true if the user's blocked status is successfully updated, false otherwise
     */
    @Override
    public boolean blockOrUnblockUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return false;
        }
        boolean blocked = userDto.isBlocked();
        user.setBlocked(blocked);
        user.setVersion(user.getVersion() + 1);
        return userRepository.update(user);
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