package com.demo.finance.in.cli;

import java.util.Scanner;

/**
 * Factory class for creating command instances based on user input choice.
 * This class manages the creation of specific commands to be executed in the application.
 */
public class CommandFactory {
    private final CommandContext context;
    private final Scanner scanner;

    /**
     * Initializes the CommandFactory with the provided context and scanner.
     *
     * @param context The context that holds all controllers and commands.
     * @param scanner Scanner to capture user input for creating commands.
     */
    public CommandFactory(CommandContext context, Scanner scanner) {
        this.context = context;
        this.scanner = scanner;
    }

    /**
     * Creates a command based on the user choice.
     * The behavior of the command depends on the user's role (e.g., admin, regular user).
     *
     * @param choice The user input representing a menu option.
     * @return The appropriate command to execute.
     */
    public Command createCommand(String choice) {
        if (context.getCurrentUser() == null) {
            return switch (choice) {
                case "1" -> context.getUserCommand()::registerUser;
                case "2" -> context.getUserCommand()::loginUser;
                case "0" -> () -> System.exit(0);
                default -> () -> System.out.println("Invalid choice. Please try again.");
            };
        } else if (context.getCurrentUser().isAdmin()) {
            return switch (choice) {
                case "1" -> context.getAdminCommand()::viewAllUsers;
                case "2" -> context.getAdminCommand()::blockUser;
                case "3" -> context.getAdminCommand()::unblockUser;
                case "4" -> context.getAdminCommand()::deleteUser;
                case "5" -> context.getAdminCommand()::updateUserRole;
                case "6" -> context.getAdminCommand()::viewTransactionsByUserId;
                case "7" -> context.getUserCommand()::logoutUser;
                default -> () -> System.out.println("Invalid choice. Please try again.");
            };
        } else {
            return switch (choice) {
                case "1" -> this::showTransactionMenu;
                case "2" -> this::showBudgetMenu;
                case "3" -> this::showGoalMenu;
                case "4" -> this::showReportMenu;
                case "5" -> this::showAccountMenu;
                case "6" -> context.getUserCommand()::logoutUser;
                default -> () -> System.out.println("Invalid choice. Please try again.");
            };
        }
    }

    /**
     * Displays the transaction menu and processes user input for transaction-related actions.
     * The menu allows users to add, update, view, filter, or delete transactions.
     */
    private void showTransactionMenu() {
        while (true) {
            Menu.showTransactionMenu();
            String choice = scanner.nextLine().trim();
            if (choice.equals("0")) return;

            Command command = switch (choice) {
                case "1" -> context.getTransactionCommand()::addTransaction;
                case "2" -> context.getTransactionCommand()::updateTransaction;
                case "3" -> context.getTransactionCommand()::viewTransactionsByUserId;
                case "4" -> context.getTransactionCommand()::filterTransactions;
                case "5" -> context.getTransactionCommand()::deleteTransaction;
                default -> () -> System.out.println("Invalid choice. Please try again.");
            };
            command.execute();
        }
    }

    /**
     * Displays the budget menu and processes user input for budget-related actions.
     * The menu allows users to set a budget, view their current budget, or check budget notifications.
     */
    private void showBudgetMenu() {
        while (true) {
            Menu.showBudgetMenu();
            String choice = scanner.nextLine().trim();
            if (choice.equals("0")) return;

            Command command = switch (choice) {
                case "1" -> context.getBudgetCommand()::setBudget;
                case "2" -> context.getBudgetCommand()::viewBudget;
                case "3" -> context.getNotificationCommand()::checkBudgetNotification;
                default -> () -> System.out.println("Invalid choice. Please try again.");
            };
            command.execute();
        }
    }

    /**
     * Displays the goal menu and processes user input for goal-related actions.
     * The menu allows users to create, view, update, delete goals, or check goal notifications.
     */
    private void showGoalMenu() {
        while (true) {
            Menu.showGoalMenu();
            String choice = scanner.nextLine().trim();
            if (choice.equals("0")) return;

            Command command = switch (choice) {
                case "1" -> context.getGoalCommand()::createGoal;
                case "2" -> context.getGoalCommand()::viewGoals;
                case "3" -> context.getGoalCommand()::updateGoal;
                case "4" -> context.getGoalCommand()::deleteGoal;
                case "5" -> context.getNotificationCommand()::checkGoalNotification;
                default -> () -> System.out.println("Invalid choice. Please try again.");
            };
            command.execute();
        }
    }

    /**
     * Displays the report menu and processes user input for report-related actions.
     * The menu allows users to generate full reports, reports by date, or analyze expenses by category.
     */
    private void showReportMenu() {
        while (true) {
            Menu.showReportMenu();
            String choice = scanner.nextLine().trim();
            if (choice.equals("0")) return;

            Command command = switch (choice) {
                case "1" -> context.getReportCommand()::generateFullReport;
                case "2" -> context.getReportCommand()::generateReportByDate;
                case "3" -> context.getReportCommand()::analyzeExpensesByCategory;
                default -> () -> System.out.println("Invalid choice. Please try again.");
            };
            command.execute();
        }
    }

    /**
     * Displays the account menu and processes user input for account-related actions.
     * The menu allows users to view their details, update their account, or delete their account.
     */
    private void showAccountMenu() {
        while (true) {
            if (context.getCurrentUser() == null) {
                return; // force users who updated or deleted their accounts back to main menu to re-login
            }
            Menu.showAccountMenu();
            String choice = scanner.nextLine().trim();
            if (choice.equals("0")) return;

            Command command = switch (choice) {
                case "1" -> context.getUserCommand()::showOwnDetails;
                case "2" -> context.getUserCommand()::updateOwnAccount;
                case "3" -> context.getUserCommand()::deleteOwnAccount;
                default -> () -> System.out.println("Invalid choice. Please try again.");
            };
            command.execute();
        }
    }
}