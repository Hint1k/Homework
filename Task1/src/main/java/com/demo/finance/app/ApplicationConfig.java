package com.demo.finance.app;

import com.demo.finance.in.cli.CliHandler;
import com.demo.finance.in.controller.*;
import com.demo.finance.domain.usecase.*;
import com.demo.finance.out.repository.*;
import com.demo.finance.out.service.PasswordService;
import com.demo.finance.out.service.ReportService;
import com.demo.finance.out.service.NotificationService;

public class ApplicationConfig {

    private final UserRepository userRepository = new UserRepositoryImpl();
    private final TransactionRepository transactionRepository = new TransactionRepositoryImpl();
    private final BudgetRepository budgetRepository = new BudgetRepositoryImpl();
    private final GoalRepository goalRepository = new GoalRepositoryImpl();
    private final PasswordService passwordService = new PasswordService();

    private final RegisterUserUseCase registerUserUseCase = new RegisterUserUseCase(userRepository, passwordService);
    private final ManageUsersUseCase manageUsersUseCase = new ManageUsersUseCase(userRepository, passwordService);
    private final ManageTransactionsUseCase manageTransactionsUseCase =
            new ManageTransactionsUseCase(transactionRepository);
    private final ManageBudgetUseCase manageBudgetsUseCase = new ManageBudgetUseCase(budgetRepository);
    private final ManageGoalsUseCase manageGoalsUseCase = new ManageGoalsUseCase(goalRepository);
    private final GenerateReportUseCase generateReportsUseCase = new GenerateReportUseCase(transactionRepository);
    private final AdminUseCase adminUseCase = new AdminUseCase(userRepository, transactionRepository);
    private final NotificationUseCase notificationUseCase = new NotificationUseCase(budgetRepository, goalRepository);

    private final NotificationService notificationService = new NotificationService(notificationUseCase);
    private final ReportService reportService = new ReportService(generateReportsUseCase);

    private final UserController userController = new UserController(registerUserUseCase, manageUsersUseCase);
    private final TransactionController transactionController = new TransactionController(manageTransactionsUseCase);
    private final BudgetController budgetController = new BudgetController(manageBudgetsUseCase);
    private final GoalController goalController = new GoalController(manageGoalsUseCase);
    private final ReportController reportController = new ReportController(reportService);
    private final AdminController adminController = new AdminController(adminUseCase);
    private final NotificationController notificationController = new NotificationController(notificationService);

    public CliHandler getCliHandler() {
        return new CliHandler(
                userController, transactionController, budgetController,
                goalController, reportController, adminController, notificationController
        );
    }
}