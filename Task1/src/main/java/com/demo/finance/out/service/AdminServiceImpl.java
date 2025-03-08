package com.demo.finance.out.service;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.UserRepository;

import java.util.List;

public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean updateUserRole(Long userId, Role newRole) {
        return userRepository.findByUserId(userId).map(user -> {
            user.setRole(newRole);
            return userRepository.update(user);
        }).orElse(false);
    }

    @Override
    public boolean blockUser(Long userId) {
        return userRepository.findByUserId(userId).map(user -> {
            user.setBlocked(true);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean unBlockUser(Long userId) {
        return userRepository.findByUserId(userId).map(user -> {
            user.setBlocked(false);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean deleteUser(Long userId) {
        return userRepository.delete(userId);
    }
}