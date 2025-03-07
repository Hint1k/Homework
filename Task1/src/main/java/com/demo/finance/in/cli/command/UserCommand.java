package com.demo.finance.in.cli.command;

import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.in.cli.Menu;

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
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        Role role = new Role("user"); // Default role

        if (context.getUserController().registerUser(name, email, password, role)) {
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
            if (user.get().isBlocked()) {
                System.out.println("User is blocked. Contact admin.");
                return;
            }
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

    public void deleteUser() {
        User user = context.getCurrentUser();
        if (user != null) {
            Long userId = user.getUserId();
            if (context.getUserController().deleteUser(userId)) {
                System.out.println("User deleted successfully.");
            } else {
                System.out.println("Failed to delete user.");
            }
        } else {
            System.out.println("No user is currently logged in.");
        }
    }

    public void updateUser() {
        User user = context.getCurrentUser();
        if (user != null) {
            System.out.print("Enter new name: ");
            String name = scanner.nextLine();
            System.out.print("Enter new email: ");
            String email = scanner.nextLine();
            System.out.print("Enter new password: ");
            String password = scanner.nextLine();
            Long userId = user.getUserId();
            Role role = user.getRole();
            if (context.getUserController().updateUser(userId, name, email, password, role)) {
                System.out.println("User updated successfully. Please log in again with your new credentials.");
                context.setCurrentUser(null); // Log out the user and force them to re-login
            } else {
                System.out.println("Failed to update user.");
            }
        } else {
            System.out.println("No user is currently logged in.");
        }
    }

    public void showUserDetails() {
        User user = context.getCurrentUser();
        if (user != null) {
            System.out.println("User Details:");
            System.out.println("ID: " + user.getUserId());
            System.out.println("Name: " + user.getName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Role: " + user.getRole().getName());
        } else {
            System.out.println("No user is currently logged in.");
        }
    }
}