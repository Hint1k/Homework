package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock private AdminService adminService;
    @InjectMocks private AdminController adminController;

    @Test
    void testGetAllUsers() {
        List<User> mockUsers = Arrays.asList(
                new User(1L, "Alice", "alice@mail.com", "password123", false,
                        new Role("user")),
                new User(2L, "Bob", "bob@mail.com", "securepass", false, new
                        Role("admin"))
        );

        when(adminService.getAllUsers()).thenReturn(mockUsers);

        List<User> users = adminController.getAllUsers();

        assertThat(users).hasSize(2).containsExactlyElementsOf(mockUsers);
        verify(adminService, times(1)).getAllUsers();
    }

    @Test
    void testUpdateUserRole_Success() {
        Long userId = 1L;
        Role newRole = new Role("admin");

        when(adminService.updateUserRole(userId, newRole)).thenReturn(true);

        boolean updated = adminController.updateUserRole(userId, newRole);

        assertThat(updated).isTrue();
        verify(adminService, times(1)).updateUserRole(userId, newRole);
    }

    @Test
    void testBlockUser_Success() {
        Long userId = 1L;

        when(adminService.blockUser(userId)).thenReturn(true);

        boolean blocked = adminController.blockUser(userId);

        assertThat(blocked).isTrue();
        verify(adminService, times(1)).blockUser(userId);
    }

    @Test
    void testUnBlockUser_Success() {
        Long userId = 1L;

        when(adminService.unBlockUser(userId)).thenReturn(true);

        boolean unblocked = adminController.unBlockUser(userId);

        assertThat(unblocked).isTrue();
        verify(adminService, times(1)).unBlockUser(userId);
    }

    @Test
    void testDeleteUser_Success() {
        Long userId = 1L;

        when(adminService.deleteUser(userId)).thenReturn(true);

        boolean deleted = adminController.deleteUser(userId);

        assertThat(deleted).isTrue();
        verify(adminService, times(1)).deleteUser(userId);
    }
}