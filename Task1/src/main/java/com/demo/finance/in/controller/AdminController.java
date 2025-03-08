package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.service.AdminService;

import java.util.List;

public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    public List<User> getAllUsers() {
        return adminService.getAllUsers();
    }

    public boolean updateUserRole(Long userId, Role newRole) {
        return adminService.updateUserRole(userId, newRole);
    }

    public boolean blockUser(Long userId) {
        return adminService.blockUser(userId);
    }

    public boolean unBlockUser(Long userId) {
        return adminService.unBlockUser(userId);
    }

    public boolean deleteUser(Long userId) {
        return adminService.deleteUser(userId);
    }
}