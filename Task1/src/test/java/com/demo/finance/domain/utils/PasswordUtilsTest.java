package com.demo.finance.domain.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PasswordUtilsTest {
    
    @InjectMocks
    private final PasswordUtils passwordUtils = new PasswordUtils();

    @Test
    void testHashPassword() {
        String rawPassword = "securePassword";
        String hashedPassword = passwordUtils.hashPassword(rawPassword);

        assertThat(hashedPassword).isNotEmpty();
    }

    @Test
    void testValidateCorrectPassword() {
        String rawPassword = "securePassword";
        String hashedPassword = passwordUtils.hashPassword(rawPassword);

        assertThat(passwordUtils.checkPassword(rawPassword, hashedPassword)).isTrue();
    }

    @Test
    void testFailForIncorrectPassword() {
        String hashedPassword = passwordUtils.hashPassword("correctPassword");

        assertThat(passwordUtils.checkPassword("wrongPassword", hashedPassword)).isFalse();
    }
}