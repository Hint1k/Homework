package com.demo.finance.in.cli.command;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.MaxRetriesReachedException;
import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.in.controller.AdminController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Scanner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class AdminCommandTest {

    @Mock private CommandContext context;
    @Mock private ValidationUtils validationUtils;
    @Mock private AdminController adminController;
    @Mock private Scanner scanner;
    @InjectMocks private AdminCommand adminCommand;

    @BeforeEach
    void setUp() {
        lenient().when(context.getAdminController()).thenReturn(adminController);
    }

    @Test
    @DisplayName("View all users - Successful retrieval")
    void testViewAllUsers() {
        when(adminController.getAllUsers()).thenReturn(List.of(
                new User(2L, "John Doe", "john@example.com", "pass123", false,
                        new Role("USER"))));

        adminCommand.viewAllUsers();

        verify(adminController, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("Block user - Successful operation")
    void testBlockUser_Success() {
        when(validationUtils.promptForPositiveLong(any(), any())).thenReturn(2L);
        when(adminController.blockUser(2L)).thenReturn(true);

        adminCommand.blockUser();

        verify(adminController, times(1)).blockUser(2L);
    }

    @Test
    @DisplayName("Unblock user - Successful operation")
    void testUnblockUser_Success() {
        when(validationUtils.promptForPositiveLong(any(), any())).thenReturn(2L);
        when(adminController.unBlockUser(2L)).thenReturn(true);

        adminCommand.unblockUser();

        verify(adminController, times(1)).unBlockUser(2L);
    }

    @Test
    @DisplayName("Delete user - Successful operation")
    void testDeleteUser_Success() {
        when(validationUtils.promptForPositiveLong(any(), any())).thenReturn(2L);
        when(adminController.deleteUser(2L)).thenReturn(true);

        adminCommand.deleteUser();

        verify(adminController, times(1)).deleteUser(2L);
    }

    @Test
    @DisplayName("Update user role - Successful operation")
    void testUpdateUserRole_Success() {
        when(validationUtils.promptForPositiveLong(anyString(), any())).thenReturn(2L);
        when(validationUtils.promptForIntInRange(anyString(), anyInt(), anyInt(), any())).thenReturn(2);

        when(adminController.updateUserRole(eq(2L), any(Role.class))).thenReturn(true);

        adminCommand.updateUserRole();

        verify(adminController, times(1)).updateUserRole(eq(2L), any(Role.class));
    }

    @Test
    @DisplayName("View all users - No users available")
    void testViewAllUsers_NoUsers_ReturnsEmptyList() {
        when(adminController.getAllUsers()).thenReturn(List.of());

        adminCommand.viewAllUsers();

        verify(adminController, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("Block user - Invalid user ID input")
    void testBlockUser_InvalidUserId_LogsError() {
        when(validationUtils.promptForPositiveLong(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid user ID"));

        adminCommand.blockUser();

        verify(validationUtils).promptForPositiveLong(any(), any());
        verify(adminController, never()).blockUser(anyLong());
    }

    @Test
    @DisplayName("Block user - User not found")
    void testBlockUser_UserNotFound_LogsError() {
        when(validationUtils.promptForPositiveLong(any(), any())).thenReturn(2L);
        when(adminController.blockUser(2L)).thenReturn(false);

        adminCommand.blockUser();

        verify(adminController, times(1)).blockUser(2L);
    }

    @Test
    @DisplayName("Unblock user - Invalid user ID input")
    void testUnblockUser_InvalidUserId_LogsError() {
        when(validationUtils.promptForPositiveLong(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid user ID"));

        adminCommand.unblockUser();

        verify(validationUtils).promptForPositiveLong(any(), any());
        verify(adminController, never()).unBlockUser(anyLong());
    }

    @Test
    @DisplayName("Unblock user - User not found")
    void testUnblockUser_UserNotFound_LogsError() {
        when(validationUtils.promptForPositiveLong(any(), any())).thenReturn(2L);
        when(adminController.unBlockUser(2L)).thenReturn(false);

        adminCommand.unblockUser();

        verify(adminController, times(1)).unBlockUser(2L);
    }

    @Test
    @DisplayName("Delete user - Invalid user ID input")
    void testDeleteUser_InvalidUserId_LogsError() {
        when(validationUtils.promptForPositiveLong(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid user ID"));

        adminCommand.deleteUser();

        verify(validationUtils).promptForPositiveLong(any(), any());
        verify(adminController, never()).deleteUser(anyLong());
    }

    @Test
    @DisplayName("Delete user - User not found")
    void testDeleteUser_UserNotFound_LogsError() {
        when(validationUtils.promptForPositiveLong(any(), any())).thenReturn(2L);
        when(adminController.deleteUser(2L)).thenReturn(false);

        adminCommand.deleteUser();

        verify(adminController, times(1)).deleteUser(2L);
    }

    @Test
    @DisplayName("Update user role - Invalid user ID input")
    void testUpdateUserRole_InvalidUserId_LogsError() {
        when(validationUtils.promptForPositiveLong(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid user ID"));

        adminCommand.updateUserRole();

        verify(validationUtils).promptForPositiveLong(any(), any());
        verify(adminController, never()).updateUserRole(anyLong(), any());
    }

    @Test
    @DisplayName("Update user role - Invalid role input")
    void testUpdateUserRole_InvalidRole_LogsError() {
        when(validationUtils.promptForPositiveLong(any(), any())).thenReturn(2L);
        when(validationUtils.promptForIntInRange(any(), anyInt(), anyInt(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid role"));

        adminCommand.updateUserRole();

        verify(validationUtils).promptForIntInRange(any(), anyInt(), anyInt(), any());
        verify(adminController, never()).updateUserRole(anyLong(), any());
    }

    @Test
    @DisplayName("Update user role - User not found")
    void testUpdateUserRole_UserNotFound_LogsError() {
        when(validationUtils.promptForPositiveLong(any(), any())).thenReturn(2L);
        when(validationUtils.promptForIntInRange(any(), anyInt(), anyInt(), any())).thenReturn(1);
        when(adminController.updateUserRole(2L, new Role("user"))).thenReturn(false);

        adminCommand.updateUserRole();

        verify(adminController, times(1)).updateUserRole(2L, new Role("user"));
    }
}