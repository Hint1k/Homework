package com.demo.finance.in.cli.command;

import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;

import java.util.Optional;
import java.util.Scanner;

import java.util.regex.Pattern;

public class UserCommand {
    private final CommandContext context;
    private final Scanner scanner;

    public UserCommand(CommandContext context, Scanner scanner) {
        this.context = context;
        this.scanner = scanner;
    }

    public void registerUser() {
        String name = promptForNonEmptyString("Enter Name: ");
        String email = promptForValidEmail("Enter Email: ");
        String password = promptForValidPassword("Enter Password: ");
        Role role = new Role("user"); // Default role

        if (context.getUserController().registerUser(name, email, password, role)) {
            System.out.println("Registration successful.");
        } else {
            System.out.println("Registration failed. Email already exists.");
        }
    }

    public void loginUser() {
        String email = promptForValidEmail("Enter Email: ");
        String password = promptForValidPassword("Enter Password: ");

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

    public void deleteOwnAccount() {
        User user = context.getCurrentUser();
        if (user != null) {
            Long userId = user.getUserId();
            if (context.getUserController().deleteOwnAccount(userId)) {
                System.out.println("User deleted successfully.");
                context.setCurrentUser(null); // Log out the user
            } else {
                System.out.println("Failed to delete user.");
            }
        } else {
            System.out.println("No user is currently logged in.");
        }
    }

    public void updateOwnAccount() {
        User user = context.getCurrentUser();
        if (user != null) {
            String name = promptForNonEmptyString("Enter new name: ");
            String email = promptForValidEmail("Enter new email: ");
            String password = promptForValidPassword("Enter new password: ");

            Long userId = user.getUserId();
            Role role = user.getRole();
            if (context.getUserController().updateOwnAccount(userId, name, email, password, role)) {
                System.out.println("User updated successfully. Please log in again with your new credentials.");
                context.setCurrentUser(null); // Log out the user
            } else {
                System.out.println("Failed to update user.");
            }
        } else {
            System.out.println("No user is currently logged in.");
        }
    }

    public void showOwnDetails() {
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

    private String promptForNonEmptyString(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("Error: Input cannot be empty.");
        }
    }

    private String promptForValidEmail(String message) {
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        while (true) {
            System.out.print(message);
            String email = scanner.nextLine().trim();
            if (emailPattern.matcher(email).matches()) return email;
            System.out.println("Error: Invalid email format. Please enter a valid email (e.g. user@demo.com).");
        }
    }

    private String promptForValidPassword(String message) {
        while (true) {
            System.out.print(message);
            String password = scanner.nextLine().trim();
            if (password.length() >= 3) return password;
            System.out.println("Error: Password must be at least 3 characters long.");
        }
    }
}