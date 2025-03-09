package com.demo.finance.domain.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidationUtilsImplTest {

    @Mock private Scanner scanner;
    @InjectMocks private ValidationUtilsImpl validationUtils;

    @BeforeEach
    void setUp() {
        reset(scanner);
    }

    @Test
    void testPromptForPositiveDouble_ValidInput_ReturnsValue() {
        when(scanner.nextLine()).thenReturn("100.5");

        double result = validationUtils.promptForPositiveDouble("Enter amount: ", scanner);

        assertThat(result).isEqualTo(100.5);
    }

    @Test
    void testPromptForPositiveDouble_InvalidInput_ThrowsMaxRetriesReachedException() {
        when(scanner.nextLine()).thenReturn("invalid", "invalid", "invalid");

        assertThatThrownBy(() -> validationUtils.promptForPositiveDouble("Enter amount: ", scanner))
                .isInstanceOf(MaxRetriesReachedException.class)
                .hasMessageContaining("Maximum retries reached");
    }

    @Test
    void testPromptForOptionalPositiveDouble_ValidInput_ReturnsValue() {
        when(scanner.nextLine()).thenReturn("200.75");

        Double result = validationUtils.promptForOptionalPositiveDouble("Enter amount: ", scanner);

        assertThat(result).isEqualTo(200.75);
    }

    @Test
    void testPromptForOptionalPositiveDouble_EmptyInput_ReturnsNull() {
        when(scanner.nextLine()).thenReturn("");

        Double result = validationUtils.promptForOptionalPositiveDouble("Enter amount: ", scanner);

        assertThat(result).isNull();
    }

    @Test
    void testPromptForPositiveLong_ValidInput_ReturnsValue() {
        when(scanner.nextLine()).thenReturn("12345");

        long result = validationUtils.promptForPositiveLong("Enter ID: ", scanner);

        assertThat(result).isEqualTo(12345);
    }

    @Test
    void testPromptForPositiveLong_InvalidInput_ThrowsMaxRetriesReachedException() {
        when(scanner.nextLine()).thenReturn("invalid", "invalid", "invalid");

        assertThatThrownBy(() -> validationUtils.promptForPositiveLong("Enter ID: ", scanner))
                .isInstanceOf(MaxRetriesReachedException.class)
                .hasMessageContaining("Maximum retries reached");
    }

    @Test
    void testPromptForNonEmptyString_ValidInput_ReturnsValue() {
        when(scanner.nextLine()).thenReturn("Valid Input");

        String result = validationUtils.promptForNonEmptyString("Enter text: ", scanner);

        assertThat(result).isEqualTo("Valid Input");
    }

    @Test
    void testPromptForNonEmptyString_EmptyInput_ThrowsMaxRetriesReachedException() {
        when(scanner.nextLine()).thenReturn("", "", "");

        assertThatThrownBy(() -> validationUtils.promptForNonEmptyString("Enter text: ", scanner))
                .isInstanceOf(MaxRetriesReachedException.class)
                .hasMessageContaining("Maximum retries reached");
    }

    @Test
    void testPromptForOptionalString_ValidInput_ReturnsValue() {
        when(scanner.nextLine()).thenReturn("Optional Input");

        String result = validationUtils.promptForOptionalString("Enter text: ", scanner);

        assertThat(result).isEqualTo("Optional Input");
    }

    @Test
    void testPromptForOptionalString_EmptyInput_ReturnsNull() {
        when(scanner.nextLine()).thenReturn("");

        String result = validationUtils.promptForOptionalString("Enter text: ", scanner);

        assertThat(result).isNull();
    }

    @Test
    void testPromptForValidEmail_ValidInput_ReturnsEmail() {
        when(scanner.nextLine()).thenReturn("user@example.com");

        String result = validationUtils.promptForValidEmail("Enter email: ", scanner);

        assertThat(result).isEqualTo("user@example.com");
    }

    @Test
    void testPromptForValidEmail_InvalidInput_ThrowsMaxRetriesReachedException() {
        when(scanner.nextLine()).thenReturn("invalid-email", "invalid-email", "invalid-email");

        assertThatThrownBy(() -> validationUtils.promptForValidEmail("Enter email: ", scanner))
                .isInstanceOf(MaxRetriesReachedException.class)
                .hasMessageContaining("Maximum retries reached");
    }

    @Test
    void testPromptForOptionalEmail_ValidInput_ReturnsEmail() {
        when(scanner.nextLine()).thenReturn("user@example.com");

        String result = validationUtils.promptForOptionalEmail("Enter email: ", scanner);

        assertThat(result).isEqualTo("user@example.com");
    }

    @Test
    void testPromptForOptionalEmail_InvalidInput_ReturnsNull() {
        when(scanner.nextLine()).thenReturn("invalid-email");

        String result = validationUtils.promptForOptionalEmail("Enter email: ", scanner);

        assertThat(result).isNull();
    }

    @Test
    void testPromptForValidPassword_ValidInput_ReturnsPassword() {
        when(scanner.nextLine()).thenReturn("password123");

        String result = validationUtils.promptForValidPassword("Enter password: ", scanner);

        assertThat(result).isEqualTo("password123");
    }

    @Test
    void testPromptForValidPassword_InvalidInput_ThrowsMaxRetriesReachedException() {
        when(scanner.nextLine()).thenReturn("pw", "pw", "pw");

        assertThatThrownBy(() -> validationUtils.promptForValidPassword("Enter password: ", scanner))
                .isInstanceOf(MaxRetriesReachedException.class)
                .hasMessageContaining("Maximum retries reached");
    }

    @Test
    void testPromptForOptionalPassword_ValidInput_ReturnsPassword() {
        when(scanner.nextLine()).thenReturn("password123");

        String result = validationUtils.promptForOptionalPassword("Enter password: ", scanner);

        assertThat(result).isEqualTo("password123");
    }

    @Test
    void testPromptForOptionalPassword_InvalidInput_ReturnsNull() {
        when(scanner.nextLine()).thenReturn("pw");

        String result = validationUtils.promptForOptionalPassword("Enter password: ", scanner);

        assertThat(result).isNull();
    }

    @Test
    void testPromptForValidDate_ValidInput_ReturnsDate() {
        when(scanner.nextLine()).thenReturn("2025-03-10");

        LocalDate result = validationUtils.promptForValidDate("Enter date (YYYY-MM-DD): ", scanner);

        assertThat(result).isEqualTo(LocalDate.of(2025, 3, 10));
    }

    @Test
    void testPromptForValidDate_InvalidInput_ThrowsMaxRetriesReachedException() {
        when(scanner.nextLine()).thenReturn("invalid-date", "invalid-date", "invalid-date");

        assertThatThrownBy(() -> validationUtils.promptForValidDate("Enter date (YYYY-MM-DD): ", scanner))
                .isInstanceOf(MaxRetriesReachedException.class)
                .hasMessageContaining("Maximum retries reached");
    }

    @Test
    void testPromptForOptionalDate_ValidInput_ReturnsDate() {
        when(scanner.nextLine()).thenReturn("2025-03-10");

        LocalDate result = validationUtils.promptForOptionalDate("Enter date (YYYY-MM-DD): ", scanner);

        assertThat(result).isEqualTo(LocalDate.of(2025, 3, 10));
    }

    @Test
    void testPromptForOptionalDate_InvalidInput_ReturnsNull() {
        when(scanner.nextLine()).thenReturn("invalid-date");

        LocalDate result = validationUtils.promptForOptionalDate("Enter date (YYYY-MM-DD): ", scanner);

        assertThat(result).isNull();
    }

    @Test
    void testPromptForTransactionType_ValidInput_ReturnsType() {
        when(scanner.nextLine()).thenReturn("i");

        Type result = validationUtils.promptForTransactionType(scanner);

        assertThat(result).isEqualTo(Type.INCOME);
    }

    @Test
    void testPromptForTransactionType_InvalidInput_ThrowsMaxRetriesReachedException() {
        when(scanner.nextLine()).thenReturn("invalid", "invalid", "invalid");

        assertThatThrownBy(() -> validationUtils.promptForTransactionType(scanner))
                .isInstanceOf(MaxRetriesReachedException.class)
                .hasMessageContaining("Maximum retries reached");
    }

    @Test
    void testPromptForOptionalTransactionType_ValidInput_ReturnsType() {
        when(scanner.nextLine()).thenReturn("e");

        Type result = validationUtils.promptForOptionalTransactionType(scanner);

        assertThat(result).isEqualTo(Type.EXPENSE);
    }

    @Test
    void testPromptForOptionalTransactionType_EmptyInput_ReturnsNull() {
        when(scanner.nextLine()).thenReturn("");

        Type result = validationUtils.promptForOptionalTransactionType(scanner);

        assertThat(result).isNull();
    }

    @Test
    void testPromptForIntInRange_ValidInput_ReturnsValue() {
        when(scanner.nextLine()).thenReturn("5");

        int result = validationUtils.promptForIntInRange("Enter number (1-10): ", 1, 10, scanner);

        assertThat(result).isEqualTo(5);
    }

    @Test
    void testPromptForIntInRange_InvalidInput_ThrowsMaxRetriesReachedException() {
        when(scanner.nextLine()).thenReturn("invalid", "invalid", "invalid");

        assertThatThrownBy(() -> validationUtils
                .promptForIntInRange("Enter number (1-10): ", 1, 10, scanner))
                .isInstanceOf(MaxRetriesReachedException.class)
                .hasMessageContaining("Maximum retries reached");
    }

    @Test
    void testPromptForPositiveInt_ValidInput_ReturnsValue() {
        when(scanner.nextLine()).thenReturn("10");

        int result = validationUtils.promptForPositiveInt("Enter positive number: ", scanner);

        assertThat(result).isEqualTo(10);
    }

    @Test
    void testPromptForPositiveInt_InvalidInput_ThrowsMaxRetriesReachedException() {
        when(scanner.nextLine()).thenReturn("invalid", "invalid", "invalid");

        assertThatThrownBy(() -> validationUtils.promptForPositiveInt("Enter positive number: ", scanner))
                .isInstanceOf(MaxRetriesReachedException.class)
                .hasMessageContaining("Maximum retries reached");
    }

    @Test
    void testPromptForOptionalPositiveInt_ValidInput_ReturnsValue() {
        when(scanner.nextLine()).thenReturn("15");

        Integer result = validationUtils.promptForOptionalPositiveInt("Enter positive number: ", scanner);

        assertThat(result).isEqualTo(15);
    }

    @Test
    void testPromptForOptionalPositiveInt_EmptyInput_ReturnsNull() {
        when(scanner.nextLine()).thenReturn("");

        Integer result = validationUtils.promptForOptionalPositiveInt("Enter positive number: ", scanner);

        assertThat(result).isNull();
    }

    @Test
    void testPromptForOptionalPositiveInt_InvalidInput_ReturnsNull() {
        when(scanner.nextLine()).thenReturn("invalid");

        Integer result = validationUtils.promptForOptionalPositiveInt("Enter positive number: ", scanner);

        assertThat(result).isNull();
    }
}