package com.demo.finance.out.service;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.PasswordUtils;
import com.demo.finance.out.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordUtils passwordUtils;
    @InjectMocks private UserServiceImpl userService;

    @Test
    void testUpdateOwnAccount_existingUser_updatesSuccessfully() {
        Long userId = 1L;
        Role role = new Role("USER");
        User updatedUser = new User(userId, "John Doe", "john@example.com", "hashedPassword",
                false, role);

        when(passwordUtils.hashPassword("newPassword")).thenReturn("hashedPassword");
        when(userRepository.update(updatedUser)).thenReturn(true);

        boolean result = userService.updateOwnAccount(userId, "John Doe", "john@example.com",
                "newPassword", role);

        assertThat(result).isTrue();
        verify(userRepository).update(updatedUser);
    }

    @Test
    void testDeleteOwnAccount_existingUser_deletesSuccessfully() {
        Long userId = 1L;
        when(userRepository.delete(userId)).thenReturn(true);

        boolean result = userService.deleteOwnAccount(userId);

        assertThat(result).isTrue();
        verify(userRepository).delete(userId);
    }

    @Test
    void testUpdateOwnAccount_nullPassword_hashesAndUpdatesSuccessfully() {
        Long userId = 1L;
        Role role = new Role("USER");
        User updatedUser = new User(userId, "John Doe", "john@example.com",
                null, false, role);

        when(passwordUtils.hashPassword(null)).thenReturn(null);
        when(userRepository.update(updatedUser)).thenReturn(true);

        boolean result = userService.updateOwnAccount(userId, "John Doe", "john@example.com",
                null, role);

        assertThat(result).isTrue();
        verify(userRepository).update(updatedUser);
    }

    @Test
    void testUpdateOwnAccount_updateFails_returnsFalse() {
        Long userId = 1L;
        Role role = new Role("USER");
        User updatedUser = new User(userId, "John Doe", "john@example.com",
                "hashedPassword", false, role);

        when(passwordUtils.hashPassword("newPassword")).thenReturn("hashedPassword");
        when(userRepository.update(updatedUser)).thenReturn(false);

        boolean result = userService.updateOwnAccount(userId, "John Doe", "john@example.com",
                "newPassword", role);

        assertThat(result).isFalse();
        verify(userRepository).update(updatedUser);
    }

    @Test
    void testDeleteOwnAccount_nonExistingUser_returnsFalse() {
        Long userId = 99L;
        when(userRepository.delete(userId)).thenReturn(false);

        boolean result = userService.deleteOwnAccount(userId);

        assertThat(result).isFalse();
        verify(userRepository).delete(userId);
    }
}