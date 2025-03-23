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
     * Parses and validates a user ID string.
     *
     * @param userIdString the string representation of the user ID
     * @param mode         the mode specifying additional constraints for the user ID
     * @return the parsed user ID as a {@code Long}
     * @throws IllegalArgumentException if the user ID is invalid or violates mode-specific constraints
     */
    Long parseUserId(String userIdString, Mode mode);

    /**
     * Validates pagination parameters for paginated requests.
     *
     * @param page the string representation of the page number
     * @param size the string representation of the page size
     * @return a {@link PaginationParams} object containing the validated page and size values
     * @throws IllegalArgumentException if the page or size format is invalid or exceeds constraints
     */
    PaginationParams validatePaginationParams(String page, String size);

    /**
     * Validates a JSON string representing a report request and extracts date ranges.
     *
     * @param json   the JSON string to validate
     * @param mode   the mode specifying the type of validation to perform
     * @param userId the user ID associated with the report
     * @return a {@link Map} containing the validated "fromDate" and "toDate" as {@link LocalDate} objects
     * @throws IllegalArgumentException if the JSON format is invalid or validation fails
     */
    Map<String, LocalDate> validateReport(String json, Mode mode, Long userId);

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
     * Validates a JSON string representing a transaction and maps it to a {@link TransactionDto} object.
     *
     * @param json the JSON string to validate
     * @param mode the mode specifying the type of validation to perform
     * @return the validated {@link TransactionDto} object
     * @throws IllegalArgumentException if the JSON format is invalid or validation fails
     */
    TransactionDto validateTransactionJson(String json, Mode mode);

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
     * Validates a JSON string representing a goal and maps it to a {@link GoalDto} object.
     *
     * @param json the JSON string to validate
     * @param mode the mode specifying the type of validation to perform
     * @return the validated {@link GoalDto} object
     * @throws IllegalArgumentException if the JSON format is invalid or validation fails
     */
    GoalDto validateGoalJson(String json, Mode mode);

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
     * Parses and validates a transaction ID string.
     *
     * @param transactionIdString the string representation of the transaction ID
     * @param mode                the mode specifying additional constraints for the transaction ID
     * @return the parsed transaction ID as a {@code Long}
     * @throws IllegalArgumentException if the transaction ID format is invalid
     */
    Long parseTransactionId(String transactionIdString, Mode mode);

    /**
     * Parses and validates a goal ID string.
     *
     * @param goalIdString the string representation of the goal ID
     * @param mode         the mode specifying additional constraints for the goal ID
     * @return the parsed goal ID as a {@code Long}
     * @throws IllegalArgumentException if the goal ID format is invalid
     */
    Long parseGoalId(String goalIdString, Mode mode);
}