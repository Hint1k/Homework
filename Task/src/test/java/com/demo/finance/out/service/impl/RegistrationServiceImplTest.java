package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.Role;
import com.demo.finance.domain.utils.impl.PasswordUtilsImpl;
import com.demo.finance.exception.DuplicateEmailException;
import com.demo.finance.out.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordUtilsImpl passwordUtils;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private RegistrationServiceImpl registrationService;

    @Test
    @DisplayName("Register user - new email - returns true")
    void testRegisterUser_newEmail_returnsTrue() {
        UserDto dto = new UserDto();
        dto.setName("Alice");
        dto.setEmail("alice@mail.com");
        dto.setPassword("password123");

        User userEntity = new User();
        userEntity.setName("Alice");
        userEntity.setEmail("alice@mail.com");
        userEntity.setPassword("password123");

        when(userMapper.toEntity(dto)).thenReturn(userEntity);
        when(userRepository.findByEmail("alice@mail.com")).thenReturn(null);
        when(passwordUtils.hashPassword("password123")).thenReturn("hashedPassword");

        boolean result = registrationService.registerUser(dto);

        assertThat(result).isTrue();
        verify(userMapper).toEntity(dto);
        verify(userRepository).save(argThat(user ->
                user.getName().equals("Alice") &&
                        user.getEmail().equals("alice@mail.com") &&
                        user.getPassword().equals("hashedPassword") &&
                        !user.isBlocked() &&
                        user.getRole().equals(Role.USER) &&
                        user.getVersion() == 1L
        ));
    }

    @Test
    @DisplayName("Register user - existing email - throws DuplicateEmailException")
    void testRegisterUser_existingEmail_throwsException() {
        UserDto dto = new UserDto();
        dto.setEmail("existing@mail.com");

        User existingUser = new User();
        existingUser.setEmail("existing@mail.com");

        User mappedUser = new User();
        mappedUser.setEmail("existing@mail.com");

        when(userMapper.toEntity(dto)).thenReturn(mappedUser);
        when(userRepository.findByEmail("existing@mail.com")).thenReturn(existingUser);

        assertThatThrownBy(() -> registrationService.registerUser(dto))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("Email is already registered: existing@mail.com");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Authenticate - valid credentials - returns true")
    void testAuthenticate_validCredentials_returnsTrue() {
        UserDto dto = new UserDto();
        dto.setEmail("valid@mail.com");
        dto.setPassword("correctPassword");

        User user = new User();
        user.setPassword("hashedPassword");
        when(userRepository.findByEmail("valid@mail.com")).thenReturn(user);
        when(passwordUtils.checkPassword("correctPassword", "hashedPassword"))
                .thenReturn(true);

        boolean result = registrationService.authenticate(dto);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Authenticate - invalid email - returns false")
    void testAuthenticate_invalidEmail_returnsFalse() {
        UserDto dto = new UserDto();
        dto.setEmail("unknown@mail.com");

        when(userRepository.findByEmail("unknown@mail.com")).thenReturn(null);

        boolean result = registrationService.authenticate(dto);

        assertThat(result).isFalse();
        verify(passwordUtils, never()).checkPassword(any(), any());
    }

    @Test
    @DisplayName("Authenticate - invalid password - returns false")
    void testAuthenticate_invalidPassword_returnsFalse() {
        UserDto dto = new UserDto();
        dto.setEmail("valid@mail.com");
        dto.setPassword("wrongPassword");

        User user = new User();
        user.setPassword("hashedPassword");
        when(userRepository.findByEmail("valid@mail.com")).thenReturn(user);
        when(passwordUtils.checkPassword("wrongPassword", "hashedPassword"))
                .thenReturn(false);

        boolean result = registrationService.authenticate(dto);

        assertThat(result).isFalse();
    }
}