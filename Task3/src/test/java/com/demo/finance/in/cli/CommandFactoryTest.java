package com.demo.finance.in.cli;

import com.demo.finance.domain.model.User;
import com.demo.finance.in.cli.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Scanner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class CommandFactoryTest {

    @Mock private CommandContext context;
    @Mock private Scanner scanner;
    @Mock private UserCommand userCommand;
    @Mock private AdminCommand adminCommand;
    @Mock private TransactionCommand transactionCommand;
    @Mock private BudgetCommand budgetCommand;
    @Mock private GoalCommand goalCommand;
    @Mock private ReportCommand reportCommand;
    @Mock private NotificationCommand notificationCommand;
    @Mock private User currentUser;

    private CommandFactory commandFactory;

    @BeforeEach
    void setUp() {
        lenient().when(context.getUserCommand()).thenReturn(userCommand);
        lenient().when(context.getAdminCommand()).thenReturn(adminCommand);
        lenient().when(context.getTransactionCommand()).thenReturn(transactionCommand);
        lenient().when(context.getBudgetCommand()).thenReturn(budgetCommand);
        lenient().when(context.getGoalCommand()).thenReturn(goalCommand);
        lenient().when(context.getReportCommand()).thenReturn(reportCommand);
        lenient().when(context.getNotificationCommand()).thenReturn(notificationCommand);

        commandFactory = new CommandFactory(context, scanner);
    }

    @Test
    @DisplayName("Create command - No user, Register user")
    void testCreateCommand_NoUser_RegisterUser() {
        when(context.getCurrentUser()).thenReturn(null);

        Command command = commandFactory.createCommand("1");

        command.execute();
        verify(userCommand).registerUser();
    }

    @Test
    @DisplayName("Create command - No user, Login user")
    void testCreateCommand_NoUser_LoginUser() {
        when(context.getCurrentUser()).thenReturn(null);

        Command command = commandFactory.createCommand("2");

        command.execute();
        verify(userCommand).loginUser();
    }

    @Test
    @DisplayName("Create command - Admin, View all users")
    void testCreateCommand_Admin_ViewAllUsers() {
        when(context.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isAdmin()).thenReturn(true);

        Command command = commandFactory.createCommand("1");

        command.execute();
        verify(adminCommand).viewAllUsers();
    }

    @Test
    @DisplayName("Create command - Admin, Block user")
    void testCreateCommand_Admin_BlockUser() {
        when(context.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isAdmin()).thenReturn(true);

        Command command = commandFactory.createCommand("2");

        command.execute();
        verify(adminCommand).blockUser();
    }

    @Test
    @DisplayName("Create command - Admin, Unblock user")
    void testCreateCommand_Admin_UnblockUser() {
        when(context.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isAdmin()).thenReturn(true);

        Command command = commandFactory.createCommand("3");

        command.execute();
        verify(adminCommand).unblockUser();
    }

    @Test
    @DisplayName("Create command - Admin, Delete user")
    void testCreateCommand_Admin_DeleteUser() {
        when(context.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isAdmin()).thenReturn(true);

        Command command = commandFactory.createCommand("4");

        command.execute();
        verify(adminCommand).deleteUser();
    }

    @Test
    @DisplayName("Create command - Admin, Update user role")
    void testCreateCommand_Admin_UpdateUserRole() {
        when(context.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isAdmin()).thenReturn(true);

        Command command = commandFactory.createCommand("5");

        command.execute();
        verify(adminCommand).updateUserRole();
    }

    @Test
    @DisplayName("Create command - Admin, View transactions by user ID")
    void testCreateCommand_Admin_ViewTransactionsByUserId() {
        when(context.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isAdmin()).thenReturn(true);

        Command command = commandFactory.createCommand("6");

        command.execute();
        verify(adminCommand).viewTransactionsByUserId();
    }

    @Test
    @DisplayName("Create command - Admin, Logout user")
    void testCreateCommand_Admin_LogoutUser() {
        when(context.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isAdmin()).thenReturn(true);

        Command command = commandFactory.createCommand("7");

        command.execute();
        verify(userCommand).logoutUser();
    }

    @Test
    @DisplayName("Create command - User, Show transaction menu")
    void testCreateCommand_User_ShowTransactionMenu() {
        when(context.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isAdmin()).thenReturn(false);

        when(scanner.nextLine()).thenReturn("1", "0");

        Command command = commandFactory.createCommand("1");
        command.execute();

        verify(transactionCommand, times(1)).addTransaction();
    }

    @Test
    @DisplayName("Create command - User, Show budget menu")
    void testCreateCommand_User_ShowBudgetMenu() {
        when(context.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isAdmin()).thenReturn(false);
        when(scanner.nextLine()).thenReturn("1", "0");

        Command command = commandFactory.createCommand("2");

        command.execute();
        verify(budgetCommand).setBudget();
    }

    @Test
    @DisplayName("Create command - User, Show goal menu")
    void testCreateCommand_User_ShowGoalMenu() {
        when(context.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isAdmin()).thenReturn(false);
        when(scanner.nextLine()).thenReturn("1", "0");

        Command command = commandFactory.createCommand("3");

        command.execute();
        verify(goalCommand).createGoal();
    }

    @Test
    @DisplayName("Create command - User, Show report menu")
    void testCreateCommand_User_ShowReportMenu() {
        when(context.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isAdmin()).thenReturn(false);
        when(scanner.nextLine()).thenReturn("1", "0");

        Command command = commandFactory.createCommand("4");

        command.execute();
        verify(reportCommand).generateFullReport();
    }

    @Test
    @DisplayName("Create command - User, Show account menu")
    void testCreateCommand_User_ShowAccountMenu() {
        when(context.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isAdmin()).thenReturn(false);
        when(scanner.nextLine()).thenReturn("1", "0");

        Command command = commandFactory.createCommand("5");

        command.execute();
        verify(userCommand).showOwnDetails();
    }

    @Test
    @DisplayName("Create command - User, Logout user")
    void testCreateCommand_User_LogoutUser() {
        when(context.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isAdmin()).thenReturn(false);

        Command command = commandFactory.createCommand("6");

        command.execute();
        verify(userCommand).logoutUser();
    }

    @Test
    @DisplayName("Create command - Invalid choice, Logs error")
    void testCreateCommand_InvalidChoice_LogsError() {
        when(context.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isAdmin()).thenReturn(false);

        Command command = commandFactory.createCommand("invalid");

        command.execute();
        verifyNoInteractions(userCommand, adminCommand, transactionCommand, budgetCommand, goalCommand,
                reportCommand, notificationCommand);
    }
}