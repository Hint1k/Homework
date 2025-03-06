package com.demo.finance.in.cli.command;

import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserCommand {
    private final CommandContext context;
    private final Scanner scanner;

    public UserCommand(CommandContext context, Scanner scanner) {
        this.context = context;
        this.scanner = scanner;
    }

    public void registerUser() {
        System.out.print("Enter User ID: ");
        Long userId = scanner.nextLong();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        Role role = new Role("user"); // Default role

        if (context.getUserController().registerUser(userId, name, email, password, role)) {
            System.out.println("Registration successful.");
        } else {
            System.out.println("Registration failed. Email already exists.");
        }
    }

    public void loginUser() {
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        Optional<User> user = context.getUserController().authenticateUser(email, password);

        if (user.isPresent()) {
            context.setCurrentUser(user.get());
            System.out.println("Login successful.");
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    public void logoutUser() {
        context.setCurrentUser(null);
        System.out.println("Logged out successfully.");
    }

    public void updateUserRole() {
        System.out.print("Enter User ID to modify: ");
        Long userId = scanner.nextLong();
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
        Long userId = scanner.nextLong();
        if (context.getAdminController().blockUser(userId)) {
            System.out.println("User blocked successfully.");
        } else {
            System.out.println("Failed to block user.");
        }
    }

    public void deleteUser() {
        System.out.print("Enter User ID to delete: ");
        Long userId = scanner.nextLong();
        if (context.getAdminController().deleteUser(userId)) {
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("Failed to delete user.");
        }
    }

    public void viewAllUsers() {
        List<User> users = context.getAdminController().getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            users.forEach(System.out::println);
        }
    }
}