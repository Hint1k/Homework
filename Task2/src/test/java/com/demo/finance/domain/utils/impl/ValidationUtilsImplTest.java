package com.demo.finance.domain.utils.impl;

import com.demo.finance.domain.utils.Type;
import com.demo.finance.exception.MaxRetriesReachedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.reset;

@ExtendWith(MockitoExtension.class)
class ValidationUtilsImplTest {

    @Mock private Scanner scanner;
    @InjectMocks private ValidationUtilsImpl validationUtils;

    @BeforeEach
    void setUp() {
        reset(scanner);
    }

    @Test
    @DisplayName("Valid input for positive BigDecimal - returns the correct value")
    void testPromptForPositiveBigDecimal_ValidInput_ReturnsValue() {
        when(scanner.nextLine()).thenReturn("100");

        BigDecimal result = validationUtils.promptForPositiveBigDecimal("Enter amount: ", scanner);

        assertThat(result).isEqualTo(new BigDecimal(100));
    }

    @Test
    @DisplayName("Invalid input for positive BigDecimal - throws MaxRetriesReachedException after retries")
    void testPromptForPositiveBigDecimal_InvalidInput_ThrowsMaxRetriesReachedException() {
        when(scanner.nextLine()).thenReturn("invalid", "invalid", "invalid");

        assertThatThrownBy(() -> validationUtils.promptForPositiveBigDecimal("Enter amount: ", scanner))
                .isInstanceOf(MaxRetriesReachedException.class)
                .hasMessageContaining("Maximum retries reached");
    }

    @Test
    @DisplayName("Valid input for optional positive BigDecimal - returns the correct value")
    void testPromptForOptionalPositiveBigDecimal_ValidInput_ReturnsValue() {
        when(scanner.nextLine()).thenReturn("200");

        BigDecimal result = validationUtils.promptForOptionalPositiveBigDecimal("Enter amount: ", scanner);

        assertThat(result).isEqualTo(new BigDecimal(200));
    }

