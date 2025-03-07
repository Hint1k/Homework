package com.demo.finance.domain.usecase;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.UserRepository;

import java.util.Optional;

import com.demo.finance.domain.utils.PasswordUtils;

public class RegistrationUseCase {
    private final UserRepository userRepository;
    private final PasswordUtils passwordUtils;

    public RegistrationUseCase(UserRepository userRepository, PasswordUtils passwordUtils) {
        this.userRepository = userRepository;
        this.passwordUtils = passwordUtils;
    }

    public boolean registerUser(String name, String email, String password, Role role) {
        if (userRepository.findByEmail(email).isPresent()) {
            return false;
        }
        Long userId = userRepository.generateNextId();
        String hashedPassword = passwordUtils.hashPassword(password);
        User newUser = new User(userId, name, email, hashedPassword, false, role);
        userRepository.save(newUser);
        return true;
    }

    public Optional<User> authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordUtils.checkPassword(password, user.getPassword()));
    }
}