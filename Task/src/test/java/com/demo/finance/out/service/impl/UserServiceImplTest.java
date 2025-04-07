package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.utils.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.impl.PasswordUtilsImpl;
import com.demo.finance.out.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
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

    private User createDefaultUser() {
        return new User(1L, "John Doe", "john@example.com", "hashedPassword",
                false, Role.USER, 1L);
    }

    private UserDto createDefaultUserDto() {
        UserDto dto = new UserDto();
        dto.setUserId(1L);
        dto.setName("John Doe");
        dto.setEmail("john@example.com");
        dto.setPassword("newPassword");
        return dto;
    }

    @Test
    @DisplayName("Update own account - existing user - updates successfully")
    void testUpdateOwnAccount_existingUser_updatesSuccessfully() {
        UserDto userDto = createDefaultUserDto();
        User existingUser = createDefaultUser();
        User mappedUser = new User();
        mappedUser.setName(userDto.getName());
        mappedUser.setEmail(userDto.getEmail());

        when(userRepository.findById(1L)).thenReturn(existingUser);
        when(passwordUtils.hashPassword("newPassword")).thenReturn("hashedPassword");
        when(userMapper.toEntity(userDto)).thenReturn(mappedUser);
        when(userRepository.update(any(User.class))).thenReturn(true);

        boolean result = userService.updateOwnAccount(userDto, 1L);

        assertThat(result).isTrue();
        verify(userRepository).findById(1L);
        verify(passwordUtils).hashPassword("newPassword");
        verify(userMapper).toEntity(userDto);
        verify(userRepository).update(argThat(user ->
                user.getUserId().equals(1L) &&
                        user.getName().equals("John Doe") &&
                        user.getEmail().equals("john@example.com") &&
                        user.getPassword().equals("hashedPassword") &&
                        user.getRole().equals(Role.USER) &&
                        user.getVersion() == 2L
        ));
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
        UserDto userDto = createDefaultUserDto();
        userDto.setPassword(null);

        User existingUser = createDefaultUser();
        existingUser.setPassword("existingHashedPassword");

        User mappedUser = new User();
        mappedUser.setName(userDto.getName());
        mappedUser.setEmail(userDto.getEmail());

        when(userRepository.findById(1L)).thenReturn(existingUser);
        when(userMapper.toEntity(userDto)).thenReturn(mappedUser);
        when(userRepository.update(any(User.class))).thenReturn(true);

        boolean result = userService.updateOwnAccount(userDto, 1L);

        assertThat(result).isTrue();
        verify(userRepository).findById(1L);
        verify(userMapper).toEntity(userDto);
        verify(userRepository).update(argThat(user ->
                user.getPassword().equals("existingHashedPassword")
        ));
        verify(passwordUtils, never()).hashPassword(any());
    }

    @Test
    @DisplayName("Update own account - update fails - returns false")
    void testUpdateOwnAccount_updateFails_returnsFalse() {
        UserDto userDto = createDefaultUserDto();
        User existingUser = new User(1L, "Old Name", "old@example.com",
                "oldHashedPassword", false, Role.USER, 1L);

        User mappedUser = new User();
        mappedUser.setName(userDto.getName());
        mappedUser.setEmail(userDto.getEmail());

        when(userRepository.findById(1L)).thenReturn(existingUser);
        when(passwordUtils.hashPassword("newPassword")).thenReturn("hashedPassword");
        when(userMapper.toEntity(userDto)).thenReturn(mappedUser);
        when(userRepository.update(any(User.class))).thenReturn(false);

        boolean result = userService.updateOwnAccount(userDto, 1L);

        assertThat(result).isFalse();
        verify(userRepository).findById(1L);
        verify(passwordUtils).hashPassword("newPassword");
        verify(userMapper).toEntity(userDto);
        verify(userRepository).update(argThat(user ->
                user.getUserId().equals(1L) &&
                        user.getName().equals("John Doe") &&
                        user.getEmail().equals("john@example.com") &&
                        user.getPassword().equals("hashedPassword") &&
                        user.getRole().equals(Role.USER) &&
                        user.getVersion() == 2L
        ));
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