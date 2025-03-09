package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.service.AdminService;

import java.util.List;

/**
 * The {@code AdminController} class handles incoming requests related to admin functionalities. It provides
 * methods to manage users, including viewing all users, updating user roles, blocking/unblocking users, and
 * deleting users. This controller delegates actions to the {@code AdminService}.
 */
public class AdminController {
    private final AdminService adminService;

    /**
     * Constructs an {@code AdminController} with the specified {@code AdminService}.
     *
     * @param adminService the {@code AdminService} used for performing admin actions
     */
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Retrieves all users from the {@code AdminService}.
     *
     * @return a list of all {@code User} objects
     */
    public List<User> getAllUsers() {
        return adminService.getAllUsers();
    }

    /**
     * Updates the role of a user identified by the specified {@code userId}.
     *
     * @param userId the ID of the user whose role is to be updated
     * @param newRole the new role to assign to the user
     * @return {@code true} if the role update was successful, otherwise {@code false}
     */
    public boolean updateUserRole(Long userId, Role newRole) {
        return adminService.updateUserRole(userId, newRole);
    }

    /**
     * Blocks a user identified by the specified {@code userId}.
     *
     * @param userId the ID of the user to be blocked
     * @return {@code true} if the user was successfully blocked, otherwise {@code false}
     */
    public boolean blockUser(Long userId) {
        return adminService.blockUser(userId);
    }

    /**
     * Unblocks a user identified by the specified {@code userId}.
     *
     * @param userId the ID of the user to be unblocked
     * @return {@code true} if the user was successfully unblocked, otherwise {@code false}
     */
    public boolean unBlockUser(Long userId) {
        return adminService.unBlockUser(userId);
    }

    /**
     * Deletes a user identified by the specified {@code userId}.
     *
     * @param userId the ID of the user to be deleted
     * @return {@code true} if the user was successfully deleted, otherwise {@code false}
     */
    public boolean deleteUser(Long userId) {
        return adminService.deleteUser(userId);
    }
}