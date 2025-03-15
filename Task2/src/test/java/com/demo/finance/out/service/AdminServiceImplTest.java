package com.demo.finance.out.service;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private AdminServiceImpl adminService;

    @Test
    void testGetAllUsers() {
        List<User> mockUsers = Arrays.asList(
                new User(1L, "Alice", "alice@mail.com", "password123", false,
                        new Role("user")),
                new User(2L, "Bob", "bob@mail.com", "securepass", false,
                        new Role("admin"))
        );

        when(userRepository.findAll()).thenReturn(mockUsers);

        List<User> users = adminService.getAllUsers();

        assertThat(users).hasSize(2).containsExactlyElementsOf(mockUsers);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testUpdateUserRole_Success() {
        Long userId = 1L;
        Role newRole = new Role("admin");
        User user = new User(userId, "Alice", "alice@mail.com", "password123", false,
                new Role("user"));

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(userRepository.update(user)).thenReturn(true);

        boolean updated = adminService.updateUserRole(userId, newRole);

        assertThat(updated).isTrue();
        assertThat(user.getRole()).isEqualTo(newRole);
        verify(userRepository, times(1)).findByUserId(userId);
        verify(userRepository, times(1)).update(user);
    }

    @Test
    void testBlockUser_Success() {
        Long userId = 1L;
        User user = new User(userId, "Alice", "alice@mail.com", "password123", false,
                new Role("user"));

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));

        boolean blocked = adminService.blockUser(userId);

        assertThat(blocked).isTrue();
        assertThat(user.isBlocked()).isTrue();
        verify(userRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testUnBlockUser_Success() {
        Long userId = 1L;
        User user = new User(userId, "Alice", "alice@mail.com", "password123", true,
                new Role("user"));

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));

        boolean unblocked = adminService.unBlockUser(userId);

        assertThat(unblocked).isTrue();
        assertThat(user.isBlocked()).isFalse();
        verify(userRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testDeleteUser_Success() {
        Long userId = 1L;

        when(userRepository.delete(userId)).thenReturn(true);

        boolean deleted = adminService.deleteUser(userId);

        assertThat(deleted).isTrue();
        verify(userRepository, times(1)).delete(userId);
    }

    @Test
    void testGetAllUsersWhenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> result = adminService.getAllUsers();

        assertThat(result).isEmpty();
    }

    @Test
    void testUpdateUserRoleWhenUserDoesNotExist() {
        Long userId = 1L;
        Role newRole = new Role("admin");

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        boolean result = adminService.updateUserRole(userId, newRole);

        assertThat(result).isFalse();
    }

    @Test
    void testBlockUserWhenUserDoesNotExist() {
        Long userId = 1L;

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        boolean result = adminService.blockUser(userId);

        assertThat(result).isFalse();
    }

    @Test
    void testUnBlockUserWhenUserDoesNotExist() {
        Long userId = 1L;

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        boolean result = adminService.unBlockUser(userId);

        assertThat(result).isFalse();
    }

    @Test
    void testDeleteUserWhenUserDoesNotExist() {
        Long userId = 3L;

        boolean result = adminService.deleteUser(userId);

        assertThat(result).isFalse();
    }
}