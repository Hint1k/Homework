package com.demo.finance.domain.utils.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FlagUtilsImplTest {

    @InjectMocks
    private FlagUtilsImpl flagUtils;

    @Test
    @DisplayName("Should return false by default for validateWithDatabase flag")
    void testShouldValidateWithDatabase_DefaultFalse() {
        boolean result = flagUtils.shouldValidateWithDatabase();
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return true when validateWithDatabase flag is set to true")
    void testSetValidateWithDatabase_True() {
        flagUtils.setValidateWithDatabase(true);

        boolean result = flagUtils.shouldValidateWithDatabase();

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when validateWithDatabase flag is set to false")
    void testSetValidateWithDatabase_False() {
        flagUtils.setValidateWithDatabase(false);

        boolean result = flagUtils.shouldValidateWithDatabase();

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should correctly toggle validateWithDatabase flag")
    void testToggleValidateWithDatabase() {
        flagUtils.setValidateWithDatabase(true);
        assertThat(flagUtils.shouldValidateWithDatabase()).isTrue();

        flagUtils.setValidateWithDatabase(false);
        assertThat(flagUtils.shouldValidateWithDatabase()).isFalse();
    }
}