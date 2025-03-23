package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.AdminService;

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

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId);
    }

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
     * Blocks the user with the specified user ID.
     *
     * @param userId the ID of the user to be blocked
     * @return {@code true} if the user was successfully blocked, {@code false} if the user was not found
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