package com.demo.finance.out.service.impl;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.demo.finance.domain.dto.UserDto;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    @DisplayName("Get user - existing user - returns user")
    void testGetUser_existingUser_returnsUser() {
        User user = new User(1L, "Alice", "alice@mail.com", "password123",
                false, new Role("user"), 1L);
        when(userRepository.findById(1L)).thenReturn(user);

        User result = adminService.getUser(1L);

        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("Update user role - existing user - updates successfully")
    void testUpdateUserRole_existingUser_updatesSuccessfully() {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);
        userDto.setRole(new Role("admin"));

        User user = new User(1L, "Alice", "alice@mail.com", "password123",
                false, new Role("user"), 1L);
        when(userRepository.findById(1L)).thenReturn(user);
        when(userRepository.update(user)).thenReturn(true);

        boolean result = adminService.updateUserRole(1L, userDto);

        assertThat(result).isTrue();
        assertThat(user.getRole().getName()).isEqualTo("admin");
        assertThat(user.getVersion()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Block or unblock user - existing user - updates successfully")
    void testBlockOrUnblockUser_existingUser_updatesSuccessfully() {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);
        userDto.setBlocked(true);

        User user = new User(1L, "Alice", "alice@mail.com", "password123",
                false, new Role("user"), 1L);
        when(userRepository.findById(1L)).thenReturn(user);
        when(userRepository.update(user)).thenReturn(true);

        boolean result = adminService.blockOrUnblockUser(1L, userDto);

        assertThat(result).isTrue();
        assertThat(user.isBlocked()).isTrue();
        assertThat(user.getVersion()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Delete user - existing user - deletes successfully")
    void testDeleteUser_existingUser_deletesSuccessfully() {
        when(userRepository.delete(1L)).thenReturn(true);

        boolean result = adminService.deleteUser(1L);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Update user role - non-existing user - returns false")
    void testUpdateUserRole_nonExistingUser_returnsFalse() {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(null);

        boolean result = adminService.updateUserRole(1L, userDto);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Block or unblock user - non-existing user - returns false")
    void testBlockOrUnblockUser_nonExistingUser_returnsFalse() {
        when(userRepository.findById(1L)).thenReturn(null);

        boolean result = adminService.blockOrUnblockUser(1L, new UserDto());

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Delete user - non-existing user - returns false")
    void testDeleteUser_nonExistingUser_returnsFalse() {
        when(userRepository.delete(1L)).thenReturn(false);

        boolean result = adminService.deleteUser(1L);

        assertThat(result).isFalse();
    }
}