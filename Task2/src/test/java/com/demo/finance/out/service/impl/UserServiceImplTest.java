package com.demo.finance.out.service.impl;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.PasswordUtils;
import com.demo.finance.out.repository.UserRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordUtils passwordUtils;
    @InjectMocks private UserServiceImpl userService;

    @Test
    @DisplayName("Update own account - existing user - updates successfully")
    void testUpdateOwnAccount_existingUser_updatesSuccessfully() {
        Long userId = 1L;
        Role role = new Role("USER");
        String name = "John Doe";
        String email = "john@example.com";
        String newPassword = "newPassword";
        String hashedPassword = "hashedPassword";

        User existingUser = new User(userId, "Old Name", "old@example.com", "oldHashedPassword",
                false, role);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordUtils.hashPassword(newPassword)).thenReturn(hashedPassword);
        User updatedUser = new User(userId, name, email, hashedPassword, false, role);
        when(userRepository.update(updatedUser)).thenReturn(true);

        boolean result = userService.updateOwnAccount(userId, name, email, newPassword, role, true);

        assertThat(result).isTrue();
        verify(userRepository, times(1)).findById(userId);
        verify(passwordUtils, times(1)).hashPassword(newPassword);
        verify(userRepository, times(1)).update(updatedUser);
    }

    @Test
    @DisplayName("Delete own account - existing user - deletes successfully")
    void testDeleteOwnAccount_existingUser_deletesSuccessfully() {
        Long userId = 1L;
        when(userRepository.delete(userId)).thenReturn(true);

        boolean result = userService.deleteOwnAccount(userId);

        assertThat(result).isTrue();
        verify(userRepository).delete(userId);
    }

    @Test
    @DisplayName("Update own account - password not provided - updates successfully")
    void testUpdateOwnAccount_nullPassword_UpdatesSuccessfully() {
        Long userId = 1L;
        String name = "John Doe";
        String email = "john@example.com";
        Role role = new Role("user");
        String existingHashedPassword = "existingHashedPassword";

        User existingUser = new User(userId, "Old Name", "old@example.com", existingHashedPassword,
                false, role);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        User updatedUser = new User(userId, name, email, existingHashedPassword, false, role);
        when(userRepository.update(updatedUser)).thenReturn(true);

        boolean result = userService.updateOwnAccount(userId, name, email, null, role, false);

        assertThat(result).isTrue();
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).update(updatedUser);
        verify(passwordUtils, never()).hashPassword(any());
    }

    @Test
    @DisplayName("Update own account - update fails - returns false")
    void testUpdateOwnAccount_updateFails_returnsFalse() {
        Long userId = 1L;
        Role role = new Role("USER");
        String name = "John Doe";
        String email = "john@example.com";
        String newPassword = "newPassword";
        String hashedPassword = "hashedPassword";

        User existingUser = new User(userId, "Old Name", "old@example.com", "oldHashedPassword",
                false, role);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordUtils.hashPassword(newPassword)).thenReturn(hashedPassword);
        User updatedUser = new User(userId, name, email, hashedPassword, false, role);
        when(userRepository.update(updatedUser)).thenReturn(false);

        boolean result = userService.updateOwnAccount(userId, name, email, newPassword, role, true);

        assertThat(result).isFalse();
        verify(userRepository, times(1)).findById(userId);
        verify(passwordUtils, times(1)).hashPassword(newPassword);
        verify(userRepository, times(1)).update(updatedUser);
    }

    @Test
    @DisplayName("Delete own account - non-existing user - returns false")
    void testDeleteOwnAccount_nonExistingUser_returnsFalse() {
        Long userId = 99L;
        when(userRepository.delete(userId)).thenReturn(false);

        boolean result = userService.deleteOwnAccount(userId);

        assertThat(result).isFalse();
        verify(userRepository).delete(userId);
    }
}