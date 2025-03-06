package com.demo.finance.in.controller;

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

    public boolean registerUser(String id, String name, String email, String password) {
        return registerUserUseCase.registerUser(id, name, email, password);
    }

    public Optional<User> authenticateUser(String email, String password) {
        return registerUserUseCase.authenticate(email, password);  // Use the correct use case for authentication
    }

    public Optional<User> getUserById(String id) {
        return manageUsersUseCase.getUserById(id);
    }

    public boolean updateUser(String id, String name, String email, String password) {
        return manageUsersUseCase.updateUser(id, name, email, password);
    }

    public boolean deleteUser(String id) {
        return manageUsersUseCase.deleteUser(id);
    }
}