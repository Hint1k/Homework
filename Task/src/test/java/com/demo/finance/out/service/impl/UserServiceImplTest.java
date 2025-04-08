package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.utils.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.impl.PasswordUtilsImpl;
import com.demo.finance.exception.custom.OptimisticLockException;
import com.demo.finance.out.repository.UserRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordUtilsImpl passwordUtils;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = Instancio.create(User.class);
        user.setEmail("john@example.com");
        user.setPassword("hashedPassword");
        user.setName("John Doe");
        userDto = Instancio.create(UserDto.class);
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");
        userDto.setPassword("newPassword");
    }

    @Test
    @DisplayName("Update own account - existing user - updates successfully")
    void testUpdateOwnAccount_existingUser_updatesSuccessfully() {
        user.setRole(Role.USER);
        user.setVersion(3L);
        userDto.setVersion(3L);

        when(userRepository.findById(1L)).thenReturn(user);
        when(passwordUtils.hashPassword("newPassword")).thenReturn("hashedPassword");
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.update(any(User.class))).thenReturn(true);

        boolean result = userService.updateOwnAccount(userDto, 1L);

        assertThat(result).isTrue();
        verify(userRepository, times(1)).findById(1L);
        verify(passwordUtils, times(1)).hashPassword("newPassword");
        verify(userMapper, times(1)).toEntity(userDto);
        verify(userRepository, times(1)).update(argThat(u -> u.getUserId().equals(1L)
                && u.getName().equals("John Doe") && u.getEmail().equals("john@example.com")
                && u.getPassword().equals("hashedPassword") && u.getRole().equals(Role.USER)
                && u.getVersion().equals(3L)));
    }

    @Test
    @DisplayName("Delete own account - existing user - deletes successfully")
    void testDeleteOwnAccount_existingUser_deletesSuccessfully() {
        Long userId = 1L;
        when(userRepository.delete(userId)).thenReturn(true);

        boolean result = userService.deleteOwnAccount(userId);

        assertThat(result).isTrue();
        verify(userRepository, times(1)).delete(userId);
    }

    @Test
    @DisplayName("Update own account - password not provided - updates successfully")
    void testUpdateOwnAccount_nullPassword_UpdatesSuccessfully() {
        userDto.setPassword(null);
        user.setPassword("existingHashedPassword");

        when(userRepository.findById(1L)).thenReturn(user);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.update(any(User.class))).thenReturn(true);

        boolean result = userService.updateOwnAccount(userDto, 1L);

        assertThat(result).isTrue();
        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).toEntity(userDto);
        verify(userRepository, times(1))
                .update(argThat(user -> user.getPassword().equals("existingHashedPassword")));
        verify(passwordUtils, never()).hashPassword(any());
    }

    @Test
    @DisplayName("Update own account - update fails - throws OptimisticLockException")
    void testUpdateOwnAccount_updateFails_throwsOptimisticLockException() {
        user.setRole(Role.USER);
        user.setVersion(2L);
        userDto.setVersion(2L);

        when(userRepository.findById(1L)).thenReturn(user);
        when(passwordUtils.hashPassword("newPassword")).thenReturn("hashedPassword");
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.update(any(User.class))).thenReturn(false);

        assertThatThrownBy(() -> userService.updateOwnAccount(userDto, 1L))
                .isInstanceOf(OptimisticLockException.class)
                .hasMessageContaining("Your account was modified by another operation.");

        verify(userRepository, times(1)).findById(1L);
        verify(passwordUtils, times(1)).hashPassword("newPassword");
        verify(userMapper, times(1)).toEntity(userDto);
        verify(userRepository, times(1)).update(argThat(u -> u.getUserId().equals(1L)
                && u.getName().equals("John Doe") && u.getEmail().equals("john@example.com")
                && u.getPassword().equals("hashedPassword") && u.getRole().equals(Role.USER)
                && u.getVersion().equals(2L)));
    }

    @Test
    @DisplayName("Delete own account - non-existing user - returns false")
    void testDeleteOwnAccount_nonExistingUser_returnsFalse() {
        Long userId = 99L;
        when(userRepository.delete(userId)).thenReturn(false);

        boolean result = userService.deleteOwnAccount(userId);

        assertThat(result).isFalse();
        verify(userRepository, times(1)).delete(userId);
    }
}