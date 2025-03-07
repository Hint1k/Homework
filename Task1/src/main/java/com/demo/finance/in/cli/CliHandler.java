package com.demo.finance.in.cli;

import com.demo.finance.in.controller.*;

import java.util.Scanner;

public class CliHandler {

    private final CommandContext context;
    private final CommandFactory commandFactory;
    private final Scanner scanner;

    public CliHandler(UserController userController, TransactionController transactionController,
                      BudgetController budgetController, GoalController goalController,
                      ReportController reportController, AdminController adminController,
                      NotificationController notificationController) {
        this.scanner = new Scanner(System.in);
        this.context = new CommandContext(userController, transactionController, budgetController,
                goalController, reportController, adminController, notificationController, scanner);
        this.commandFactory = new CommandFactory(context, scanner);
    }

    public void start() {
        while (true) {
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
        }
    }
}