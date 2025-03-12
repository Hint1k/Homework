package com.demo.finance.app;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.*;
import com.demo.finance.in.cli.CliHandler;
import com.demo.finance.in.controller.*;
import com.demo.finance.out.repository.*;
import com.demo.finance.out.service.*;
import com.demo.finance.out.service.impl.*;

import java.io.InputStream;
import java.util.Properties;

/**
 * Configures and initializes the application by setting up repositories, services, controllers,
 * and loading the configuration for the default admin account.
 */
public class ApplicationConfig {

    private final BudgetRepository budgetRepository = new BudgetRepositoryImpl();
    private final GoalRepository goalRepository = new GoalRepositoryImpl();
    private final TransactionRepository transactionRepository = new TransactionRepositoryImpl();
    private final UserRepository userRepository = new UserRepositoryImpl();

    private final BalanceUtils balanceUtils = new BalanceUtilsImpl(transactionRepository);
    private final EmailService emailService = new EmailServiceImpl();
    private final PasswordUtils passwordUtils = new PasswordUtils();
    private final ValidationUtils validationUtils = new ValidationUtilsImpl();

    private final AdminService adminService = new AdminServiceImpl(userRepository);
    private final BudgetService budgetService = new BudgetServiceImpl(budgetRepository, transactionRepository);
    private final GoalService goalService = new GoalServiceImpl(goalRepository, balanceUtils);
    private final NotificationService notificationService = new NotificationServiceImpl(budgetRepository,
            goalRepository, transactionRepository, userRepository, balanceUtils, emailService);
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

    /**
     * Initializes and returns a CLI handler that connects all controllers
     * and validation utilities to handle user input.
     *
     * @return The configured {@link CliHandler} for handling user commands.
     */
    public CliHandler getCliHandler() {
        return new CliHandler(
                userController, transactionController, budgetController, goalController,
                reportController, adminController, notificationController, validationUtils
        );
    }

    private final Properties adminProperties = new Properties();

    /**
     * Initializes the application configuration by loading the admin properties
     * and setting up the default admin account.
     */
    public ApplicationConfig() {
        loadAdminProperties();
        initializeDefaultAdminAccount();
    }

    /**
     * Loads the admin properties from the "application.properties" file located in the resources folder.
     *
     * @throws RuntimeException if loading the properties fails.
     */
    private void loadAdminProperties() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Failed to find application.properties file.");
            }
            adminProperties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load application properties", e);
        }
    }

    /**
     * Initializes the default admin account using the loaded properties and saves it to the user repository.
     */
    private void initializeDefaultAdminAccount() {
        Long adminId = Long.parseLong(adminProperties.getProperty("admin.id"));
        String adminEmail = adminProperties.getProperty("admin.email");
        String adminName = adminProperties.getProperty("admin.name");
        String rawPassword = adminProperties.getProperty("admin.password");
        String roleName = adminProperties.getProperty("admin.role");

        String hashedPassword = passwordUtils.hashPassword(rawPassword);
        Role role = new Role(roleName);
        User admin = new User(adminId, adminName, adminEmail, hashedPassword, false, role);
        userRepository.save(admin);
    }
}