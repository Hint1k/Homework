package com.demo.finance.in.cli;

import com.demo.finance.in.controller.*;
import java.util.Scanner;

public class CliHandler {

    private final UserController userController;
    private final TransactionController transactionController;
    private final BudgetController budgetController;
    private final GoalController goalController;
    private final ReportController reportController;
    private final AdminController adminController;
    private final NotificationController notificationController;
    private final Scanner scanner;

    public CliHandler(UserController userController, TransactionController transactionController,
                      BudgetController budgetController, GoalController goalController,
                      ReportController reportController, AdminController adminController,
                      NotificationController notificationController) {
        this.userController = userController;
        this.transactionController = transactionController;
        this.budgetController = budgetController;
        this.goalController = goalController;
        this.reportController = reportController;
        this.adminController = adminController;
        this.notificationController = notificationController;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            Menu.showMainMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> handleUserRegistration();
                case "2" -> handleUserLogin();
                case "3" -> handleTransactions();
                case "4" -> handleBudgets();
                case "5" -> handleGoals();
                case "6" -> handleReports();
                case "7" -> handleAdminPanel();
                case "0" -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleUserRegistration() {
        System.out.println("Enter User ID: ");
        String id = scanner.nextLine();
        System.out.println("Enter Name: ");
        String name = scanner.nextLine();
        System.out.println("Enter Email: ");
        String email = scanner.nextLine();
        System.out.println("Enter Password: ");
        String password = scanner.nextLine();
        if (userController.registerUser(id, name, email, password)) {
            System.out.println("Registration successful.");
        } else {
            System.out.println("Registration failed.");
        }
    }

    private void handleUserLogin() {
        System.out.println("Enter Email: ");
        String email = scanner.nextLine();
        System.out.println("Enter Password: ");
        String password = scanner.nextLine();
        if (userController.authenticateUser(email, password).isPresent()) {
            System.out.println("Login successful.");
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    private void handleTransactions() {
        System.out.println("Transactions menu not implemented yet.");
    }

    private void handleBudgets() {
        System.out.println("Budgets menu not implemented yet.");
    }

    private void handleGoals() {
        System.out.println("Goals menu not implemented yet.");
    }

    private void handleReports() {
        System.out.println("Reports menu not implemented yet.");
    }

    private void handleAdminPanel() {
        System.out.println("Admin panel not implemented yet.");
    }
}