package com.demo.finance.in.cli;

public class Menu {

    public static void showMainMenu() {
        System.out.println("\n=== Personal Finance Tracker ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Manage Transactions");
        System.out.println("4. Manage Budget");
        System.out.println("5. Manage Goals");
        System.out.println("6. Generate Reports");
        System.out.println("7. Admin Panel");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }
}