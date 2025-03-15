package com.demo.finance.in.cli.command;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.exception.MaxRetriesReachedException;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.in.controller.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class UserCommandTest {

    @Mock private CommandContext context;
    @Mock private ValidationUtils validationUtils;
    @Mock private UserController userController;
    @Mock private User currentUser;
    @InjectMocks private UserCommand userCommand;

    @BeforeEach
    void setUp() {
        lenient().when(context.getUserController()).thenReturn(userController);
        lenient().when(context.getCurrentUser()).thenReturn(currentUser);
        lenient().when(currentUser.getUserId()).thenReturn(2L);
    }

    @Test
    @DisplayName("Register user - Success")
    void testRegisterUser_Success() {
        when(validationUtils.promptForNonEmptyString(any(), any())).thenReturn("John Doe");
        when(validationUtils.promptForValidEmail(any(), any())).thenReturn("john@example.com");
        when(validationUtils.promptForValidPassword(any(), any())).thenReturn("securePass123");
        when(userController.registerUser("John Doe", "john@example.com", "securePass123",
                new Role("user"))).thenReturn(true);

        userCommand.registerUser();

        verify(userController, times(1))
                .registerUser("John Doe", "john@example.com", "securePass123",
                        new Role("user"));
    }

    @Test
    @DisplayName("Login user - Success")
    void testLoginUser_Success() {
        User mockUser = new User(2L, "John Doe", "john@example.com", "securePass123",
                false, new Role("user"));
        when(validationUtils.promptForValidEmail(any(), any())).thenReturn("john@example.com");
        when(validationUtils.promptForValidPassword(any(), any())).thenReturn("securePass123");
        when(userController.authenticateUser("john@example.com", "securePass123"))
                .thenReturn(Optional.of(mockUser));

        userCommand.loginUser();

        verify(userController, times(1))
                .authenticateUser("john@example.com", "securePass123");
        verify(context, times(1)).setCurrentUser(mockUser);
    }

    @Test
    @DisplayName("Logout user")
    void testLogoutUser() {
        userCommand.logoutUser();

        verify(context, times(1)).setCurrentUser(null);
    }

    @Test
    @DisplayName("Update own account - Success")
    void testUpdateOwnAccount_Success() {
        when(currentUser.getRole()).thenReturn(new Role("user"));

        when(validationUtils.promptForOptionalString(any(), any())).thenReturn("John Updated");
        when(validationUtils.promptForOptionalEmail(any(), any())).thenReturn("updated@example.com");
        when(validationUtils.promptForOptionalPassword(any(), any())).thenReturn("newSecurePass123");

        when(userController.updateOwnAccount(eq(2L), eq("John Updated"), eq("updated@example.com"),
                eq("newSecurePass123"), eq(new Role("user")), eq(true))).thenReturn(true);

        userCommand.updateOwnAccount();

        verify(userController, times(1)).updateOwnAccount(eq(2L), eq("John Updated"),
                eq("updated@example.com"), eq("newSecurePass123"), eq(new Role("user")),
                eq(true));
    }

    @Test
    @DisplayName("Delete own account - Success")
    void testDeleteOwnAccount_Success() {
        when(userController.deleteOwnAccount(2L)).thenReturn(true);

        userCommand.deleteOwnAccount();

        verify(userController, times(1)).deleteOwnAccount(2L);
        verify(context, times(1)).setCurrentUser(null);
    }

    @Test
    @DisplayName("Register user - Invalid email logs error")
    void testRegisterUser_InvalidEmail_LogsError() {
        when(validationUtils.promptForNonEmptyString(any(), any())).thenReturn("John Doe");
        when(validationUtils.promptForValidEmail(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid email"));

        userCommand.registerUser();

        verify(validationUtils).promptForValidEmail(any(), any());
        verify(userController, never()).registerUser(anyString(), anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Register user - Invalid password logs error")
    void testRegisterUser_InvalidPassword_LogsError() {
        when(validationUtils.promptForNonEmptyString(any(), any())).thenReturn("John Doe");
        when(validationUtils.promptForValidEmail(any(), any())).thenReturn("john@example.com");
        when(validationUtils.promptForValidPassword(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid password"));

        userCommand.registerUser();

        verify(validationUtils).promptForValidPassword(any(), any());
        verify(userController, never()).registerUser(anyString(), anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Login user - Invalid email logs error")
    void testLoginUser_InvalidEmail_LogsError() {
        when(validationUtils.promptForValidEmail(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid email"));

        userCommand.loginUser();

        verify(validationUtils).promptForValidEmail(any(), any());
        verify(userController, never()).authenticateUser(anyString(), anyString());
    }

    @Test
    @DisplayName("Login user - Invalid password logs error")
    void testLoginUser_InvalidPassword_LogsError() {
        when(validationUtils.promptForValidEmail(any(), any())).thenReturn("john@example.com");
        when(validationUtils.promptForValidPassword(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid password"));

        userCommand.loginUser();

        verify(validationUtils).promptForValidPassword(any(), any());
        verify(userController, never()).authenticateUser(anyString(), anyString());
    }

    @Test
    @DisplayName("Login user - User blocked logs error")
    void testLoginUser_UserBlocked_LogsError() {
        when(validationUtils.promptForValidEmail(any(), any())).thenReturn("john@example.com");
        when(validationUtils.promptForValidPassword(any(), any())).thenReturn("securePass123");
        when(userController.authenticateUser("john@example.com", "securePass123"))
                .thenReturn(Optional.of(new User(2L, "John Doe", "john@example.com",
                        "securePass123", true, new Role("user"))));

        userCommand.loginUser();

        verify(userController, times(1))
                .authenticateUser("john@example.com", "securePass123");
        verify(context, never()).setCurrentUser(any());
    }

    @Test
    @DisplayName("Delete own account - User not found logs error")
    void testDeleteOwnAccount_UserNotFound_LogsError() {
        when(userController.deleteOwnAccount(2L)).thenReturn(false);

        userCommand.deleteOwnAccount();

        verify(userController, times(1)).deleteOwnAccount(2L);
        verify(context, never()).setCurrentUser(null);
    }

    @Test
    @DisplayName("Update own account - Invalid email logs error")
    void testUpdateOwnAccount_InvalidEmail_LogsError() {
        when(currentUser.getRole()).thenReturn(new Role("user"));

        when(validationUtils.promptForOptionalString(any(), any())).thenReturn("John Updated");
        when(validationUtils.promptForOptionalEmail(any(), any()))
                .thenAnswer(invocation -> {
                    System.out.println("Simulating invalid input for email.");
                    return null; // Return null to simulate keeping the current value
                });

        when(validationUtils.promptForOptionalPassword(any(), any())).thenReturn("newSecurePass123");

        userCommand.updateOwnAccount();

        verify(validationUtils).promptForOptionalEmail(any(), any());
        verify(userController, never())
                .updateOwnAccount(anyLong(), anyString(), anyString(), anyString(), any(), anyBoolean());
    }

    @Test
    @DisplayName("Update own account - Invalid password logs error")
    void testUpdateOwnAccount_InvalidPassword_LogsError() {
        when(currentUser.getRole()).thenReturn(new Role("user"));

        when(validationUtils.promptForOptionalString(any(), any())).thenReturn("John Updated");
        when(validationUtils.promptForOptionalEmail(any(), any())).thenReturn("updated@example.com");

        when(validationUtils.promptForOptionalPassword(any(), any()))
                .thenAnswer(invocation -> {
                    System.out.println("Simulating invalid input for password.");
                    return null; // Return null to simulate keeping the current value
                });

        userCommand.updateOwnAccount();

        verify(validationUtils).promptForOptionalPassword(any(), any());
        verify(userController, never())
                .updateOwnAccount(anyLong(), anyString(), anyString(), anyString(), any(), anyBoolean());
    }
}