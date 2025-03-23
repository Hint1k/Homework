package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.AdminService;

/**
 * The {@code AdminServiceImpl} class implements the {@link AdminService} interface
 * and provides concrete implementations for administrative operations related to user management.
 * It interacts with the database through the {@link UserRepository} to handle tasks such as retrieving,
 * updating, blocking/unblocking, and deleting users.
 */
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    /**
     * Constructs a new instance of {@code AdminServiceImpl} with the provided repository.
     *
     * @param userRepository the repository used to interact with user data in the database
     */
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
     * Updates the role of a specific user based on the provided user data.
     *
     * @param userDto the {@link UserDto} object containing updated role information for the user
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    @Override
    public boolean updateUserRole(UserDto userDto) {
        Long userId = userDto.getUserId();
        Role newRole = userDto.getRole();
        User user = userRepository.findById(userId);
        if (user == null) {
            return false;
        }
        user.setRole(newRole);
        user.setVersion(user.getVersion() + 1);
        return userRepository.update(user);
    }

    /**
     * Blocks or unblocks a specific user in the system.
     *
     * @param userId  the unique identifier of the user
     * @param blocked a boolean flag indicating whether to block ({@code true}) or unblock ({@code false}) the user
     * @return {@code true} if the operation was successful, {@code false} otherwise
     */
    @Override
    public boolean blockOrUnblockUser(Long userId, boolean blocked) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return false;
        }
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