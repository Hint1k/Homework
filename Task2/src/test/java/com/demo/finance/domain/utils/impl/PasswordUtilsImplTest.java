package com.demo.finance.domain.utils.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PasswordUtilsImplTest {
    
    @InjectMocks private PasswordUtilsImpl passwordUtils;

    @Test
    @DisplayName("Hash password - ensures the password is hashed successfully")
    void testHashPassword() {
        String rawPassword = "securePassword";
        String hashedPassword = passwordUtils.hashPassword(rawPassword);

        assertThat(hashedPassword).isNotEmpty();
    }

    @Test
    @DisplayName("Validate correct password - checks if the raw password matches the hashed password")
    void testValidateCorrectPassword() {
        String rawPassword = "securePassword";
        String hashedPassword = passwordUtils.hashPassword(rawPassword);

        assertThat(passwordUtils.checkPassword(rawPassword, hashedPassword)).isTrue();
    }

    @Test
    @DisplayName("Fail for incorrect password - verifies the raw password doesn't match the hashed password")
    void testFailForIncorrectPassword() {
        String hashedPassword = passwordUtils.hashPassword("correctPassword");

        assertThat(passwordUtils.checkPassword("wrongPassword", hashedPassword)).isFalse();
    }
}