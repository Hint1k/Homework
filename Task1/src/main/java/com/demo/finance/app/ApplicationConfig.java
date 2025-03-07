package com.demo.finance.app;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.in.cli.CliHandler;
import com.demo.finance.in.controller.*;
import com.demo.finance.domain.usecase.*;
import com.demo.finance.out.repository.*;
import com.demo.finance.out.service.PasswordService;
import com.demo.finance.out.service.ReportService;

public class ApplicationConfig {

    private final UserRepository userRepository = new UserRepositoryImpl();
    private final TransactionRepository transactionRepository = new TransactionRepositoryImpl();
    private final BudgetRepository budgetRepository = new BudgetRepositoryImpl();
    private final GoalRepository goalRepository = new GoalRepositoryImpl();
    private final PasswordService passwordService = new PasswordService();

    private final RegistrationUseCase registrationUseCase = new RegistrationUseCase(userRepository, passwordService);
    private final UsersUseCase usersUseCase = new UsersUseCase(userRepository, passwordService);
    private final TransactionsUseCase transactionsUseCase =
            new TransactionsUseCase(transactionRepository);
    private final BudgetUseCase manageBudgetsUseCase = new BudgetUseCase(budgetRepository,transactionRepository);
    private final GoalsUseCase goalsUseCase = new GoalsUseCase(goalRepository, transactionRepository);
    private final ReportUseCase generateReportsUseCase = new ReportUseCase(transactionRepository);
    private final AdminUseCase adminUseCase = new AdminUseCase(userRepository, transactionRepository);
//    private final NotificationUseCase notificationUseCase = new NotificationUseCase(budgetRepository, goalRepository);

    //    private final NotificationService notificationService = new NotificationService(notificationUseCase);
    private final ReportService reportService = new ReportService(generateReportsUseCase);

    private final UserController userController = new UserController(registrationUseCase, usersUseCase);
    private final TransactionController transactionController = new TransactionController(transactionsUseCase);
    private final BudgetController budgetController = new BudgetController(manageBudgetsUseCase);
    private final GoalController goalController = new GoalController(goalsUseCase);
    private final ReportController reportController = new ReportController(reportService);
    private final AdminController adminController = new AdminController(adminUseCase);
//    private final NotificationController notificationController = new NotificationController(notificationService);

    public CliHandler getCliHandler() {
        return new CliHandler(
                userController, transactionController, budgetController,
                goalController, reportController, adminController
//               , notificationController
        );
    }

    public ApplicationConfig() {
        initializeDefaultAdminAccount();
    }

    private void initializeDefaultAdminAccount() {
        Long adminId = 1L;
        String adminEmail = "admin@demo.com";
        String hashedPassword = passwordService.hashPassword("123");
        Role role = new Role("admin");
        User admin = new User(adminId, "Default Admin", adminEmail, hashedPassword, false, role);
        userRepository.save(admin);
    }
}