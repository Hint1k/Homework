package com.demo.finance.domain.usecase;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.UserRepository;

import java.util.Optional;

import com.demo.finance.out.service.PasswordService;

public class ManageUsersUseCase {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public ManageUsersUseCase(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public Optional<User> authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordService.checkPassword(password, user.getPassword()));
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public boolean updateUser(Long userId, String name, String email, String password, Role role) {
        String hashedPassword = passwordService.hashPassword(password);
        User updatedUser = new User(userId, name, email, hashedPassword, false, role);
        return userRepository.update(updatedUser);
    }

    public boolean deleteUser(Long userId) {
        return userRepository.delete(userId);
    }
}