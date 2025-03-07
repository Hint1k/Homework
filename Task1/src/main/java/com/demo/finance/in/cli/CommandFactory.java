package com.demo.finance.in.cli;

import java.util.Scanner;

public class CommandFactory {
    private final CommandContext context;
    private final Scanner scanner;

    public CommandFactory(CommandContext context, Scanner scanner) {
        this.context = context;
        this.scanner = scanner;
    }

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

    private void showTransactionMenu() {
        while (true) {
            Menu.showTransactionMenu();
            String choice = scanner.nextLine().trim();
            if (choice.equals("0")) return;

            Command command = switch (choice) {
                case "1" -> context.getTransactionCommand()::addTransaction;
                case "2" -> context.getTransactionCommand()::viewTransactionsByUserId;
                case "3" -> context.getTransactionCommand()::filterTransactions;
                case "4" -> context.getTransactionCommand()::deleteTransaction;
                default -> () -> System.out.println("Invalid choice. Please try again.");
            };
            command.execute();
        }
    }

    private void showBudgetMenu() {
        while (true) {
            Menu.showBudgetMenu();
            String choice = scanner.nextLine().trim();
            if (choice.equals("0")) return;

            Command command = switch (choice) {
                case "1" -> context.getBudgetCommand()::setBudget;
                case "2" -> context.getBudgetCommand()::viewBudget;
                case "3" -> context.getBudgetCommand()::checkBudgetLimit;
                default -> () -> System.out.println("Invalid choice. Please try again.");
            };
            command.execute();
        }
    }

    private void showGoalMenu() {
        while (true) {
            Menu.showGoalMenu();
            String choice = scanner.nextLine().trim();
            if (choice.equals("0")) return;

            Command command = switch (choice) {
                case "1" -> context.getGoalCommand()::createGoal;
                case "2" -> context.getGoalCommand()::viewGoals;
                case "3" -> context.getGoalCommand()::updateGoalProgress;
                case "4" -> context.getGoalCommand()::deleteGoal;
                default -> () -> System.out.println("Invalid choice. Please try again.");
            };
            command.execute();
        }
    }

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

    private void showAccountMenu() {
        while (true) {
            if (context.getCurrentUser() == null) {
                return; // force users who updated or deleted their accounts back to main menu to re-login
            }
            Menu.showAccountMenu();
            String choice = scanner.nextLine().trim();
            if (choice.equals("0")) return;

            Command command = switch (choice) {
            case "1" -> context.getUserCommand()::showUserDetails;
            case "2" -> context.getUserCommand()::updateUser;
            case "3" -> context.getUserCommand()::deleteUser;
            default -> () -> System.out.println("Invalid choice. Please try again.");
            };
            command.execute();
        }
    }
}