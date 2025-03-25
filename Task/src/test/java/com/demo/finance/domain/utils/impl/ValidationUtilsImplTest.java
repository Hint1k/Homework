package com.demo.finance.domain.utils.impl;

import com.demo.finance.domain.dto.GoalDto;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ValidationUtilsImplTest {

    @InjectMocks
    private ValidationUtilsImpl validationUtils;

    @Test
    @DisplayName("Validate user JSON with valid input - returns UserDto")
    void testValidateUserJson_ValidInput_ReturnsUserDto() {
        String json = "{\"name\":\"John\",\"email\":\"john@test.com\",\"password\":\"pass123\"}";
        UserDto result = validationUtils.validateUserJson(json, Mode.REGISTER);
        assertThat(result.getName()).isEqualTo("John");
        assertThat(result.getEmail()).isEqualTo("john@test.com");
    }

    @Test
    @DisplayName("Validate user JSON with invalid email - throws ValidationException")
    void testValidateUserJson_InvalidEmail_ThrowsException() {
        String json = "{\"name\":\"John\",\"email\":\"invalid\",\"password\":\"pass123\"}";
        assertThatThrownBy(() -> validationUtils.validateUserJson(json, Mode.REGISTER))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid email format");
    }

    @Test
    @DisplayName("Parse valid user ID - returns Long")
    void testParseUserId_ValidInput_ReturnsLong() {
        Long result = validationUtils.parseUserId("123", Mode.DELETE);
        assertThat(result).isEqualTo(123L);
    }

    @Test
    @DisplayName("Parse invalid user ID - throws IllegalArgumentException")
    void testParseUserId_InvalidInput_ThrowsException() {
        assertThatThrownBy(() -> validationUtils.parseUserId("abc", Mode.DELETE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid user ID format. User ID must be a positive integer.");
    }

    @Test
    @DisplayName("Parse admin user ID for delete - throws ValidationException")
    void testParseUserId_AdminDelete_ThrowsException() {
        assertThatThrownBy(() -> validationUtils.parseUserId("1", Mode.DELETE))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Default Admin cannot be deleted");
    }

    @Test
    @DisplayName("Validate pagination params with valid input - returns PaginationParams")
    void testValidatePaginationParams_ValidInput_ReturnsParams() {
        String json = "{\"page\":1,\"size\":10}";
        PaginationParams result = validationUtils.validatePaginationParams(json, Mode.PAGE);
        assertThat(result.page()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(10);
    }

    @Test
    @DisplayName("Validate pagination params with large size - throws Exception")
    void testValidatePaginationParams_SizeTooLarge_ThrowsException() {
        String json = "{\"page\":1,\"size\":101}";
        assertThatThrownBy(() -> validationUtils.validatePaginationParams(json, Mode.PAGE))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Size cannot exceed 100");
    }

    @Test
    @DisplayName("Validate report JSON with valid dates - returns date map")
    void testValidateReportJson_ValidDates_ReturnsMap() {
        String json = "{\"fromDate\":\"2023-01-01\",\"toDate\":\"2023-01-31\"}";
        Map<String, LocalDate> result = validationUtils.validateReportJson(json, Mode.REPORT, 1L);
        assertThat(result.get("fromDate")).isEqualTo(LocalDate.of(2023, 1, 1));
        assertThat(result.get("toDate")).isEqualTo(LocalDate.of(2023, 1, 31));
    }

    @Test
    @DisplayName("Validate report JSON with invalid date order - throws Exception")
    void testValidateReportJson_InvalidDateOrder_ThrowsException() {
        String json = "{\"fromDate\":\"2023-01-31\",\"toDate\":\"2023-01-01\"}";
        assertThatThrownBy(() -> validationUtils.validateReportJson(json, Mode.REPORT, 1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("FromDate cannot be after ToDate");
    }

    @Test
    @DisplayName("Validate budget JSON with valid limit - returns BigDecimal")
    void testValidateBudgetJson_ValidLimit_ReturnsBigDecimal() {
        String json = "{\"monthlyLimit\":1000.50}";
        BigDecimal result = validationUtils.validateBudgetJson(json, Mode.BUDGET, 1L);
        assertThat(result).isEqualTo(BigDecimal.valueOf(1000.50));
    }

    @Test
    @DisplayName("Validate transaction JSON with valid input - returns TransactionDto")
    void testValidateTransactionJson_ValidInput_ReturnsDto() {
        String json = "{\"userId\":1,\"amount\":100,\"category\":\"Food\",\"date\":\"2023-01-01\","
                + "\"description\": \"1\", \"type\":\"EXPENSE\"}";
        TransactionDto result = validationUtils.validateJson(json, Mode.TRANSACTION_CREATE, TransactionDto.class);
        assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(result.getType()).isEqualTo("EXPENSE");
    }

    @Test
    @DisplayName("Validate goal JSON with valid input - returns GoalDto")
    void testValidateGoalJson_ValidInput_ReturnsDto() {
        String json = "{\"userId\":1,\"goalName\":\"Car\",\"targetAmount\":10000,"
                + "\"duration\":12,\"startTime\":\"2025-03-30\"}";
        GoalDto result = validationUtils.validateJson(json, Mode.GOAL_CREATE, GoalDto.class);
        assertThat(result.getGoalName()).isEqualTo("Car");
        assertThat(result.getTargetAmount()).isEqualTo(BigDecimal.valueOf(10000));
    }

    @Test
    @DisplayName("Validate JSON with missing required field - throws Exception")
    void testValidateJson_MissingField_ThrowsException() {
        String json = "{\"name\":\"John\"}";
        assertThatThrownBy(() -> validationUtils.validateUserJson(json, Mode.REGISTER))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Missing required field");
    }

    @Test
    @DisplayName("Parse long with valid input - returns Long")
    void testParseLong_ValidInput_ReturnsLong() {
        Long result = validationUtils.parseLong("123");
        assertThat(result).isEqualTo(123L);
    }

    @Test
    @DisplayName("Parse long with empty input - throws Exception")
    void testParseLong_EmptyInput_ThrowsException() {
        assertThatThrownBy(() -> validationUtils.parseLong(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Id cannot be null or empty");
    }

    @Test
    @DisplayName("Validate field values with negative amount - throws Exception")
    void testValidateFieldValues_NegativeAmount_ThrowsException() {
        String json = "{\"userId\":10,\"amount\":\"-100\",\"category\":\"1\",\"date\":\"2025-03-23\","
                + "\"description\":\"1\",\"type\":\"EXPENSE\"}";
        assertThatThrownBy(() -> validationUtils.validateJson(json, Mode.TRANSACTION_CREATE, TransactionDto.class))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Amount must be positive");
    }

    @Test
    @DisplayName("Validate field values with invalid type - throws Exception")
    void testValidateFieldValues_InvalidType_ThrowsException() {
        String json = "{\"userId\":1,\"amount\":100,\"category\":\"Food\",\"date\":\"2023-01-01\","
                + "\"description\": \"1\", \"type\":\"INVALID\"}";
        assertThatThrownBy(() -> validationUtils.validateJson(json, Mode.TRANSACTION_CREATE, TransactionDto.class))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Type must be either INCOME or EXPENSE");
    }

    @Test
    @DisplayName("Validate user JSON with ID for update - returns UserDto with ID")
    void testValidateUserJson_WithIdForUpdate_ReturnsDtoWithId() {
        String json = "{\"userId\":1,\"name\":\"John\",\"email\":\"john@test.com\",\"password\":\"pass123\"}";
        UserDto result = validationUtils.validateUserJson(json, Mode.UPDATE, 1L);
        assertThat(result.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Validate user JSON with mismatched ID - throws Exception")
    void testValidateUserJson_MismatchedId_ThrowsException() {
        String json = "{\"userId\":2,\"name\":\"John\",\"email\":\"jay4@demo.com\",\"password\":\"123\"}";
        assertThatThrownBy(() -> validationUtils.validateUserJson(json, Mode.UPDATE, 1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("A user can't update other users");
    }

    @Test
    @DisplayName("Validate transaction JSON with ID - returns TransactionDto with ID")
    void testValidateTransactionJson_WithId_ReturnsDtoWithId() {
        String json = "{\"userId\":1,\"amount\":100,\"category\":1,\"description\":1}";
        TransactionDto result = validationUtils
                .validateTransactionJson(json, Mode.TRANSACTION_UPDATE, "123");
        assertThat(result.getTransactionId()).isEqualTo(123L);
    }

    @Test
    @DisplayName("Validate goal JSON with ID - returns GoalDto with ID")
    void testValidateGoalJson_WithId_ReturnsDtoWithId() {
        String json = "{\"userId\":1,\"goalName\":\"Car\",\"targetAmount\":10000,"
                + "\"duration\":12,\"startTime\":\"2025-03-30\"}";
        GoalDto result = validationUtils.validateGoalJson(json, Mode.GOAL_UPDATE, "456");
        assertThat(result.getGoalId()).isEqualTo(456L);
    }
}