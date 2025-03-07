package com.demo.finance.domain.usecase;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.UserRepository;

import java.util.List;

public class AdminUseCase {

    private final UserRepository userRepository;

    public AdminUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean updateUserRole(Long userId, Role newRole) {
        return userRepository.findByUserId(userId).map(user -> {
            user.setRole(newRole);
            return userRepository.update(user);
        }).orElse(false);
    }

    public boolean blockUser(Long userId) {
        return userRepository.findByUserId(userId).map(user -> {
            user.setBlocked(true);
            return true;
        }).orElse(false);
    }

    public boolean unBlockUser(Long userId) {
        return userRepository.findByUserId(userId).map(user -> {
            user.setBlocked(false);
            return true;
        }).orElse(false);
    }

    public boolean deleteUser(Long userId) {
        return userRepository.delete(userId);
    }
}