package com.demo.finance.domain.usecase;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.UserRepository;

import java.util.Optional;

import com.demo.finance.out.service.PasswordService;

public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public RegisterUserUseCase(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public boolean registerUser(String name, String email, String password, Role role) {
        if (userRepository.findByEmail(email).isPresent()) {
            return false;
        }
        Long userId = userRepository.generateNextId();
        String hashedPassword = passwordService.hashPassword(password);
        User newUser = new User(userId, name, email, hashedPassword, false, role);
        userRepository.save(newUser);
        return true;
    }

    public Optional<User> authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordService.checkPassword(password, user.getPassword()));
    }
}