package com.demo.finance.app;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.MockEmailUtils;
import com.demo.finance.in.cli.CliHandler;
import com.demo.finance.in.controller.*;
import com.demo.finance.out.repository.*;
import com.demo.finance.out.service.*;
import com.demo.finance.domain.utils.PasswordUtils;

public class ApplicationConfig {

    private final UserRepository userRepository = new UserRepositoryImpl();
    private final TransactionRepository transactionRepository = new TransactionRepositoryImpl();
    private final BudgetRepository budgetRepository = new BudgetRepositoryImpl();
    private final GoalRepository goalRepository = new GoalRepositoryImpl();
    private final PasswordUtils passwordUtils = new PasswordUtils();
    private final MockEmailUtils mockEmailUtils = new MockEmailUtils();

    private final RegistrationService registrationService = new RegistrationService(userRepository, passwordUtils);
    private final UserService userService = new UserService(userRepository, passwordUtils);
    private final TransactionService transactionService =
            new TransactionService(transactionRepository);
    private final BudgetService manageBudgetsUseCase = new BudgetService(budgetRepository, transactionRepository);
    private final GoalService goalService = new GoalService(goalRepository, transactionRepository);
    private final ReportService reportService = new ReportService(transactionRepository);
    private final AdminService adminService = new AdminService(userRepository);
    private final NotificationService notificationService =
            new NotificationService(budgetRepository, goalRepository, transactionRepository, userRepository, mockEmailUtils);

    private final UserController userController = new UserController(registrationService, userService);
    private final TransactionController transactionController = new TransactionController(transactionService);
    private final BudgetController budgetController = new BudgetController(manageBudgetsUseCase);
    private final GoalController goalController = new GoalController(goalService);
    private final ReportController reportController = new ReportController(reportService);
    private final AdminController adminController = new AdminController(adminService);
    private final NotificationController notificationController = new NotificationController(notificationService);

    public CliHandler getCliHandler() {
        return new CliHandler(
                userController, transactionController, budgetController, goalController,
                reportController, adminController, notificationController
        );
    }

    public ApplicationConfig() {
        initializeDefaultAdminAccount();
    }

    private void initializeDefaultAdminAccount() {
        Long adminId = 1L;
        String adminEmail = "admin@demo.com";
        String hashedPassword = passwordUtils.hashPassword("123");
        Role role = new Role("admin");
        User admin = new User(adminId, "Default Admin", adminEmail, hashedPassword, false, role);
        userRepository.save(admin);
    }
}