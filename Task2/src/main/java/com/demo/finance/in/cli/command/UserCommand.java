package com.demo.finance.in.cli.command;

import com.demo.finance.exception.MaxRetriesReachedException;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;

import java.util.Optional;
import java.util.Scanner;

/**
 * Command class for handling user-related actions such as registration,
 * login, logout, updating user information, and viewing user details.
 */
public class UserCommand {

    private final Scanner scanner;
    private final CommandContext context;
    private final ValidationUtils validationUtils;
    private static final String PROMPT_EMAIL = "Enter email address: ";
    private static final String PROMPT_PASSWORD = "Enter password: ";
    private static final String NO_USER_LOGGED_IN = "No user is currently logged in.";
    private static final String OR_KEEP_CURRENT_VALUE = " or leave it blank to keep current value: ";

    /**
     * Initializes the UserCommand with the provided context, validation utilities,
     * and scanner.
     *
     * @param context The CommandContext that holds controllers.
     * @param validationUtils Utility for validation.
     * @param scanner Scanner to capture user input.
     */
    public UserCommand(CommandContext context, ValidationUtils validationUtils, Scanner scanner) {
        this.scanner = scanner;
        this.context = context;
        this.validationUtils = validationUtils;
    }

    /**
     * Prompts the user for registration details (name, email, password),
     * and attempts to register a new user.
     * If registration is successful, a confirmation message is displayed.
     */
    public void registerUser() {
        String name, email, password;
        try {
            name = validationUtils.promptForNonEmptyString("Enter Name: ", scanner);
            email = validationUtils.promptForValidEmail(PROMPT_EMAIL, scanner);
            password = validationUtils.promptForValidPassword(PROMPT_PASSWORD, scanner);
        } catch (MaxRetriesReachedException e) {
            System.out.println(e.getMessage());
            return;
        }
        Role role = new Role("user"); // Default role
        if (context.getUserController().registerUser(name, email, password, role)) {
            System.out.println("Registration successful.");
        } else {
            System.out.println("Registration failed. Email already exists.");
        }
    }

    /**
     * Prompts the user for login credentials (email, password),
     * and attempts to authenticate the user.
     * If successful, the user is logged in; otherwise, an error message is displayed.
     */
    public void loginUser() {
        String email, password;
        try {
            email = validationUtils.promptForValidEmail(PROMPT_EMAIL, scanner);
            password = validationUtils.promptForValidPassword(PROMPT_PASSWORD, scanner);
        } catch (MaxRetriesReachedException e) {
            System.out.println(e.getMessage());
            return;
        }
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

    /**
     * Logs the current user out by setting the current user to null.
     */
    public void logoutUser() {
        context.setCurrentUser(null);
        System.out.println("Logged out successfully.");
    }

    /**
     * Deletes the current user's account after verifying their identity.
     * If successful, the user is logged out and their account is removed.
     * If no user is logged in, an error message is shown.
     */
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

    /**
     * Prompts the user to update their account details (name, email, password).
     * If the user leaves a field blank, the current value is retained.
     * After updating, the user is logged out and required to log in again with their new credentials.
     * If no user is logged in, an error message is shown.
     */
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

    /**
     * Displays the current user's details including ID, name, email, and role.
     * If no user is logged in, an error message is shown.
     */
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