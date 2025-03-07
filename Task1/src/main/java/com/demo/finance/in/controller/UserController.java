package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.usecase.RegistrationUseCase;
import com.demo.finance.domain.usecase.UsersUseCase;

import java.util.Optional;

public class UserController {
    private final RegistrationUseCase registrationUseCase;
    private final UsersUseCase usersUseCase;

    public UserController(RegistrationUseCase registrationUseCase, UsersUseCase usersUseCase) {
        this.registrationUseCase = registrationUseCase;
        this.usersUseCase = usersUseCase;
    }

    public boolean registerUser(String name, String email, String password, Role role) {
        return registrationUseCase.registerUser(name, email, password, role);
    }

    public Optional<User> authenticateUser(String email, String password) {
        return registrationUseCase.authenticate(email, password);
    }

    public Optional<User> getUserById(Long userId) {
        return usersUseCase.getUserById(userId);
    }

    public boolean updateUser(Long userId, String name, String email, String password, Role role) {
        return usersUseCase.updateUser(userId, name, email, password, role);
    }

    public boolean deleteUser(Long userId) {
        return usersUseCase.deleteUser(userId);
    }
}