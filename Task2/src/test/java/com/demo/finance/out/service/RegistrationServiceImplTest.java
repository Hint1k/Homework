package com.demo.finance.out.service;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.PasswordUtils;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.impl.RegistrationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordUtils passwordUtils;
    @InjectMocks private RegistrationServiceImpl registrationService;

    @Test
    void testRegisterUser_Success() {
        String name = "Alice";
        String email = "mymail@mail.com";
        String password = "password123";
        Role role = new Role("user");
        Long userId = 1L;

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.generateNextId()).thenReturn(userId);
        when(passwordUtils.hashPassword(password)).thenReturn("hashedPassword");

        boolean result = registrationService.registerUser(name, email, password, role);

        assertThat(result).isTrue();
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).generateNextId();
        verify(passwordUtils, times(1)).hashPassword(password);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        String name = "Kate";
        String email = "maymail@mail.com";
        String password = "password321";
        Role role = new Role("user");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(
                new User(1L, name, email, "hashedPassword", false, role)
        ));

        boolean result = registrationService.registerUser(name, email, password, role);

        assertThat(result).isFalse();
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, never()).generateNextId();
        verify(passwordUtils, never()).hashPassword(password);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAuthenticate_Success() {
        String email = "alice@mail.com";
        String password = "password123";
        String hashedPassword = "hashedPassword";
        User user = new User(1L, "Alice", email, hashedPassword, false, new Role("user"));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordUtils.checkPassword(password, hashedPassword)).thenReturn(true);

        Optional<User> result = registrationService.authenticate(email, password);

        assertThat(result).isPresent().contains(user);
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordUtils, times(1)).checkPassword(password, hashedPassword);
    }

    @Test
    void testAuthenticate_InvalidEmail() {
        String email = "alice@mail.com";
        String password = "password123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> result = registrationService.authenticate(email, password);

        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordUtils, never()).checkPassword(anyString(), anyString());
    }

    @Test
    void testAuthenticate_InvalidPassword() {
        String email = "alice@mail.com";
        String password = "wrongPassword";
        String hashedPassword = "hashedPassword";
        User user = new User(1L, "Alice", email, hashedPassword, false, new Role("user"));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordUtils.checkPassword(password, hashedPassword)).thenReturn(false);

        Optional<User> result = registrationService.authenticate(email, password);

        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordUtils, times(1)).checkPassword(password, hashedPassword);
    }

    @Test
    void testRegisterUser_GenerateNextIdCalledOnce() {
        String name = "Bob";
        String email = "bob@mail.com";
        String password = "password123";
        Role role = new Role("user");
        Long userId = 1L;

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.generateNextId()).thenReturn(userId);
        when(passwordUtils.hashPassword(password)).thenReturn("hashedPassword");

        registrationService.registerUser(name, email, password, role);

        verify(userRepository, times(1)).generateNextId();
    }

    @Test
    void testAuthenticate_UserNotActive_ReturnsEmpty() {
        String email = "alice@mail.com";
        String password = "password123";
        String hashedPassword = "hashedPassword";
        Role role = new Role("user");
        User user = new User(1L, "Alice", email, hashedPassword, false, role);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordUtils.checkPassword(password, hashedPassword)).thenReturn(true);

        Optional<User> result = registrationService.authenticate(email, password);

        assertThat(result).isPresent().contains(user);
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordUtils, times(1)).checkPassword(password, hashedPassword);
    }
}