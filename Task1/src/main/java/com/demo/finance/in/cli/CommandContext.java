package com.demo.finance.in.cli;

import com.demo.finance.domain.model.User;
import com.demo.finance.in.cli.command.*;
import com.demo.finance.in.controller.*;

import java.util.Scanner;

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

    public CommandContext(UserController userController, TransactionController transactionController,
                          BudgetController budgetController, GoalController goalController,
                          ReportController reportController, AdminController adminController,
                          NotificationController notificationController, Scanner scanner) {
        this.userController = userController;
        this.transactionController = transactionController;
        this.budgetController = budgetController;
        this.goalController = goalController;
        this.reportController = reportController;
        this.adminController = adminController;
        this.notificationController = notificationController;
        this.transactionCommand = new TransactionCommand(this, scanner);
        this.userCommand = new UserCommand(this, scanner);
        this.goalCommand = new GoalCommand(this, scanner);
        this.budgetCommand = new BudgetCommand(this, scanner);
        this.reportCommand = new ReportCommand(this, scanner);
        this.adminCommand = new AdminCommand(this, scanner);
        this.notificationCommand = new NotificationCommand(this, scanner);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public UserController getUserController() {
        return userController;
    }

    public TransactionController getTransactionController() {
        return transactionController;
    }

    public BudgetController getBudgetController() {
        return budgetController;
    }

    public GoalController getGoalController() {
        return goalController;
    }

    public ReportController getReportController() {
        return reportController;
    }

    public AdminController getAdminController() {
        return adminController;
    }

    public NotificationController getNotificationController() {
        return notificationController;
    }

    public TransactionCommand getTransactionCommand() {
        return transactionCommand;
    }

    public UserCommand getUserCommand() {
        return userCommand;
    }

    public GoalCommand getGoalCommand() {
        return goalCommand;
    }

    public BudgetCommand getBudgetCommand() {
        return budgetCommand;
    }

    public ReportCommand getReportCommand() {
        return reportCommand;
    }

    public AdminCommand getAdminCommand() {
        return adminCommand;
    }

    public NotificationCommand getNotificationCommand() {
        return notificationCommand;
    }
}