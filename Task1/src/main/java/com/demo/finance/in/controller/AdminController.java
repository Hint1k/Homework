package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.usecase.AdminUseCase;

import java.util.List;

public class AdminController {
    private final AdminUseCase adminUseCase;

    public AdminController(AdminUseCase adminUseCase) {
        this.adminUseCase = adminUseCase;
    }

    public List<User> getAllUsers() {
        return adminUseCase.getAllUsers();
    }

    public boolean updateUserRole(Long userId, Role newRole) {
        return adminUseCase.updateUserRole(userId, newRole);
    }

    public boolean blockUser(Long userId) {
        return adminUseCase.blockUser(userId);
    }

    public boolean unBlockUser(Long userId) {
        return adminUseCase.unBlockUser(userId);
    }

    public boolean deleteUser(Long userId) {
        return adminUseCase.deleteUser(userId);
    }
}