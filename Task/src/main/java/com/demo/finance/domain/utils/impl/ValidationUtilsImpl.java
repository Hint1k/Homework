package com.demo.finance.domain.utils.impl;

import com.demo.finance.domain.dto.*;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.PaginationParams;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.ValidationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The {@code ValidationUtilsImpl} class implements the {@link ValidationUtils} interface
 * and provides concrete implementations for validating various JSON inputs and parameters.
 * It ensures that input data adheres to expected formats and constraints, throwing exceptions
 * when validation fails.
 */
public class ValidationUtilsImpl implements ValidationUtils {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$");

    private final ObjectMapper objectMapper;

    /**
     * Constructs a new instance of {@code ValidationUtilsImpl} and initializes the {@link ObjectMapper}
     * with support for Java 8 date and time types via the {@link JavaTimeModule}.
     */
    public ValidationUtilsImpl() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Validates a JSON string representing a user and maps it to a {@link UserDto} object.
     *
     * @param json the JSON string to validate
     * @param mode the mode specifying the type of validation to perform
     * @return the validated {@link UserDto} object
     * @throws ValidationException if the JSON format is invalid or validation fails
     */
    @Override
    public UserDto validateUserJson(String json, Mode mode) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            validateRequiredFields(jsonNode, mode);
            UserDto userDto = objectMapper.readValue(json, UserDto.class);
            validateFieldValues(jsonNode, mode);
            return userDto;
        } catch (Exception e) {
            throw new ValidationException("Invalid JSON format or validation error: " + e.getMessage());
        }
    }

    /**
     * Validates a JSON string representing a user, maps it to a {@link UserDto} object,
     * and associates it with the provided user ID.
     *
     * @param json   the JSON string to validate
     * @param mode   the mode specifying the type of validation to perform
     * @param userId the string representation of the user ID
     * @return the validated {@link UserDto} object with the associated user ID
     * @throws ValidationException if the JSON format is invalid or validation fails
     */
    @Override
    public UserDto validateUserJson(String json, Mode mode, String userId) {
        Long parsedUserId = parseUserId(userId, mode);
        UserDto userDto = validateUserJson(json, mode);
        userDto.setUserId(parsedUserId);
        return userDto;
    }

    /**
     * Validates a JSON string representing a user, maps it to a {@link UserDto} object,
     * and ensures the user ID in the JSON matches the provided user ID.
     *
     * @param json   the JSON string to validate
     * @param mode   the mode specifying the type of validation to perform
     * @param userId the user ID to match
     * @return the validated {@link UserDto} object
     * @throws ValidationException if the user IDs do not match or validation fails
     */
    @Override
    public UserDto validateUserJson(String json, Mode mode, Long userId) {
        UserDto userDto = validateUserJson(json, mode);
        Long userIdJson = userDto.getUserId();
        if (userIdJson.equals(userId)) {
            return userDto;
        } else {
            throw new ValidationException("A user can't update other users");
        }
    }

    /**
     * Parses and validates a user ID string.
     *
     * @param userIdString the string representation of the user ID
     * @param mode         the mode specifying additional constraints for the user ID
     * @return the parsed user ID as a {@code Long}
     * @throws ValidationException if the user ID is invalid or violates mode-specific constraints
     */
    @Override
    public Long parseUserId(String userIdString, Mode mode) {
        Long userId = parseLong(userIdString, "Invalid userId format");
        if (userId == 1 && mode == Mode.DELETE) {
            throw new ValidationException("Default Admin cannot be deleted");
        }
        if (userId == 1 && mode == Mode.UPDATE_ROLE) {
            throw new ValidationException("Default Admin role cannot be changed");
        }
        if (userId == 1 && mode == Mode.BLOCK_UNBLOCK) {
            throw new ValidationException("Default Admin cannot be blocked or unblocked");
        }
        return userId;
    }

    /**
     * Validates pagination parameters for paginated requests.
     *
     * @param page the string representation of the page number
     * @param size the string representation of the page size
     * @return a {@link PaginationParams} object containing the validated page and size values
     * @throws IllegalArgumentException if the page or size format is invalid or exceeds constraints
     */
    @Override
    public PaginationParams validatePaginationParams(String page, String size) {
        int parsedPage = parseInt(page, "Invalid page format: must be an integer.");
        int parsedSize = parseInt(size, "Invalid size format: must be an integer.");
        if (parsedSize > 100) {
            throw new IllegalArgumentException("Size cannot exceed 100.");
        }
        return new PaginationParams(parsedPage, parsedSize);
    }

    /**
     * Validates a JSON string representing a report request and extracts date ranges.
     *
     * @param json   the JSON string to validate
     * @param mode   the mode specifying the type of validation to perform
     * @param userId the user ID associated with the report
     * @return a {@link Map} containing the validated "fromDate" and "toDate" as {@link LocalDate} objects
     * @throws ValidationException if the JSON format is invalid or validation fails
     */
    @Override
    public Map<String, LocalDate> validateReport(String json, Mode mode, Long userId) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            validateRequiredFields(jsonNode, mode);
            validateFieldValues(jsonNode, mode);
            LocalDate fromDate = LocalDate.parse(jsonNode.get("fromDate").asText());
            LocalDate toDate = LocalDate.parse(jsonNode.get("toDate").asText());
            if (toDate.isAfter(fromDate)) {
                return Map.of("fromDate", fromDate, "toDate", toDate);
            } else {
                throw new ValidationException("FromDate cannot be after ToDate date");
            }
        } catch (Exception e) {
            throw new ValidationException("Invalid JSON format or validation error: " + e.getMessage());
        }
    }

    /**
     * Validates a JSON string representing a budget request and extracts the monthly limit.
     *
     * @param json   the JSON string to validate
     * @param mode   the mode specifying the type of validation to perform
     * @param userId the user ID associated with the budget
     * @return the validated monthly limit as a {@link BigDecimal}
     * @throws ValidationException if the JSON format is invalid or validation fails
     */
    @Override
    public BigDecimal validateBudgetJson(String json, Mode mode, Long userId) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            validateRequiredFields(jsonNode, mode);
            validateFieldValues(jsonNode, mode);
            return BigDecimal.valueOf(jsonNode.get("monthlyLimit").asDouble());
        } catch (Exception e) {
            throw new ValidationException("Invalid JSON format or validation error: " + e.getMessage());
        }
    }

    /**
     * Validates a JSON string representing a transaction and maps it to a {@link TransactionDto} object.
     *
     * @param json the JSON string to validate
     * @param mode the mode specifying the type of validation to perform
     * @return the validated {@link TransactionDto} object
     * @throws ValidationException if the JSON format is invalid or validation fails
     */
    @Override
    public TransactionDto validateTransactionJson(String json, Mode mode) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            validateRequiredFields(jsonNode, mode);
            TransactionDto transactionDto = objectMapper.readValue(json, TransactionDto.class);
            validateFieldValues(jsonNode, mode);
            return transactionDto;
        } catch (Exception e) {
            throw new ValidationException("Invalid JSON format or validation error: " + e.getMessage());
        }
    }

    /**
     * Validates a JSON string representing a transaction, maps it to a {@link TransactionDto} object,
     * and associates it with the provided transaction ID.
     *
     * @param json          the JSON string to validate
     * @param mode          the mode specifying the type of validation to perform
     * @param transactionId the string representation of the transaction ID
     * @return the validated {@link TransactionDto} object with the associated transaction ID
     * @throws ValidationException if the JSON format is invalid or validation fails
     */
    @Override
    public TransactionDto validateTransactionJson(String json, Mode mode, String transactionId) {
        Long parsedTransactionId = parseTransactionId(transactionId, mode);
        TransactionDto transactionDto = validateTransactionJson(json, mode);
        transactionDto.setTransactionId(parsedTransactionId);
        return transactionDto;
    }

    /**
     * Validates a JSON string representing a goal and maps it to a {@link GoalDto} object.
     *
     * @param json the JSON string to validate
     * @param mode the mode specifying the type of validation to perform
     * @return the validated {@link GoalDto} object
     * @throws ValidationException if the JSON format is invalid or validation fails
     */
    @Override
    public GoalDto validateGoalJson(String json, Mode mode) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            validateRequiredFields(jsonNode, mode);
            GoalDto goalDto = objectMapper.readValue(json, GoalDto.class);
            validateFieldValues(jsonNode, mode);
            return goalDto;
        } catch (Exception e) {
            throw new ValidationException("Invalid JSON format or validation error: " + e.getMessage());
        }
    }

    /**
     * Validates a JSON string representing a goal, maps it to a {@link GoalDto} object,
     * and associates it with the provided goal ID.
     *
     * @param json   the JSON string to validate
     * @param mode   the mode specifying the type of validation to perform
     * @param goalId the string representation of the goal ID
     * @return the validated {@link GoalDto} object with the associated goal ID
     * @throws ValidationException if the JSON format is invalid or validation fails
     */
    @Override
    public GoalDto validateGoalJson(String json, Mode mode, String goalId) {
        Long parsedGoalId = parseGoalId(goalId, mode);
        GoalDto goalDto = validateGoalJson(json, mode);
        goalDto.setGoalId(parsedGoalId);
        return goalDto;
    }

    /**
     * Parses and validates a transaction ID string.
     *
     * @param transactionIdString the string representation of the transaction ID
     * @param mode                the mode specifying additional constraints for the transaction ID
     * @return the parsed transaction ID as a {@code Long}
     * @throws IllegalArgumentException if the transaction ID format is invalid
     */
    @Override
    public Long parseTransactionId(String transactionIdString, Mode mode) {
        try {
            return Long.parseLong(transactionIdString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format of transaction ID: " + transactionIdString);
        }
    }

    /**
     * Parses and validates a goal ID string.
     *
     * @param goalIdString the string representation of the goal ID
     * @param mode         the mode specifying additional constraints for the goal ID
     * @return the parsed goal ID as a {@code Long}
     * @throws IllegalArgumentException if the goal ID format is invalid
     */
    @Override
    public Long parseGoalId(String goalIdString, Mode mode) {
        try {
            return Long.parseLong(goalIdString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format of goal ID: " + goalIdString);
        }
    }

    /**
     * Validates the presence of required fields in the JSON node based on the specified mode.
     *
     * @param jsonNode the JSON node to validate
     * @param mode     the mode specifying the required fields to check
     * @throws ValidationException if any required field is missing
     */
    private void validateRequiredFields(JsonNode jsonNode, Mode mode) {
        switch (mode) {
            case TRANSACTION_CREATE:
                checkField(jsonNode, "userId");
                checkField(jsonNode, "amount");
                checkField(jsonNode, "category");
                checkField(jsonNode, "date");
                checkField(jsonNode, "description");
                checkField(jsonNode, "type");
                break;
            case TRANSACTION_UPDATE:
                checkField(jsonNode, "userId");
                checkField(jsonNode, "amount");
                checkField(jsonNode, "category");
                checkField(jsonNode, "description");
                break;
            case TRANSACTION_DELETE:
                checkField(jsonNode, "transactionId");
                checkField(jsonNode, "userId");
                break;
            case GOAL_CREATE:
                checkField(jsonNode, "userId");
                checkField(jsonNode, "goalName");
                checkField(jsonNode, "targetAmount");
                checkField(jsonNode, "duration");
                checkField(jsonNode, "startTime");
                break;
            case GOAL_UPDATE:
                checkField(jsonNode, "userId");
                checkField(jsonNode, "goalName");
                checkField(jsonNode, "targetAmount");
                checkField(jsonNode, "duration");
                break;
            case GOAL_DELETE:
                checkField(jsonNode, "goalId");
                checkField(jsonNode, "userId");
                break;
            case UPDATE:
                checkField(jsonNode, "userId");
                checkField(jsonNode, "name");
                checkField(jsonNode, "email");
                checkField(jsonNode, "password");
            case REGISTER:
                checkField(jsonNode, "name");
                checkField(jsonNode, "email");
                checkField(jsonNode, "password");
                break;
            case AUTHENTICATE:
                checkField(jsonNode, "email");
                checkField(jsonNode, "password");
                break;
            case UPDATE_ROLE:
                checkField(jsonNode, "role");
                break;
            case BLOCK_UNBLOCK:
                checkField(jsonNode, "blocked");
                break;
            case REPORT:
                checkField(jsonNode, "fromDate");
                checkField(jsonNode, "toDate");
            case BUDGET:
                checkField(jsonNode, "monthlyLimit");
                break;
            default:
                break;
        }
    }

    /**
     * Validates the values of fields in the provided JSON node based on the specified mode.
     * Ensures that each field adheres to its expected format and constraints, throwing a
     * {@link ValidationException} if any validation fails. The following validations are performed:
     * <ul>
     *   <li>userId: Must be a non-null Long value.</li>
     *   <li>email: Must conform to a valid email format.</li>
     *   <li>password: Must not be empty or blank.</li>
     *   <li>fromDate and toDate: Must be valid dates in the correct format, and toDate must be after fromDate.</li>
     *   <li>blocked: Must be a boolean value.</li>
     *   <li>monthlyLimit, amount, targetAmount, savedAmount: Must be positive numeric values.</li>
     *   <li>date and startTime: Must be valid dates in the correct format.</li>
     *   <li>type: Must be either "INCOME" or "EXPENSE".</li>
     *   <li>duration: Must be a positive integer.</li>
     * </ul>
     *
     * @param jsonNode the JSON node containing the fields to validate
     * @param mode     the mode specifying additional constraints for validation
     * @throws ValidationException if any field value fails validation
     */
    private void validateFieldValues(JsonNode jsonNode, Mode mode) {
        if (jsonNode.has("userId") && !jsonNode.get("userId").isIntegralNumber()) {
            throw new ValidationException("Invalid userId: must be a non-null Long.");
        }
        if (jsonNode.has("email") && !isValidEmail(jsonNode.get("email").asText())) {
            throw new ValidationException("Invalid email format.");
        }
        if (jsonNode.has("password")) {
            String password = jsonNode.get("password").asText();
            if (password.isBlank()) {
                throw new ValidationException("Password cannot be empty.");
            }
        }
        if (jsonNode.has("fromDate")) {
            try {
                LocalDate.parse(jsonNode.get("fromDate").asText());
            } catch (DateTimeParseException e) {
                throw new ValidationException("Invalid date format.");
            }
        }
        if (jsonNode.has("toDate")) {
            try {
                LocalDate.parse(jsonNode.get("toDate").asText());
            } catch (DateTimeParseException e) {
                throw new ValidationException("Invalid date format.");
            }
        }
        if (jsonNode.has("blocked") && !jsonNode.get("blocked").isBoolean()) {
            throw new ValidationException("Blocked field must be a boolean.");
        }
        if (jsonNode.has("monthlyLimit")) {
            BigDecimal amount = new BigDecimal(jsonNode.get("monthlyLimit").asText());
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Amount must be positive.");
            }
        }
        if (jsonNode.has("amount")) {
            BigDecimal amount = new BigDecimal(jsonNode.get("amount").asText());
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Amount must be positive.");
            }
        }
        if (jsonNode.has("date")) {
            try {
                LocalDate.parse(jsonNode.get("date").asText());
            } catch (DateTimeParseException e) {
                throw new ValidationException("Invalid date format.");
            }
        }
        if (jsonNode.has("type")) {
            String type = jsonNode.get("type").asText();
            if (!type.equalsIgnoreCase("INCOME") && !type.equalsIgnoreCase("EXPENSE")) {
                throw new ValidationException("Type must be either INCOME or EXPENSE.");
            }
        }
        if (jsonNode.has("targetAmount")) {
            BigDecimal targetAmount = new BigDecimal(jsonNode.get("targetAmount").asText());
            if (targetAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Target amount must be positive.");
            }
        }
        if (jsonNode.has("savedAmount")) {
            BigDecimal savedAmount = new BigDecimal(jsonNode.get("savedAmount").asText());
            if (savedAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Saved amount must be positive.");
            }
        }
        if (jsonNode.has("duration")) {
            int duration = jsonNode.get("duration").asInt();
            if (duration <= 0) {
                throw new ValidationException("Duration must be a positive integer.");
            }
        }
        if (jsonNode.has("startTime")) {
            try {
                LocalDate.parse(jsonNode.get("startTime").asText());
            } catch (DateTimeParseException e) {
                throw new ValidationException("Invalid start time format.");
            }
        }
    }

    /**
     * Checks if the specified field exists and is non-null in the provided JSON node.
     * Throws a {@link ValidationException} if the field is missing or null.
     *
     * @param jsonNode   the JSON node to check for the field
     * @param fieldName  the name of the field to validate
     * @throws ValidationException if the field is missing or null in the JSON node
     */
    private void checkField(JsonNode jsonNode, String fieldName) {
        if (!jsonNode.hasNonNull(fieldName)) {
            throw new ValidationException("Missing required field: " + fieldName);
        }
    }

    /**
     * Validates whether the provided email string conforms to a valid email format.
     * Uses a predefined regular expression pattern to perform the validation.
     *
     * @param email the email string to validate
     * @return {@code true} if the email matches the valid format, {@code false} otherwise
     */
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Parses a string value into a {@code Long}, throwing an exception with a custom error message
     * if the parsing fails due to an invalid format.
     *
     * @param value        the string value to parse
     * @param errorMessage the error message to include in the exception if parsing fails
     * @return the parsed {@code Long} value
     * @throws IllegalArgumentException if the string value cannot be parsed into a {@code Long}
     */
    private Long parseLong(String value, String errorMessage) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Parses a string value into an {@code int}, throwing an exception with a custom error message
     * if the parsing fails due to an invalid format.
     *
     * @param value        the string value to parse
     * @param errorMessage the error message to include in the exception if parsing fails
     * @return the parsed {@code int} value
     * @throws IllegalArgumentException if the string value cannot be parsed into an {@code int}
     */
    private int parseInt(String value, String errorMessage) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}