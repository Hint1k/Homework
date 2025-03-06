package com.demo.finance.in.cli.command;

import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.model.Role;

import java.util.List;
import java.util.Scanner;

public class AdminCommand {
    private final CommandContext context;
    private final Scanner scanner;

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

    public void viewAllTransactions() {
        List<Transaction> transactions = context.getAdminController().getAllTransactions();
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            transactions.forEach(System.out::println);
        }
    }

    public void deleteTransaction() {
        System.out.print("Enter Transaction ID to delete: ");
        Long transactionId = scanner.nextLong();
        if (context.getAdminController().deleteTransaction(transactionId)) {
            System.out.println("Transaction deleted successfully.");
        } else {
            System.out.println("Failed to delete transaction.");
        }
    }
}