package com.demo.finance.app.config;

import com.demo.finance.domain.utils.BalanceUtils;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.domain.utils.impl.BalanceUtilsImpl;
import com.demo.finance.domain.utils.impl.PasswordUtilsImpl;
import com.demo.finance.domain.utils.impl.ValidationUtilsImpl;
import com.demo.finance.in.controller.*;
import com.demo.finance.out.repository.*;
import com.demo.finance.out.repository.impl.*;
import com.demo.finance.out.service.*;
import com.demo.finance.out.service.impl.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

/**
 * The {@code AppConfig} class serves as the central configuration class for the application.
 * It initializes and wires all dependencies, including repositories, services, utilities, and servlets,
 * ensuring that the application components are properly instantiated and ready for use.
 */
@Setter
@Getter
public class AppConfig {

    private final DatabaseConfig databaseConfig;
    private final LiquibaseManager liquibaseManager;
    private final ObjectMapper objectMapper;

    private final BudgetRepository budgetRepository;
    private final GoalRepository goalRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    private final BalanceUtils balanceUtils;
    private final EmailService emailService;
    private final PasswordUtilsImpl passwordUtils;
    private final ValidationUtils validationUtils;

    private final AdminService adminService;
    private final BudgetService budgetService;
    private final GoalService goalService;
    private final NotificationService notificationService;
    private final RegistrationService registrationService;
    private final ReportService reportService;
    private final TransactionService transactionService;
    private final UserService userService;

    private final UserServlet userServlet;
    private final TransactionServlet transactionServlet;
    private final AdminServlet adminServlet;
    private final BudgetServlet budgetServlet;
    private final GoalServlet goalServlet;
    private final NotificationServlet notificationServlet;
    private final ReportServlet reportServlet;

    /**
     * Constructs a new instance of {@code AppConfig}, initializing all required components and dependencies.
     * This includes setting up database configurations, running migrations, and wiring repositories, services,
     * utilities, and servlets to ensure the application is fully configured and operational.
     */
    public AppConfig() {
        this.databaseConfig = DatabaseConfig.getInstance();
        this.liquibaseManager = new LiquibaseManager(databaseConfig);
        this.liquibaseManager.runMigrations();

        this.objectMapper = new ObjectMapper();

        this.budgetRepository = new BudgetRepositoryImpl();
        this.goalRepository = new GoalRepositoryImpl();
        this.transactionRepository = new TransactionRepositoryImpl();
        this.userRepository = new UserRepositoryImpl();

        this.balanceUtils = new BalanceUtilsImpl(transactionRepository);
        this.emailService = new EmailServiceImpl();
        this.passwordUtils = new PasswordUtilsImpl();
        this.validationUtils = new ValidationUtilsImpl();

        this.adminService = new AdminServiceImpl(userRepository);
        this.budgetService = new BudgetServiceImpl(budgetRepository, transactionRepository);
        this.goalService = new GoalServiceImpl(goalRepository);
        this.notificationService = new NotificationServiceImpl(budgetRepository,
                goalRepository, transactionRepository, userRepository, balanceUtils, emailService);
        this.registrationService = new RegistrationServiceImpl(userRepository, passwordUtils);
        this.reportService = new ReportServiceImpl(transactionRepository);
        this.transactionService = new TransactionServiceImpl(transactionRepository);
        this.userService = new UserServiceImpl(userRepository, passwordUtils);

        this.userServlet = new UserServlet(registrationService, userService, validationUtils, objectMapper);
        this.transactionServlet = new TransactionServlet(transactionService, objectMapper, validationUtils);
        this.adminServlet = new AdminServlet(adminService, userService, transactionService, objectMapper,
                validationUtils);
        this.budgetServlet = new BudgetServlet(budgetService, objectMapper, validationUtils);
        this.goalServlet = new GoalServlet(goalService, objectMapper, validationUtils);
        this.notificationServlet = new NotificationServlet(notificationService, objectMapper);
        this.reportServlet = new ReportServlet(reportService, objectMapper, validationUtils);
    }
}