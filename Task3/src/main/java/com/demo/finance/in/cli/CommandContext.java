package com.demo.finance.in.cli;

import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.command.*;
import com.demo.finance.in.controller.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Scanner;

/**
 * Context that holds all the necessary controllers and commands for the CLI.
 * It serves as a container for various controller and command objects, and provides
 * access to them throughout the execution of the application.
 */
@Getter
@Setter
public class CommandContext {

    private User currentUser;
    private final UserController userController;
    private final TransactionController transactionController;
    private final BudgetController budgetController;
    private final GoalController goalController;
    private final ReportController reportController;
    private final AdminController adminController;
    private final NotificationController notificationController;
    private final TransactionCommand transactionCommand;
    private final UserCommand userCommand;
    private final GoalCommand goalCommand;
    private final BudgetCommand budgetCommand;
    private final ReportCommand reportCommand;
    private final AdminCommand adminCommand;
    private final NotificationCommand notificationCommand;
    private final Scanner scanner;

    /**
     * Initializes the CommandContext with the necessary controllers and commands.
     * Each command is associated with the corresponding controller, validation utility, and scanner.
     *
     * @param userController           Controller for user-related actions.
     * @param transactionController    Controller for transaction-related actions.
     * @param budgetController         Controller for budget-related actions.
     * @param goalController           Controller for goal-related actions.
     * @param reportController         Controller for report-related actions.
     * @param adminController          Controller for admin-related actions.
     * @param notificationController  Controller for notification-related actions.
     * @param validationUtils         Utility for validation during input prompts.
     * @param scanner                  Scanner to capture user input.
     */
    public CommandContext(UserController userController, TransactionController transactionController,
                          BudgetController budgetController, GoalController goalController,
                          ReportController reportController, AdminController adminController,
                          NotificationController notificationController, ValidationUtils validationUtils,
                          Scanner scanner) {
        this.userController = userController;
        this.transactionController = transactionController;
        this.budgetController = budgetController;
        this.goalController = goalController;
        this.reportController = reportController;
        this.adminController = adminController;
        this.notificationController = notificationController;
        this.scanner = scanner;
        this.transactionCommand = new TransactionCommand(this, validationUtils, scanner);
        this.userCommand = new UserCommand(this, validationUtils, scanner);
        this.goalCommand = new GoalCommand(this, validationUtils, scanner);
        this.budgetCommand = new BudgetCommand(this, validationUtils, scanner);
        this.reportCommand = new ReportCommand(this, validationUtils, scanner);
        this.adminCommand = new AdminCommand(this, validationUtils, scanner);
        this.notificationCommand = new NotificationCommand(this);
    }
}