    @Test
    @DisplayName("Empty input for optional positive BigDecimal - returns null")
    void testPromptForOptionalPositiveBigDecimal_EmptyInput_ReturnsNull() {
        when(scanner.nextLine()).thenReturn("");

        BigDecimal result = validationUtils.promptForOptionalPositiveBigDecimal("Enter amount: ", scanner);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Valid input for positive Long - returns the correct value")
    void testPromptForPositiveLong_ValidInput_ReturnsValue() {
        when(scanner.nextLine()).thenReturn("12345");

        long result = validationUtils.promptForPositiveLong("Enter ID: ", scanner);

        assertThat(result).isEqualTo(12345);
    }

    @Test
    @DisplayName("Invalid input for positive Long - throws MaxRetriesReachedException after retries")
    void testPromptForPositiveLong_InvalidInput_ThrowsMaxRetriesReachedException() {
        when(scanner.nextLine()).thenReturn("invalid", "invalid", "invalid");

        assertThatThrownBy(() -> validationUtils.promptForPositiveLong("Enter ID: ", scanner))
                .isInstanceOf(MaxRetriesReachedException.class)
                .hasMessageContaining("Maximum retries reached");
    }

    @Test
    @DisplayName("Valid input for non-empty String - returns the correct value")
    void testPromptForNonEmptyString_ValidInput_ReturnsValue() {
        when(scanner.nextLine()).thenReturn("Valid Input");

        String result = validationUtils.promptForNonEmptyString("Enter text: ", scanner);

        assertThat(result).isEqualTo("Valid Input");
    }

    @Test
    @DisplayName("Empty input for non-empty String - throws MaxRetriesReachedException after retries")
    void testPromptForNonEmptyString_EmptyInput_ThrowsMaxRetriesReachedException() {
        when(scanner.nextLine()).thenReturn("", "", "");

        assertThatThrownBy(() -> validationUtils.promptForNonEmptyString("Enter text: ", scanner))
                .isInstanceOf(MaxRetriesReachedException.class)
                .hasMessageContaining("Maximum retries reached");
    }

    @Test
    @DisplayName("Valid input for optional String - returns the correct value")
    void testPromptForOptionalString_ValidInput_ReturnsValue() {
        when(scanner.nextLine()).thenReturn("Optional Input");

        String result = validationUtils.promptForOptionalString("Enter text: ", scanner);

        assertThat(result).isEqualTo("Optional Input");
    }

    @Test
    @DisplayName("Empty input for optional String - returns null")
    void testPromptForOptionalString_EmptyInput_ReturnsNull() {
        when(scanner.nextLine()).thenReturn("");

        String result = validationUtils.promptForOptionalString("Enter text: ", scanner);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Valid input for valid Email - returns the correct value")
    void testPromptForValidEmail_ValidInput_ReturnsEmail() {
        when(scanner.nextLine()).thenReturn("user@example.com");

        String result = validationUtils.promptForValidEmail("Enter email: ", scanner);

        assertThat(result).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("Invalid input for valid Email - throws MaxRetriesReachedException after retries")
    void testPromptForValidEmail_InvalidInput_ThrowsMaxRetriesReachedException() {
        when(scanner.nextLine()).thenReturn("invalid-email", "invalid-email", "invalid-email");

        assertThatThrownBy(() -> validationUtils.promptForValidEmail("Enter email: ", scanner))
                .isInstanceOf(MaxRetriesReachedException.class)
                .hasMessageContaining("Maximum retries reached");
    }

    @Test
    @DisplayName("Valid input for optional Email - returns the correct value")
    void testPromptForOptionalEmail_ValidInput_ReturnsEmail() {
        when(scanner.nextLine()).thenReturn("user@example.com");

        String result = validationUtils.promptForOptionalEmail("Enter email: ", scanner);

        assertThat(result).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("Invalid input for optional Email - returns null")
    void testPromptForOptionalEmail_InvalidInput_ReturnsNull() {
        when(scanner.nextLine()).thenReturn("invalid-email");

        String result = validationUtils.promptForOptionalEmail("Enter email: ", scanner);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Valid input for valid Password - returns the correct value")
    void testPromptForValidPassword_ValidInput_ReturnsPassword() {
        when(scanner.nextLine()).thenReturn("password123");

        String result = validationUtils.promptForValidPassword("Enter password: ", scanner);

        assertThat(result).isEqualTo("password123");
    }

    @Test
    @DisplayName("Invalid input for valid Password - throws MaxRetriesReachedException after retries")
    void testPromptForValidPassword_InvalidInput_ThrowsMaxRetriesReachedException() {
        when(scanner.nextLine()).thenReturn("pw", "pw", "pw");

        assertThatThrownBy(() -> validationUtils.promptForValidPassword("Enter password: ", scanner))
                .isInstanceOf(MaxRetriesReachedException.class)
                .hasMessageContaining("Maximum retries reached");
    }

    @Test
    @DisplayName("Valid input for optional Password - returns the correct value")
    void testPromptForOptionalPassword_ValidInput_ReturnsPassword() {
        when(scanner.nextLine()).thenReturn("password123");

        String result = validationUtils.promptForOptionalPassword("Enter password: ", scanner);

        assertThat(result).isEqualTo("password123");
    }

    @Test
    @DisplayName("Invalid input for optional Password - returns null")
    void testPromptForOptionalPassword_InvalidInput_ReturnsNull() {
        when(scanner.nextLine()).thenReturn("pw");

        String result = validationUtils.promptForOptionalPassword("Enter password: ", scanner);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Valid input for valid Date - returns the correct value")
    void testPromptForValidDate_ValidInput_ReturnsDate() {
        when(scanner.nextLine()).thenReturn("2025-03-10");

        LocalDate result = validationUtils.promptForValidDate("Enter date (YYYY-MM-DD): ", scanner);

        assertThat(result).isEqualTo(LocalDate.of(2025, 3, 10));
    }

    @Test
    @DisplayName("Invalid input for valid Date - throws MaxRetriesReachedException after retries")
    void testPromptForValidDate_InvalidInput_ThrowsMaxRetriesReachedException() {
        when(scanner.nextLine()).thenReturn("invalid-date", "invalid-date", "invalid-date");

        assertThatThrownBy(() -> validationUtils.promptForValidDate("Enter date (YYYY-MM-DD): ", scanner))
                .isInstanceOf(MaxRetriesReachedException.class)
                .hasMessageContaining("Maximum retries reached");
    }

    @Test
    @DisplayName("Valid input for optional Date - returns the correct value")
    void testPromptForOptionalDate_ValidInput_ReturnsDate() {
        when(scanner.nextLine()).thenReturn("2025-03-10");

        LocalDate result = validationUtils.promptForOptionalDate("Enter date (YYYY-MM-DD): ", scanner);

        assertThat(result).isEqualTo(LocalDate.of(2025, 3, 10));
    }

    @Test
    @DisplayName("Invalid input for optional Date - returns null")
    void testPromptForOptionalDate_InvalidInput_ReturnsNull() {
        when(scanner.nextLine()).thenReturn("invalid-date");

        LocalDate result = validationUtils.promptForOptionalDate("Enter date (YYYY-MM-DD): ", scanner);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Valid input for Transaction Type - returns the correct type")
    void testPromptForTransactionType_ValidInput_ReturnsType() {
        when(scanner.nextLine()).thenReturn("i");

        Type result = validationUtils.promptForTransactionType(scanner);

        assertThat(result).isEqualTo(Type.INCOME);
    }

    @Test
    @DisplayName("Invalid input for Transaction Type - throws MaxRetriesReachedException after retries")
    void testPromptForTransactionType_InvalidInput_ThrowsMaxRetriesReachedException() {
        when(scanner.nextLine()).thenReturn("invalid", "invalid", "invalid");

        assertThatThrownBy(() -> validationUtils.promptForTransactionType(scanner))
                .isInstanceOf(MaxRetriesReachedException.class)
                .hasMessageContaining("Maximum retries reached");
    }

    @Test
    @DisplayName("Valid input for optional Transaction Type - returns the correct type")
    void testPromptForOptionalTransactionType_ValidInput_ReturnsType() {
        when(scanner.nextLine()).thenReturn("e");

        Type result = validationUtils.promptForOptionalTransactionType(scanner);

        assertThat(result).isEqualTo(Type.EXPENSE);
    }

    @Test
    @DisplayName("Empty input for optional Transaction Type - returns null")
    void testPromptForOptionalTransactionType_EmptyInput_ReturnsNull() {
        when(scanner.nextLine()).thenReturn("");

        Type result = validationUtils.promptForOptionalTransactionType(scanner);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Valid input for int in range - returns the correct value")
    void testPromptForIntInRange_ValidInput_ReturnsValue() {
        when(scanner.nextLine()).thenReturn("5");

        int result = validationUtils.promptForIntInRange("Enter number (1-10): ", 1, 10, scanner);

        assertThat(result).isEqualTo(5);
    }

    @Test
    @DisplayName("Invalid input for int in range - throws MaxRetriesReachedException after retries")
    void testPromptForIntInRange_InvalidInput_ThrowsMaxRetriesReachedException() {
        when(scanner.nextLine()).thenReturn("invalid", "invalid", "invalid");

        assertThatThrownBy(() -> validationUtils
                .promptForIntInRange("Enter number (1-10): ", 1, 10, scanner))
                .isInstanceOf(MaxRetriesReachedException.class)
                .hasMessageContaining("Maximum retries reached");
    }

    @Test
    @DisplayName("Valid input for positive int - returns the correct value")
    void testPromptForPositiveInt_ValidInput_ReturnsValue() {
        when(scanner.nextLine()).thenReturn("10");

        int result = validationUtils.promptForPositiveInt("Enter positive number: ", scanner);

        assertThat(result).isEqualTo(10);
    }

    @Test
    @DisplayName("Invalid input for positive int - throws MaxRetriesReachedException after retries")
    void testPromptForPositiveInt_InvalidInput_ThrowsMaxRetriesReachedException() {
        when(scanner.nextLine()).thenReturn("invalid", "invalid", "invalid");

        assertThatThrownBy(() -> validationUtils.promptForPositiveInt("Enter positive number: ", scanner))
                .isInstanceOf(MaxRetriesReachedException.class)
                .hasMessageContaining("Maximum retries reached");
    }

    @Test
    @DisplayName("Valid input for optional positive int - returns the correct value")
    void testPromptForOptionalPositiveInt_ValidInput_ReturnsValue() {
        when(scanner.nextLine()).thenReturn("15");

        Integer result = validationUtils.promptForOptionalPositiveInt("Enter positive number: ", scanner);

        assertThat(result).isEqualTo(15);
    }

    @Test
    @DisplayName("Empty input for optional positive int - returns null")
    void testPromptForOptionalPositiveInt_EmptyInput_ReturnsNull() {
        when(scanner.nextLine()).thenReturn("");

        Integer result = validationUtils.promptForOptionalPositiveInt("Enter positive number: ", scanner);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Invalid input for optional positive int - returns null")
    void testPromptForOptionalPositiveInt_InvalidInput_ReturnsNull() {
        when(scanner.nextLine()).thenReturn("invalid");

        Integer result = validationUtils.promptForOptionalPositiveInt("Enter positive number: ", scanner);

        assertThat(result).isNull();
    }
}