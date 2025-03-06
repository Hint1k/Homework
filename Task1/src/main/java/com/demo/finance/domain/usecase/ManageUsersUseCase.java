package com.demo.finance.domain.usecase;

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

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public boolean updateUser(String id, String name, String email, String password) {
        String hashedPassword = passwordService.hashPassword(password);
        User updatedUser = new User(id, name, email, hashedPassword, false);
        return userRepository.update(updatedUser);
    }

    public boolean deleteUser(String id) {
        return userRepository.delete(id);
    }
}