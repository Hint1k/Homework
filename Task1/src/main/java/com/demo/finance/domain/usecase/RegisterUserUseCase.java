package com.demo.finance.domain.usecase;

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

    public boolean registerUser(String id, String name, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            return false;
        }

        String hashedPassword = passwordService.hashPassword(password);
        User newUser = new User(id, name, email, hashedPassword, false);
        userRepository.save(newUser);
        return true;
    }

    public Optional<User> authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordService.checkPassword(password, user.getPassword()));
    }
}