package com.demo.finance.in.cli.command;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.model.Role;

import java.util.List;
import java.util.Scanner;

public class AdminCommand {
    private final CommandContext context;
    private final Scanner scanner;
    private static final Long DEFAULT_ADMIN_ID = 1L;

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
        long userId = promptForPositiveLong("Enter User ID to modify: ");
        int roleChoice = promptForIntInRange();
        Role newRole = (roleChoice == 2) ? new Role("admin") : new Role("user");

        if (context.getAdminController().updateUserRole(userId, newRole)) {
            System.out.println("User role updated successfully.");
        } else {
            System.out.println("Failed to update user role.");
        }
    }

    public void blockUser() {
        long userId = promptForPositiveLong("Enter User ID to block: ");
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
        long userId = promptForPositiveLong("Enter User ID to unblock: ");
        if (context.getAdminController().unBlockUser(userId)) {
            System.out.println("User unblocked successfully.");
        } else {
            System.out.println("Failed to unblock user. User may not exist.");
        }
    }

    public void deleteUser() {
        long userId = promptForPositiveLong("Enter User ID to delete: ");
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
        long userId = promptForPositiveLong("Enter User ID to view transactions: ");
        List<Transaction> transactions = context.getTransactionController()
                .getTransactionsByUserId(userId);
        if (transactions.isEmpty()) {
            System.out.println("No transactions found for the user.");
        } else {
            transactions.forEach(System.out::println);
        }
    }

    private long promptForPositiveLong(String message) {
        while (true) {
            try {
                System.out.print(message);
                long value = Long.parseLong(scanner.nextLine().trim());
                if (value > 0) return value;
                System.out.println("Error: Value must be a positive number.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }

    private int promptForIntInRange() {
        while (true) {
            try {
                System.out.print("Set role (1 = User, 2 = Admin): ");
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= 1 && value <= 2) return value;
                System.out.println("Error: Please enter a number between " + 1 + " and " + 2 + ".");
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }
}