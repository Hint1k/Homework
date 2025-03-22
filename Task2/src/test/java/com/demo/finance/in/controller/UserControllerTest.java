package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.service.RegistrationService;
import com.demo.finance.out.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock private RegistrationService registrationService;
    @Mock private UserService userService;
    @InjectMocks private UserController userController;

    @Test
    @DisplayName("Register user - Success scenario")
    void testRegisterUser_Success() {
        Role role = new Role("user");
        when(registrationService.registerUser("Alice", "alice@mail.com", "password123", role))
                .thenReturn(true);

        boolean result = userController
                .registerUser("Alice", "alice@mail.com", "password123", role);

        assertThat(result).isTrue();
        verify(registrationService, times(1))
                .registerUser("Alice", "alice@mail.com", "password123", role);
    }

    @Test
    @DisplayName("Authenticate user - Success scenario")
    void testAuthenticateUser_Success() {
        User user = new User(1L, "Alice", "alice@mail.com", "password123", false,
                new Role("user"));
        when(registrationService.authenticate("alice@mail.com", "password123"))
                .thenReturn(Optional.of(user));

        Optional<User> result = userController.authenticateUser("alice@mail.com", "password123");

        assertThat(result).isPresent().contains(user);
        verify(registrationService, times(1))
                .authenticate("alice@mail.com", "password123");
    }

    @Test
    @DisplayName("Update own account - Success scenario")
    void testUpdateOwnAccount_Success() {
        Role role = new Role("user");
        when(userService.updateOwnAccount(1L, "Alice Updated", "alice_updated@mail.com",
                "newpassword123", role, true)).thenReturn(true);

        boolean result = userController.updateOwnAccount(1L, "Alice Updated",
                "alice_updated@mail.com", "newpassword123", role, true);

        assertThat(result).isTrue();
        verify(userService, times(1)).updateOwnAccount(1L, "Alice Updated",
                "alice_updated@mail.com", "newpassword123", role, true);
    }

    @Test
    @DisplayName("Delete own account - Success scenario")
    void testDeleteOwnAccount_Success() {
        when(userService.deleteOwnAccount(1L)).thenReturn(true);

        boolean result = userController.deleteOwnAccount(1L);

        assertThat(result).isTrue();
        verify(userService, times(1)).deleteOwnAccount(1L);
    }
}