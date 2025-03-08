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

    private final BudgetRepository budgetRepository = new BudgetRepositoryImpl();
    private final GoalRepository goalRepository = new GoalRepositoryImpl();
    private final TransactionRepository transactionRepository = new TransactionRepositoryImpl();
    private final UserRepository userRepository = new UserRepositoryImpl();

    private final MockEmailUtils mockEmailUtils = new MockEmailUtils();
    private final PasswordUtils passwordUtils = new PasswordUtils();

    private final AdminService adminService = new AdminServiceImpl(userRepository);
    private final BudgetService budgetService = new BudgetServiceImpl(budgetRepository, transactionRepository);
    private final GoalService goalService = new GoalServiceImpl(goalRepository, transactionRepository);
    private final NotificationService notificationService = new NotificationServiceImpl(budgetRepository,
            goalRepository, transactionRepository, userRepository, mockEmailUtils);
    private final RegistrationService registrationService = new RegistrationServiceImpl(userRepository, passwordUtils);
    private final ReportService reportService = new ReportServiceImpl(transactionRepository);
    private final TransactionService transactionService = new TransactionServiceImpl(transactionRepository);
    private final UserService userService = new UserServiceImpl(userRepository, passwordUtils);

    private final AdminController adminController = new AdminController(adminService);
    private final BudgetController budgetController = new BudgetController(budgetService);
    private final GoalController goalController = new GoalController(goalService);
    private final NotificationController notificationController = new NotificationController(notificationService);
    private final ReportController reportController = new ReportController(reportService);
    private final TransactionController transactionController = new TransactionController(transactionService);
    private final UserController userController = new UserController(registrationService, userService);

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