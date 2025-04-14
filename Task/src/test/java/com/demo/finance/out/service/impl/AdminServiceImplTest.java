package com.demo.finance.out.service.impl;

import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.Role;
import com.demo.finance.exception.custom.OptimisticLockException;
import com.demo.finance.exception.custom.UserNotFoundException;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.TokenService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.DisplayName;

import static com.demo.finance.domain.utils.Role.ADMIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.demo.finance.domain.dto.UserDto;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenService tokenService;
    @InjectMocks
    private AdminServiceImpl adminService;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = Instancio.create(User.class);
        user.setUserId(1L);
        user.setRole(Role.USER);
        user.setVersion(1L);
        userDto = Instancio.create(UserDto.class);
        userDto.setUserId(1L);
        userDto.setVersion(1L);
    }

    @Test
    @DisplayName("Get user - existing user - returns user")
    void testGetUser_existingUser_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(user);

        User result = adminService.getUser(1L);

        assertThat(result).isEqualTo(user);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Update user role - existing user - updates successfully")
    void testUpdateUserRole_existingUser_updatesSuccessfully() {
        userDto.setRole("ADMIN");

        when(userRepository.findById(1L)).thenReturn(user);
        when(userRepository.update(user)).thenReturn(true);

        boolean result = adminService.updateUserRole(1L, userDto);

        assertThat(result).isTrue();
        assertThat(user.getRole()).isEqualTo(ADMIN);
        assertThat(user.getVersion()).isEqualTo(1L);
        verify(userRepository, times(1)).update(user);
        verify(userRepository, times(1)).findById(1L);
        verify(tokenService, times(1)).invalidateUserToken(user.getUserId());
    }

    @Test
    @DisplayName("Block or unblock user - existing user - updates successfully")
    void testBlockOrUnblockUser_existingUser_updatesSuccessfully() {
        userDto.setBlocked(true);

        when(userRepository.findById(1L)).thenReturn(user);
        when(userRepository.update(user)).thenReturn(true);

        boolean result = adminService.blockOrUnblockUser(1L, userDto);

        assertThat(result).isTrue();
        assertThat(user.isBlocked()).isTrue();
        assertThat(user.getVersion()).isEqualTo(1L);
        verify(userRepository, times(1)).update(user);
        verify(userRepository, times(1)).findById(1L);
        verify(tokenService, times(1)).invalidateUserToken(user.getUserId());
    }

    @Test
    @DisplayName("Delete user - existing user - deletes successfully")
    void testDeleteUser_existingUser_deletesSuccessfully() {
        when(userRepository.delete(1L)).thenReturn(true);

        boolean result = adminService.deleteUser(1L);

        assertThat(result).isTrue();
        verify(userRepository, times(1)).delete(1L);
        verify(tokenService, times(1)).invalidateUserToken(user.getUserId());
    }

    @Test
    @DisplayName("Update user role - non-existing user - throws UserNotFoundException")
    void testUpdateUserRole_nonExistingUser_throwsException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(null);

        assertThatThrownBy(() -> adminService.updateUserRole(userId, userDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User with ID " + userId + " not found");
        verify(userRepository, times(1)).findById(userId);
        verify(tokenService, never()).invalidateUserToken(user.getUserId());
    }

    @Test
    @DisplayName("Block or unblock user - non-existing user - throws UserNotFoundException")
    void testBlockOrUnblockUser_nonExistingUser_throwsException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(null);

        assertThatThrownBy(() -> adminService.blockOrUnblockUser(userId, new UserDto()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User with ID " + userId + " not found");
        verify(userRepository, times(1)).findById(userId);
        verify(tokenService, never()).invalidateUserToken(user.getUserId());
    }

    @Test
    @DisplayName("Delete user - non-existing user - returns false")
    void testDeleteUser_nonExistingUser_returnsFalse() {
        when(userRepository.delete(1L)).thenReturn(false);

        boolean result = adminService.deleteUser(1L);

        assertThat(result).isFalse();
        verify(userRepository, times(1)).delete(1L);
        verify(tokenService, never()).invalidateUserToken(user.getUserId());
    }

    @Test
    @DisplayName("Update user role - version mismatch - throws OptimisticLockException")
    void testUpdateUserRole_versionMismatch_throwsOptimisticLockException() {
        userDto.setRole("ADMIN");
        userDto.setVersion(1L);
        user.setVersion(2L);

        when(userRepository.findById(1L)).thenReturn(user);
        when(userRepository.update(user)).thenReturn(false);

        assertThatThrownBy(() -> adminService.updateUserRole(1L, userDto))
                .isInstanceOf(OptimisticLockException.class)
                .hasMessageContaining("User with ID 1 was modified. Check version number.");
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).update(user);
        verify(tokenService, never()).invalidateUserToken(user.getUserId());
    }

    @Test
    @DisplayName("Block or unblock user - version mismatch - throws OptimisticLockException")
    void testBlockOrUnblockUser_versionMismatch_throwsOptimisticLockException() {
        userDto.setBlocked(true);
        userDto.setVersion(1L);
        user.setVersion(2L);

        when(userRepository.findById(1L)).thenReturn(user);
        when(userRepository.update(user)).thenReturn(false);

        assertThatThrownBy(() -> adminService.blockOrUnblockUser(1L, userDto))
                .isInstanceOf(OptimisticLockException.class)
                .hasMessageContaining("User with ID 1 was modified. Check version number.");
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).update(user);
        verify(tokenService, never()).invalidateUserToken(user.getUserId());
    }
}