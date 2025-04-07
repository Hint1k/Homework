package com.demo.finance.domain.utils.impl;

import com.demo.finance.domain.dto.BudgetDto;
import com.demo.finance.domain.dto.GoalDto;
import com.demo.finance.domain.dto.ReportDatesDto;
import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.PaginationParams;
import com.demo.finance.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ValidationUtilsImplTest {

    @InjectMocks
    private ValidationUtilsImpl validationUtils;

    @Test
    @DisplayName("Validate UserDto - valid input - returns validated object")
    void testValidateUser_ValidInput_Success() {
        UserDto user = new UserDto(1L, "John", "john@test.com", "password123",
                false, "USER", 1L);

        UserDto result = validationUtils.validateRequest(user, Mode.REGISTER_USER);
        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("Validate UserDto - invalid email - throws ValidationException")
    void testValidateUser_InvalidEmail_ThrowsException() {
        UserDto user = new UserDto(1L, "John", "invalid-email", "password123",
                false, "USER", 1L);

        assertThatThrownBy(() -> validationUtils.validateRequest(user, Mode.REGISTER_USER))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid email format");
    }

    @Test
    @DisplayName("Validate TransactionDto - valid input - returns validated object")
    void testValidateTransaction_ValidInput_Success() {
        TransactionDto transaction = new TransactionDto(1L, 1L, BigDecimal.valueOf(100),
                "Food", LocalDate.now(), "Lunch", "EXPENSE");

        TransactionDto result = validationUtils.validateRequest(transaction, Mode.TRANSACTION_CREATE);
        assertThat(result).isEqualTo(transaction);
    }

    @Test
    @DisplayName("Validate TransactionDto - negative amount - throws ValidationException")
    void testValidateTransaction_NegativeAmount_ThrowsException() {
        TransactionDto transaction = new TransactionDto(1L, 1L, BigDecimal.valueOf(-100),
                "Food", LocalDate.now(), "Lunch", "EXPENSE");

        assertThatThrownBy(() -> validationUtils.validateRequest(transaction, Mode.TRANSACTION_CREATE))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Amount must be a positive number");
    }

    @Test
    @DisplayName("Validate GoalDto - valid input - returns validated object")
    void testValidateGoal_ValidInput_Success() {
        GoalDto goal = new GoalDto(1L, 1L, "New Car", BigDecimal.valueOf(20000),
                BigDecimal.ZERO, 12, LocalDate.now());

        GoalDto result = validationUtils.validateRequest(goal, Mode.GOAL_CREATE);
        assertThat(result).isEqualTo(goal);
    }

    @Test
    @DisplayName("Validate GoalDto - zero duration - throws ValidationException")
    void testValidateGoal_ZeroDuration_ThrowsException() {
        GoalDto goal = new GoalDto(1L, 1L, "New Car", BigDecimal.valueOf(20000),
                BigDecimal.ZERO, 0, LocalDate.now());

        assertThatThrownBy(() -> validationUtils.validateRequest(goal, Mode.GOAL_CREATE))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Duration must be a positive integer");
    }

    @Test
    @DisplayName("Validate PaginationParams - valid input - returns validated object")
    void testValidatePagination_ValidInput_Success() {
        PaginationParams params = new PaginationParams(1, 10);

        PaginationParams result = validationUtils.validateRequest(params, Mode.PAGE);
        assertThat(result).isEqualTo(params);
    }

    @Test
    @DisplayName("Validate PaginationParams - size too large - throws ValidationException")
    void testValidatePagination_SizeTooLarge_ThrowsException() {
        PaginationParams params = new PaginationParams(1, 101);

        assertThatThrownBy(() -> validationUtils.validateRequest(params, Mode.PAGE))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Size cannot exceed 100");
    }

    @Test
    @DisplayName("Validate ReportDatesDto - valid dates - returns validated object")
    void testValidateReportDates_ValidDates_Success() {
        ReportDatesDto dates = new ReportDatesDto(
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 31)
        );

        ReportDatesDto result = validationUtils.validateRequest(dates, Mode.REPORT);
        assertThat(result).isEqualTo(dates);
    }

    @Test
    @DisplayName("Validate ReportDatesDto - invalid date order - throws ValidationException")
    void testValidateReportDates_InvalidOrder_ThrowsException() {
        ReportDatesDto dates = new ReportDatesDto(
                LocalDate.of(2023, 1, 31),
                LocalDate.of(2023, 1, 1)
        );

        assertThatThrownBy(() -> validationUtils.validateRequest(dates, Mode.REPORT))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("To date cannot be before from date");
    }

    @Test
    @DisplayName("Parse user ID - valid input - returns Long")
    void testParseUserId_ValidInput_ReturnsLong() {
        Long result = validationUtils.parseUserId("123", Mode.DELETE);
        assertThat(result).isEqualTo(123L);
    }

    @Test
    @DisplayName("Parse user ID - invalid format - throws ValidationException")
    void testParseUserId_InvalidFormat_ThrowsException() {
        assertThatThrownBy(() -> validationUtils.parseUserId("abc", Mode.DELETE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid user ID format");
    }

    @Test
    @DisplayName("Parse user ID - admin deletion attempt - throws ValidationException")
    void testParseUserId_AdminDelete_ThrowsException() {
        assertThatThrownBy(() -> validationUtils.parseUserId("1", Mode.DELETE))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Default Admin cannot be deleted");
    }

    @Test
    @DisplayName("Parse long - valid input - returns Long")
    void testParseLong_ValidInput_ReturnsLong() {
        Long result = validationUtils.parseLong("123");
        assertThat(result).isEqualTo(123L);
    }

    @Test
    @DisplayName("Parse long - empty input - throws ValidationException")
    void testParseLong_EmptyInput_ThrowsException() {
        assertThatThrownBy(() -> validationUtils.parseLong(""))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Id cannot be null or empty");
    }

    @Test
    @DisplayName("Validate BudgetDto - valid input - returns validated object")
    void testValidateBudget_ValidInput_Success() {
        BudgetDto budget = new BudgetDto(1L, 1L, BigDecimal.valueOf(1000), BigDecimal.valueOf(500));

        BudgetDto result = validationUtils.validateRequest(budget, Mode.BUDGET);
        assertThat(result).isEqualTo(budget);
    }

    @Test
    @DisplayName("Validate BudgetDto - negative limit - throws ValidationException")
    void testValidateBudget_NegativeLimit_ThrowsException() {
        BudgetDto budget = new BudgetDto(1L, 1L, BigDecimal.valueOf(-1000), BigDecimal.valueOf(500));

        assertThatThrownBy(() -> validationUtils.validateRequest(budget, Mode.BUDGET))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Monthly limit must be a positive number");
    }

    @Test
    @DisplayName("Validate fields - missing required field - throws ValidationException")
    void testValidateFields_MissingRequired_ThrowsException() {
        UserDto user = new UserDto(1L, null, "john@test.com", "password123",
                false, "USER", 1L);

        assertThatThrownBy(() -> validationUtils.validateRequest(user, Mode.REGISTER_USER))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Missing required field: name");
    }

    @Test
    @DisplayName("Validate UserDto - empty password - throws ValidationException")
    void testValidateUser_EmptyPassword_ThrowsException() {
        UserDto user = new UserDto(1L, "John", "john@test.com", "",
                false, "USER", 1L);

        assertThatThrownBy(() -> validationUtils.validateRequest(user, Mode.REGISTER_USER))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Password cannot be empty");
    }

    @Test
    @DisplayName("Validate UserDto - empty name - throws ValidationException")
    void testValidateUser_EmptyName_ThrowsException() {
        UserDto user = new UserDto(1L, "", "john@test.com", "password123",
                false, "USER", 1L);

        assertThatThrownBy(() -> validationUtils.validateRequest(user, Mode.REGISTER_USER))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Name cannot be empty");
    }

    @Test
    @DisplayName("Validate TransactionDto - empty category - throws ValidationException")
    void testValidateTransaction_EmptyCategory_ThrowsException() {
        TransactionDto transaction = new TransactionDto(1L, 1L, BigDecimal.valueOf(100),
                "", LocalDate.now(), "Lunch", "EXPENSE");

        assertThatThrownBy(() -> validationUtils.validateRequest(transaction, Mode.TRANSACTION_CREATE))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Category cannot be empty");
    }

    @Test
    @DisplayName("Validate TransactionDto - invalid type - throws ValidationException")
    void testValidateTransaction_InvalidType_ThrowsException() {
        TransactionDto transaction = new TransactionDto(1L, 1L, BigDecimal.valueOf(100),
                "Food", LocalDate.now(), "Lunch", "INVALID");

        assertThatThrownBy(() -> validationUtils.validateRequest(transaction, Mode.TRANSACTION_CREATE))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Type must be either INCOME or EXPENSE");
    }

    @Test
    @DisplayName("Validate GoalDto - empty goal name - throws ValidationException")
    void testValidateGoal_EmptyName_ThrowsException() {
        GoalDto goal = new GoalDto(1L, 1L, "", BigDecimal.valueOf(20000),
                BigDecimal.ZERO, 12, LocalDate.now());

        assertThatThrownBy(() -> validationUtils.validateRequest(goal, Mode.GOAL_CREATE))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Goal name cannot be empty");
    }

    @Test
    @DisplayName("Validate PaginationParams - zero page - throws ValidationException")
    void testValidatePagination_ZeroPage_ThrowsException() {
        PaginationParams params = new PaginationParams(0, 10);

        assertThatThrownBy(() -> validationUtils.validateRequest(params, Mode.PAGE))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Page must be positive integer");
    }

    @Test
    @DisplayName("Parse user ID - admin role update attempt - throws ValidationException")
    void testParseUserId_AdminRoleUpdate_ThrowsException() {
        assertThatThrownBy(() -> validationUtils.parseUserId("1", Mode.UPDATE_ROLE))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Default Admin role cannot be changed");
    }

    @Test
    @DisplayName("Parse user ID - admin block attempt - throws ValidationException")
    void testParseUserId_AdminBlock_ThrowsException() {
        assertThatThrownBy(() -> validationUtils.parseUserId("1", Mode.BLOCK_UNBLOCK))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Default Admin cannot be blocked or unblocked");
    }

    @Test
    @DisplayName("Validate UserDto - UPDATE_USER mode - validates correctly")
    void testValidateUser_UpdateUserMode_Success() {
        UserDto user = new UserDto(1L, "John", "john@test.com", "newpassword",
                false, "USER", 1L);

        UserDto result = validationUtils.validateRequest(user, Mode.UPDATE_USER);
        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("Validate UserDto - AUTHENTICATE mode - validates correctly")
    void testValidateUser_AuthenticateMode_Success() {
        UserDto user = new UserDto(null, null, "john@test.com", "password123",
                false, null, null);

        UserDto result = validationUtils.validateRequest(user, Mode.AUTHENTICATE);
        assertThat(result).isEqualTo(user);
    }
}