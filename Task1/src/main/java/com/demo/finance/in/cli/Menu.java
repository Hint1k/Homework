package com.demo.finance.in.cli;

/**
 * The {@code Menu} class provides various static methods to display different menus in the console-based
 * Personal Finance Tracker application. These menus include options for users and admins to interact with
 * the system, such as managing transactions, budgets, goals, and generating reports.
 */
public class Menu {

    /**
     * Displays the main menu with options to register, login, or exit the application.
     */
    public static void showMainMenu() {
        System.out.println("\n=== Personal Finance Tracker ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    /**
     * Displays the user menu with options to manage transactions, budgets, goals, reports, and account details.
     */
    public static void showUserMenu() {
        System.out.println("\n=== User Menu ===");
        System.out.println("1. Manage Transactions");
        System.out.println("2. Manage Budget");
        System.out.println("3. Manage Goals");
        System.out.println("4. Generate Reports");
        System.out.println("5. Manage My Account");
        System.out.println("6. Logout");
        System.out.print("Enter your choice: ");
    }

    /**
     * Displays the admin menu with options to view users, block/unblock users, manage roles, and view transactions.
     */
    public static void showAdminMenu() {
        System.out.println("\n=== Admin Menu ===");
        System.out.println("1. View All Users");
        System.out.println("2. Block User");
        System.out.println("3. UnBlock User");
        System.out.println("4. Delete User");
        System.out.println("5. Manage User Roles");
        System.out.println("6. View Transactions By UserId ");
        System.out.println("7. Logout");
        System.out.print("Enter your choice: ");
    }

    /**
     * Displays the account menu with options to view, update, or delete user account details.
     */
    public static void showAccountMenu() {
        System.out.println("\n=== Account Menu ===");
        System.out.println("1. View User Account Details");
        System.out.println("2. Update User Account Details");
        System.out.println("3. Delete User Account");
        System.out.println("0. Back");
        System.out.print("Enter your choice: ");
    }

    /**
     * Displays the transaction menu with options to add, update, view, filter, or delete transactions.
     */
    public static void showTransactionMenu() {
        System.out.println("\n=== Transaction Management ===");
        System.out.println("1. Add Transaction");
        System.out.println("2. Update Transaction");
        System.out.println("3. View All Transactions");
        System.out.println("4. Filter Transactions");
        System.out.println("5. Delete Transaction");
        System.out.println("0. Back");
        System.out.print("Enter your choice: ");
    }

    /**
     * Displays the budget menu with options to set, view, or check notifications for the budget.
     */
    public static void showBudgetMenu() {
        System.out.println("\n=== Budget Management ===");
        System.out.println("1. Set Monthly Budget");
        System.out.println("2. View Budget");
        System.out.println("3. Check Budget Notification");
        System.out.println("0. Back");
        System.out.print("Enter your choice: ");
    }

    /**
     * Displays the goal menu with options to create, view, update, delete, or check notifications for goals.
     */
    public static void showGoalMenu() {
        System.out.println("\n=== Goal Management ===");
        System.out.println("1. Create Goal");
        System.out.println("2. View Goals");
        System.out.println("3. Update Goal");
        System.out.println("4. Delete Goal");
        System.out.println("5. Check Goal Notification");
        System.out.println("0. Back");
        System.out.print("Enter your choice: ");
    }

    /**
     * Displays the report menu with options to generate various financial reports such as full report,
     * report by date range, or expense analysis by category.
     */
    public static void showReportMenu() {
        System.out.println("\n=== Generate Reports ===");
        System.out.println("1. Full Financial Report");
        System.out.println("2. Report by Date Range");
        System.out.println("3. Expense Analysis by Category");
        System.out.println("0. Back");
        System.out.print("Enter your choice: ");
    }
}