package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.service.RegistrationService;
import com.demo.finance.out.service.UserService;

import java.util.Optional;

public class UserController {
    private final RegistrationService registrationService;
    private final UserService userService;

    public UserController(RegistrationService registrationService, UserService userService) {
        this.registrationService = registrationService;
        this.userService = userService;
    }

    public boolean registerUser(String name, String email, String password, Role role) {
        return registrationService.registerUser(name, email, password, role);
    }

    public Optional<User> authenticateUser(String email, String password) {
        return registrationService.authenticate(email, password);
    }

    public boolean updateOwnAccount(Long userId, String name, String email, String password, Role role) {
        return userService.updateOwnAccount(userId, name, email, password, role);
    }

    public boolean deleteOwnAccount(Long userId) {
        return userService.deleteOwnAccount(userId);
    }
}