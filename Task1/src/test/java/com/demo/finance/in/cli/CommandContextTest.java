package com.demo.finance.in.cli;

import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.command.*;
import com.demo.finance.in.controller.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CommandContextTest {

    @Mock private UserController userController;
    @Mock private TransactionController transactionController;
    @Mock private BudgetController budgetController;
    @Mock private GoalController goalController;
    @Mock private ReportController reportController;
    @Mock private AdminController adminController;
    @Mock private NotificationController notificationController;
    @Mock private ValidationUtils validationUtils;
    @Mock private Scanner scanner;
    @Mock private User currentUser;

    private CommandContext commandContext;

    @BeforeEach
    void setUp() {
        commandContext = new CommandContext(
                userController, transactionController, budgetController,
                goalController, reportController, adminController,
                notificationController, validationUtils, scanner
        );
    }

    @Test
    @DisplayName("Get current user - Returns current user")
    void testGetCurrentUser_ReturnsCurrentUser() {
        commandContext.setCurrentUser(currentUser);

        User result = commandContext.getCurrentUser();

        assertThat(result).isEqualTo(currentUser);
    }

    @Test
    @DisplayName("Set current user - Sets current user")
    void testSetCurrentUser_SetsCurrentUser() {
        commandContext.setCurrentUser(currentUser);

        assertThat(commandContext.getCurrentUser()).isEqualTo(currentUser);
    }

    @Test
    @DisplayName("Get user controller - Returns user controller")
    void testGetUserController_ReturnsUserController() {
        UserController result = commandContext.getUserController();

        assertThat(result).isEqualTo(userController);
    }

    @Test
    @DisplayName("Get transaction controller - Returns transaction controller")
    void testGetTransactionController_ReturnsTransactionController() {
        TransactionController result = commandContext.getTransactionController();

        assertThat(result).isEqualTo(transactionController);
    }

    @Test
    @DisplayName("Get budget controller - Returns budget controller")
    void testGetBudgetController_ReturnsBudgetController() {
        BudgetController result = commandContext.getBudgetController();

        assertThat(result).isEqualTo(budgetController);
    }

    @Test
    @DisplayName("Get goal controller - Returns goal controller")
    void testGetGoalController_ReturnsGoalController() {
        GoalController result = commandContext.getGoalController();

        assertThat(result).isEqualTo(goalController);
    }

    @Test
    @DisplayName("Get report controller - Returns report controller")
    void testGetReportController_ReturnsReportController() {
        ReportController result = commandContext.getReportController();

        assertThat(result).isEqualTo(reportController);
    }

    @Test
    @DisplayName("Get admin controller - Returns admin controller")
    void testGetAdminController_ReturnsAdminController() {
        AdminController result = commandContext.getAdminController();

        assertThat(result).isEqualTo(adminController);
    }

    @Test
    @DisplayName("Get notification controller - Returns notification controller")
    void testGetNotificationController_ReturnsNotificationController() {
        NotificationController result = commandContext.getNotificationController();

        assertThat(result).isEqualTo(notificationController);
    }

    @Test
    @DisplayName("Get transaction command - Returns transaction command")
    void testGetTransactionCommand_ReturnsTransactionCommand() {
        TransactionCommand result = commandContext.getTransactionCommand();

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Get user command - Returns user command")
    void testGetUserCommand_ReturnsUserCommand() {
        UserCommand result = commandContext.getUserCommand();

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Get goal command - Returns goal command")
    void testGetGoalCommand_ReturnsGoalCommand() {
        GoalCommand result = commandContext.getGoalCommand();

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Get budget command - Returns budget command")
    void testGetBudgetCommand_ReturnsBudgetCommand() {
        BudgetCommand result = commandContext.getBudgetCommand();

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Get report command - Returns report command")
    void testGetReportCommand_ReturnsReportCommand() {
        ReportCommand result = commandContext.getReportCommand();

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Get admin command - Returns admin command")
    void testGetAdminCommand_ReturnsAdminCommand() {
        AdminCommand result = commandContext.getAdminCommand();

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Get notification command - Returns notification command")
    void testGetNotificationCommand_ReturnsNotificationCommand() {
        NotificationCommand result = commandContext.getNotificationCommand();

        assertThat(result).isNotNull();
    }
}