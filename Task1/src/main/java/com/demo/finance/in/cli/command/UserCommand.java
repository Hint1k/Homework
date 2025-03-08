package com.demo.finance.in.cli.command;

import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;

import java.util.Optional;
import java.util.Scanner;

public class UserCommand {

    private final Scanner scanner;
    private final CommandContext context;
    private final ValidationUtils validationUtils;
    private static final String PROMPT_EMAIL = "Enter email address: ";
    private static final String PROMPT_PASSWORD = "Enter password: ";
    private static final String NO_USER_LOGGED_IN = "No user is currently logged in.";
    private static final String OR_KEEP_CURRENT_VALUE = " or leave it blank to keep current value: ";

    public UserCommand(CommandContext context, ValidationUtils validationUtils, Scanner scanner) {
        this.scanner = scanner;
        this.context = context;
        this.validationUtils = validationUtils;
    }

    public void registerUser() {
        String name = validationUtils.promptForNonEmptyString("Enter Name: ", scanner);
        String email = validationUtils.promptForValidEmail(PROMPT_EMAIL, scanner);
        String password = validationUtils.promptForValidPassword(PROMPT_PASSWORD, scanner);
        Role role = new Role("user"); // Default role

        if (context.getUserController().registerUser(name, email, password, role)) {
            System.out.println("Registration successful.");
        } else {
            System.out.println("Registration failed. Email already exists.");
        }
    }

    public void loginUser() {
        String email = validationUtils.promptForValidEmail(PROMPT_EMAIL, scanner);
        String password = validationUtils.promptForValidPassword(PROMPT_PASSWORD, scanner);

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
            System.out.println(NO_USER_LOGGED_IN);
        }
    }

    public void updateOwnAccount() {
        User user = context.getCurrentUser();
        if (user != null) {
            String name = validationUtils.promptForOptionalString("Enter new name"
                    + OR_KEEP_CURRENT_VALUE, scanner);
            if (name == null) {
                name = user.getName();
            }
            String email = validationUtils.promptForOptionalEmail("Enter new email"
                    + OR_KEEP_CURRENT_VALUE, scanner);
            if (email == null) {
                email = user.getEmail();
            }
            String password = validationUtils.promptForOptionalPassword("Enter new password"
                    + OR_KEEP_CURRENT_VALUE, scanner);
            if (password == null) {
                password = user.getPassword();
            }
            Long userId = user.getUserId();
            Role role = user.getRole();
            if (context.getUserController().updateOwnAccount(userId, name, email, password, role)) {
                System.out.println("User updated successfully. Please log in again with your new credentials.");
                context.setCurrentUser(null); // Log out the user
            } else {
                System.out.println("Failed to update user.");
            }
        } else {
            System.out.println(NO_USER_LOGGED_IN);
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
            System.out.println(NO_USER_LOGGED_IN);
        }
    }
}