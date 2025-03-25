package com.demo.finance.domain.utils;

import com.demo.finance.domain.dto.GoalDto;
import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.dto.UserDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * The {@code ValidationUtils} interface defines a contract for validating various JSON inputs and parameters
 * used in the application. It ensures that input data adheres to expected formats and constraints, providing
 * methods to validate user, transaction, goal, budget, and report-related data. This interface is designed
 * to be implemented by classes that perform detailed validation logic.
 */
public interface ValidationUtils {

    /**
     * Validates a JSON string representing a user and maps it to a {@link UserDto} object.
     *
     * @param json the JSON string to validate
     * @param mode the mode specifying the type of validation to perform
     * @return the validated {@link UserDto} object
     * @throws IllegalArgumentException if the JSON format is invalid or validation fails
     */
    UserDto validateUserJson(String json, Mode mode);

    /**
     * Validates a JSON string representing a user, maps it to a {@link UserDto} object,
     * and associates it with the provided user ID.
     *
     * @param json   the JSON string to validate
     * @param mode   the mode specifying the type of validation to perform
     * @param userId the string representation of the user ID
     * @return the validated {@link UserDto} object with the associated user ID
     * @throws IllegalArgumentException if the JSON format is invalid or validation fails
     */
    UserDto validateUserJson(String json, Mode mode, String userId);

    /**
     * Validates a JSON string representing a user, maps it to a {@link UserDto} object,
     * and ensures the user ID in the JSON matches the provided user ID.
     *
     * @param json   the JSON string to validate
     * @param mode   the mode specifying the type of validation to perform
     * @param userId the user ID to match
     * @return the validated {@link UserDto} object
     * @throws IllegalArgumentException if the user IDs do not match or validation fails
     */
    UserDto validateUserJson(String json, Mode mode, Long userId);

    /**
     * Validates pagination parameters based on the provided JSON string and mode.
     * This method ensures that the required fields are present in the JSON and validates their values.
     *
     * @param json the JSON string containing pagination parameters (e.g., "page" and "size").
     * @param mode the mode of validation, which determines the required fields and rules.
     * @return a {@link PaginationParams} object containing validated page and size values.
     */
    PaginationParams validatePaginationParams(String json, Mode mode);

    /**
     * Validates a JSON string representing a report request and extracts date ranges.
     *
     * @param json   the JSON string to validate
     * @param mode   the mode specifying the type of validation to perform
     * @param userId the user ID associated with the report
     * @return a {@link Map} containing the validated "fromDate" and "toDate" as {@link LocalDate} objects
     * @throws IllegalArgumentException if the JSON format is invalid or validation fails
     */
    Map<String, LocalDate> validateReportJson(String json, Mode mode, Long userId);

    /**
     * Validates a JSON string representing a budget request and extracts the monthly limit.
     *
     * @param json   the JSON string to validate
     * @param mode   the mode specifying the type of validation to perform
     * @param userId the user ID associated with the budget
     * @return the validated monthly limit as a {@link BigDecimal}
     * @throws IllegalArgumentException if the JSON format is invalid or validation fails
     */
    BigDecimal validateBudgetJson(String json, Mode mode, Long userId);

    /**
     * Validates a JSON string representing a transaction, maps it to a {@link TransactionDto} object,
     * and associates it with the provided transaction ID.
     *
     * @param json          the JSON string to validate
     * @param mode          the mode specifying the type of validation to perform
     * @param transactionId the string representation of the transaction ID
     * @return the validated {@link TransactionDto} object with the associated transaction ID
     * @throws IllegalArgumentException if the JSON format is invalid or validation fails
     */
    TransactionDto validateTransactionJson(String json, Mode mode, String transactionId);

    /**
     * Validates a JSON string representing a goal, maps it to a {@link GoalDto} object,
     * and associates it with the provided goal ID.
     *
     * @param json   the JSON string to validate
     * @param mode   the mode specifying the type of validation to perform
     * @param goalId the string representation of the goal ID
     * @return the validated {@link GoalDto} object with the associated goal ID
     * @throws IllegalArgumentException if the JSON format is invalid or validation fails
     */
    GoalDto validateGoalJson(String json, Mode mode, String goalId);

    /**
     * Parses and validates a user ID string based on the specified mode.
     * <p>
     * This method attempts to parse the provided user ID string into a {@code Long} value and validates it
     * according to the constraints defined by the given mode. If the input is invalid or violates the mode-specific
     * constraints, an exception is thrown.
     *
     * @param userIdString the string representation of the user ID to parse
     * @param mode         the mode specifying additional constraints for the user ID (e.g., GET, DELETE)
     * @return the parsed user ID as a {@code Long}
     * @throws IllegalArgumentException if the user ID is null, empty, or cannot be parsed into a valid {@code Long}
     */
    Long parseUserId(String userIdString, Mode mode);

    /**
     * Parses a string value into a {@code Long}, ensuring it is non-null, non-empty, and in valid numeric format.
     * <p>
     * This method trims the input string, checks for null or empty values,
     * and attempts to parse it into a {@code Long}.
     * If parsing fails due to an invalid numeric format, an exception is thrown with a descriptive error message.
     *
     * @param value the string value to parse
     * @return the parsed {@code Long} value
     * @throws IllegalArgumentException if the input value is null, empty,
     *                                  or cannot be parsed into a valid {@code Long}
     */
    Long parseLong(String value);

    /**
     * Validates a JSON string and maps it to a specified DTO object.
     * <p>
     * This method parses the input JSON string, ensures all required fields are present,
     * performs additional field value validations based on the specified mode, and maps
     * the JSON to an instance of the provided DTO class. If the JSON format is invalid or
     * validation fails, an exception is thrown.
     *
     * @param json     the JSON string to validate
     * @param mode     the mode specifying the type of validation to perform
     * @param dtoClass the class of the DTO object to map the JSON to ({@link TransactionDto}, {@link GoalDto})
     * @param <T>      the type of the DTO object
     * @return the validated DTO object of the specified type
     */
    <T> T validateJson(String json, Mode mode, Class<T> dtoClass);
}