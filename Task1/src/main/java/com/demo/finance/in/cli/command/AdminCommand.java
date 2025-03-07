package com.demo.finance.in.cli.command;

import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.model.Role;

import java.util.List;
import java.util.Scanner;

public class AdminCommand {
    private final CommandContext context;
    private final Scanner scanner;
    private final static Long DEFAULT_ADMIN_ID = 1L;

    public AdminCommand(CommandContext context, Scanner scanner) {
        this.context = context;
        this.scanner = scanner;
    }

    public void viewAllUsers() {
        List<User> users = context.getAdminController().getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            users.forEach(System.out::println);
        }
    }

    public void updateUserRole() {
        System.out.print("Enter User ID to modify: ");
        Long userId = Long.parseLong(scanner.nextLine());
        System.out.print("Set role (1=User, 2=Admin): ");
        String roleChoice = scanner.nextLine();
        Role newRole = "2".equals(roleChoice) ? new Role("admin") : new Role("user");

        if (context.getAdminController().updateUserRole(userId, newRole)) {
            System.out.println("User role updated successfully.");
        } else {
            System.out.println("Failed to update user role.");
        }
    }

    public void blockUser() {
        System.out.print("Enter User ID to block: ");
        Long userId = Long.parseLong(scanner.nextLine());
        if (DEFAULT_ADMIN_ID.equals(userId)) {
            System.out.println("Default Admin can't be blocked.");
            return; // non default admins can be blocked
        }
        if (context.getAdminController().blockUser(userId)) {
            System.out.println("User blocked successfully.");
        } else {
            System.out.println("Failed to block user.");
        }
    }

    public void unblockUser() {
        System.out.print("Enter User ID to unblock: ");
        Long userId = Long.parseLong(scanner.nextLine());
        if (context.getAdminController().unBlockUser(userId)) {
            System.out.println("User unblocked successfully.");
        } else {
            System.out.println("Failed to unblock user.");
        }
    }

    public void deleteUser() {
        System.out.print("Enter User ID to delete: ");
        Long userId = Long.parseLong(scanner.nextLine());
        if (context.getAdminController().deleteUser(userId)) {
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("Failed to delete user.");
        }
    }
}