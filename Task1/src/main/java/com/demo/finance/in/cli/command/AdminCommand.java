package com.demo.finance.in.cli.command;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.model.Role;

import java.util.List;
import java.util.Scanner;

public class AdminCommand {

    private static final Long DEFAULT_ADMIN_ID = 1L;
    private final CommandContext context;
    private final ValidationUtils validationUtils;
    private final Scanner scanner;

    public AdminCommand(CommandContext context, ValidationUtils validationUtils, Scanner scanner) {
        this.context = context;
        this.validationUtils = validationUtils;
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
        long userId = validationUtils.promptForPositiveLong("Enter User ID to modify: ", scanner);
        int roleChoice = validationUtils
                .promptForIntInRange("Set role (User=1, Admin=2): ", 1, 2, scanner);
        Role newRole = (roleChoice == 2) ? new Role("admin") : new Role("user");

        if (context.getAdminController().updateUserRole(userId, newRole)) {
            System.out.println("User role updated successfully.");
        } else {
            System.out.println("Failed to update user role.");
        }
    }

    public void blockUser() {
        long userId = validationUtils.promptForPositiveLong("Enter User ID to block: ", scanner);
        if (userId == DEFAULT_ADMIN_ID) {
            System.out.println("Error: Default Admin cannot be blocked.");
            return;
        }
        if (context.getAdminController().blockUser(userId)) {
            System.out.println("User blocked successfully.");
        } else {
            System.out.println("Failed to block user. User may not exist.");
        }
    }

    public void unblockUser() {
        long userId = validationUtils.promptForPositiveLong("Enter User ID to unblock: ", scanner);
        if (context.getAdminController().unBlockUser(userId)) {
            System.out.println("User unblocked successfully.");
        } else {
            System.out.println("Failed to unblock user. User may not exist.");
        }
    }

    public void deleteUser() {
        long userId = validationUtils.promptForPositiveLong("Enter User ID to delete: ", scanner);
        if (userId == DEFAULT_ADMIN_ID) {
            System.out.println("Error: Default Admin cannot be deleted.");
            return;
        }
        if (context.getAdminController().deleteUser(userId)) {
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("Failed to delete user. User may not exist.");
        }
    }

    public void viewTransactionsByUserId() {
        long userId = validationUtils.promptForPositiveLong("Enter User ID to view transactions: ", scanner);
        List<Transaction> transactions = context.getTransactionController()
                .getTransactionsByUserId(userId);
        if (transactions.isEmpty()) {
            System.out.println("No transactions found for the user.");
        } else {
            transactions.forEach(System.out::println);
        }
    }
}