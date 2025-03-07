package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.usecase.RegisterUserUseCase;
import com.demo.finance.domain.usecase.ManageUsersUseCase;

import java.util.Optional;

public class UserController {
    private final RegisterUserUseCase registerUserUseCase;
    private final ManageUsersUseCase manageUsersUseCase;

    public UserController(RegisterUserUseCase registerUserUseCase, ManageUsersUseCase manageUsersUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.manageUsersUseCase = manageUsersUseCase;
    }

    public boolean registerUser(String name, String email, String password, Role role) {
        return registerUserUseCase.registerUser(name, email, password, role);
    }

    public Optional<User> authenticateUser(String email, String password) {
        return registerUserUseCase.authenticate(email, password);
    }

    public Optional<User> getUserById(Long userId) {
        return manageUsersUseCase.getUserById(userId);
    }

    public boolean updateUser(Long userId, String name, String email, String password, Role role) {
        return manageUsersUseCase.updateUser(userId, name, email, password, role);
    }

    public boolean deleteUser(Long userId) {
        return manageUsersUseCase.deleteUser(userId);
    }
}