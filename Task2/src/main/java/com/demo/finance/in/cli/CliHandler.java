package com.demo.finance.in.cli;

import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.controller.*;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Handles the interaction between the user and the Command Line Interface (CLI).
 * It manages the flow of the application by displaying menus and processing user inputs.
 * Commands are created and executed based on user input.
 */
public class CliHandler {

    private final CommandContext context;
    private final CommandFactory commandFactory;
    private final Scanner scanner;

    /**
     * Initializes the CLI handler with the necessary controllers and validation utilities.
     *
     * @param userController          Controller for user-related actions.
     * @param transactionController   Controller for transaction-related actions.
     * @param budgetController        Controller for budget-related actions.
     * @param goalController          Controller for goal-related actions.
     * @param reportController        Controller for report-related actions.
     * @param adminController         Controller for admin-related actions.
     * @param notificationController Controller for notification-related actions.
     * @param validationUtils        Utility for validation during input prompts.
     */
    public CliHandler(UserController userController, TransactionController transactionController,
                      BudgetController budgetController, GoalController goalController,
                      ReportController reportController, AdminController adminController,
                      NotificationController notificationController, ValidationUtils validationUtils) {
        this.scanner = new Scanner(System.in);
        this.context = new CommandContext(userController, transactionController, budgetController,
                goalController, reportController, adminController, notificationController, validationUtils, scanner);
        this.commandFactory = new CommandFactory(context, scanner);
    }

    /**
     * Starts the CLI by continuously displaying the appropriate menu and executing commands based on user input.
     * The method loops infinitely until the application is manually exited.
     */
    public void start() {
        while (true) { // Manual exit command
            try {
                if (context.getCurrentUser() == null) {
                    Menu.showMainMenu();
                    String choice = scanner.nextLine().trim();
                    Command command = commandFactory.createCommand(choice);
                    command.execute();
                } else if (context.getCurrentUser().isAdmin()) {
                    Menu.showAdminMenu();
                    String choice = scanner.nextLine().trim();
                    Command command = commandFactory.createCommand(choice);
                    command.execute();
                } else {
                    Menu.showUserMenu();
                    String choice = scanner.nextLine().trim();
                    Command command = commandFactory.createCommand(choice);
                    command.execute();
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }
}