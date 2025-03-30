package com.demo.finance.domain.utils.impl;

import com.demo.finance.domain.dto.*;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.PaginationParams;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The {@code ValidationUtilsImpl} class implements the {@link ValidationUtils} interface
 * and provides concrete implementations for validating various JSON inputs and parameters.
 * It ensures that input data adheres to expected formats and constraints, throwing exceptions
 * when validation fails.
 * <p>
 * This class uses reflection and predefined rules to validate required fields and specific
 * constraints for different modes of operation. It supports validation for user data, transactions,
 * goals, budgets, pagination parameters, and more.
 */
@Component
public class ValidationUtilsImpl implements ValidationUtils {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$");

    private static final Map<Mode, List<String>> REQUIRED_FIELDS_MAP = new HashMap<>();

    static {
        REQUIRED_FIELDS_MAP.put(Mode.TRANSACTION_CREATE, List.of("amount", "category", "date", "description", "type"));
        REQUIRED_FIELDS_MAP.put(Mode.TRANSACTION_UPDATE, List.of("amount", "category", "description"));
        REQUIRED_FIELDS_MAP.put(Mode.GOAL_CREATE, List.of("goalName", "targetAmount", "duration", "startTime"));
        REQUIRED_FIELDS_MAP.put(Mode.GOAL_UPDATE, List.of("goalName", "targetAmount", "duration"));
        REQUIRED_FIELDS_MAP.put(Mode.REGISTER_USER, List.of("name", "email", "password"));
        REQUIRED_FIELDS_MAP.put(Mode.UPDATE_USER, List.of("name", "email", "password"));
        REQUIRED_FIELDS_MAP.put(Mode.AUTHENTICATE, List.of("email", "password"));
        REQUIRED_FIELDS_MAP.put(Mode.UPDATE_ROLE, List.of("role"));
        REQUIRED_FIELDS_MAP.put(Mode.BLOCK_UNBLOCK, List.of("blocked"));
        REQUIRED_FIELDS_MAP.put(Mode.REPORT, List.of("fromDate", "toDate"));
        REQUIRED_FIELDS_MAP.put(Mode.BUDGET, List.of("monthlyLimit"));
        REQUIRED_FIELDS_MAP.put(Mode.PAGE, List.of("page", "size"));
    }

    /**
     * Validates the given object based on the specified mode.
     * <p>
     * This method performs a series of validations depending on the type of the object and the mode.
     * It checks for required fields, validates specific constraints for each DTO type, and throws
     * a {@link ValidationException} if any validation fails.
     *
     * @param <T>    the type of the object to validate
     * @param object the object to validate
     * @param mode   the mode specifying the validation rules
     * @return the validated object if all validations pass
     * @throws ValidationException if any validation fails
     */
    @Override
    public <T> T validateRequest(T object, Mode mode) {
        try {
            validateRequiredFields(object, mode);
            if (object instanceof UserDto) {
                validateUserFields((UserDto) object, mode);
            } else if (object instanceof TransactionDto) {
                validateTransactionFields((TransactionDto) object, mode);
            } else if (object instanceof GoalDto) {
                validateGoalFields((GoalDto) object, mode);
            } else if (object instanceof BudgetDto) {
                validateBudgetFields((BudgetDto) object);
            } else if (object instanceof ReportDatesDto) {
                validateReportDatesFields((ReportDatesDto) object);
            } else if (object instanceof PaginationParams) {
                validateParamsValues((PaginationParams) object);
            }
            return object;
        } catch (Exception e) {
            throw new ValidationException("Validation error: " + e.getMessage());
        }
    }

    /**
     * Parses and validates a user ID string.
     * <p>
     * This method ensures that the user ID is a positive integer and adheres to mode-specific constraints.
     * For example, certain operations like deletion or role updates are restricted for the default admin user.
     *
     * @param userIdString the string representation of the user ID
     * @param mode         the mode specifying additional constraints for the user ID
     * @return the parsed user ID as a {@code Long}
     * @throws ValidationException if the user ID is invalid or violates mode-specific constraints
     */
    @Override
    public Long parseUserId(String userIdString, Mode mode) {
        if (userIdString == null || !userIdString.matches("\\d+")) {
            throw new IllegalArgumentException("Invalid user ID format. User ID must be a positive integer.");
        }
        Long userId = parseLong(userIdString);
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
     * Parses a string value into a {@code Long}, ensuring it is non-null, non-empty, and in valid numeric format.
     * <p>
     * This method trims the input string, checks for null or empty values, and attempts to parse it into a
     * {@code Long}. If parsing fails due to an invalid numeric format, an exception is thrown with a descriptive
     * error message.
     *
     * @param value the string value to parse
     * @return the parsed {@code Long} value
     * @throws IllegalArgumentException if the input value is null, empty, or cannot be parsed into a valid {@code Long}
     */
    @Override
    public Long parseLong(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("Id cannot be null or empty.");
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid numeric format for id: " + value);
        }
    }

    /**
     * Validates the required fields of the given DTO based on the specified mode.
     * <p>
     * This method retrieves the list of required fields for the given mode and checks each field using
     * the {@link #checkField(Object, String)} method.
     *
     * @param dto  the DTO object to validate
     * @param mode the mode specifying the required fields
     * @throws ValidationException if any required field is missing or invalid
     */
    private void validateRequiredFields(Object dto, Mode mode) {
        List<String> requiredFields = REQUIRED_FIELDS_MAP.getOrDefault(mode, List.of());
        for (String field : requiredFields) {
            checkField(dto, field);
        }
    }

    /**
     * Checks the existence and validity of a specific field in the given object using reflection.
     * <p>
     * This method dynamically invokes the getter method for the specified field and ensures that its value
     * is not null. If the field is missing or inaccessible, a {@link ValidationException} is thrown.
     *
     * @param object    the object containing the field to check
     * @param fieldName the name of the field to validate
     * @throws ValidationException if the field is missing, inaccessible, or has a null value
     */
    private void checkField(Object object, String fieldName) {
        try {
            if (object instanceof UserDto && "blocked".equals(fieldName)) {
                Method method = object.getClass().getMethod("isBlocked");
                Object value = method.invoke(object);
                if (value == null) {
                    throw new ValidationException("Missing required field: blocked");
                }
                return;
            }
            boolean isRecord = object.getClass().isRecord();
            String methodName = isRecord ? fieldName
                    : "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            Method method;
            try {
                method = object.getClass().getMethod(methodName);
            } catch (NoSuchMethodException e) {
                throw new ValidationException("Field not found in DTO: " + fieldName);
            }
            Object value = method.invoke(object);
            if (value == null) {
                throw new ValidationException("Missing required field: " + fieldName);
            }
        } catch (Exception e) {
            if (e instanceof ValidationException) {
                throw (ValidationException) e;
            }
            throw new ValidationException("Cannot access field: " + fieldName);
        }
    }

    /**
     * Validates pagination parameters to ensure they meet the required constraints.
     * <p>
     * This method checks that the page number and size are positive integers and that the size does not exceed 100.
     *
     * @param params the pagination parameters to validate
     * @throws ValidationException if the page number or size is invalid
     */
    private void validateParamsValues(PaginationParams params) {
        try {
            if (params.page() < 1) {
                throw new ValidationException("Page must be positive integer: " + params.page());
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid page number.");
        }
        try {
            if (params.size() < 1) {
                throw new ValidationException("Size must be positive integer: " + params.size());
            }
            if (params.size() > 100) {
                throw new ValidationException("Size cannot exceed 100.");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid size number.");
        }
    }

    /**
     * Validates user-related fields based on the specified mode.
     * <p>
     * This method ensures that email, password, and name fields are valid and non-empty where applicable.
     * Additional constraints are applied for role updates and authentication modes.
     *
     * @param dto  the user DTO to validate
     * @param mode the mode specifying the validation rules
     * @throws ValidationException if any user-related field is invalid
     */
    private void validateUserFields(UserDto dto, Mode mode) {
        if (mode != Mode.UPDATE_ROLE && mode != Mode.BLOCK_UNBLOCK && !isValidEmail(dto.getEmail())) {
            throw new ValidationException("Invalid email format.");
        }
        if (mode != Mode.UPDATE_ROLE && mode != Mode.BLOCK_UNBLOCK && isBlank(dto.getPassword())) {
            throw new ValidationException("Password cannot be empty.");
        }
        if (mode != Mode.AUTHENTICATE && mode != Mode.UPDATE_ROLE && mode != Mode.BLOCK_UNBLOCK
                && isBlank(dto.getName())) {
            throw new ValidationException("Name cannot be empty.");
        }
        if (mode == Mode.UPDATE_ROLE && (dto.getRole() == null || isBlank(dto.getRole().getName()))) {
            throw new ValidationException("Role cannot be empty.");
        }
    }

    /**
     * Validates transaction-related fields based on the specified mode.
     * <p>
     * This method ensures that the amount, category, description, and type fields are valid and non-empty
     * where applicable. Additional constraints are applied for transaction creation mode.
     *
     * @param dto  the transaction DTO to validate
     * @param mode the mode specifying the validation rules
     * @throws ValidationException if any transaction-related field is invalid
     */
    private void validateTransactionFields(TransactionDto dto, Mode mode) {
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Amount must be a positive number.");
        }
        if (mode == Mode.TRANSACTION_CREATE && dto.getDate() == null) {
            throw new ValidationException("Transaction date cannot be null.");
        }
        if (isBlank(dto.getCategory())) {
            throw new ValidationException("Category cannot be empty.");
        }
        if (isBlank(dto.getDescription())) {
            throw new ValidationException("Description cannot be empty.");
        }
        if (mode == Mode.TRANSACTION_CREATE && !isValidType(dto.getType())) {
            throw new ValidationException("Type must be either INCOME or EXPENSE.");
        }
    }

    /**
     * Validates goal-related fields based on the specified mode.
     * <p>
     * This method ensures that the goal name, target amount, duration, and start time fields are valid
     * and non-empty where applicable. Additional constraints are applied for goal creation mode.
     *
     * @param dto  the goal DTO to validate
     * @param mode the mode specifying the validation rules
     * @throws ValidationException if any goal-related field is invalid
     */
    private void validateGoalFields(GoalDto dto, Mode mode) {
        if (isBlank(dto.getGoalName())) {
            throw new ValidationException("Goal name cannot be empty.");
        }
        if (dto.getTargetAmount() == null || dto.getTargetAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Target amount must be a positive number.");
        }
        if (dto.getDuration() == null || dto.getDuration() < 1) {
            throw new ValidationException("Duration must be a positive integer.");
        }
        if (mode == Mode.GOAL_CREATE && dto.getStartTime() == null) {
            throw new ValidationException("Start time cannot be empty.");
        }
    }

    /**
     * Validates budget-related fields.
     * <p>
     * This method ensures that the monthly limit field is a positive number.
     *
     * @param dto the budget DTO to validate
     * @throws ValidationException if the monthly limit is invalid
     */
    private void validateBudgetFields(BudgetDto dto) {
        if (dto.getMonthlyLimit() == null || dto.getMonthlyLimit().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Monthly limit must be a positive number.");
        }
    }

    /**
     * Validates report date-related fields.
     * <p>
     * This method ensures that both the from date and to date fields are non-null and that the to date
     * is not before the from date.
     *
     * @param dto the report dates DTO to validate
     * @throws ValidationException if the date range is invalid
     */
    private void validateReportDatesFields(ReportDatesDto dto) {
        if (dto.getFromDate() == null) {
            throw new ValidationException("From date cannot be null.");
        }
        if (dto.getToDate() == null) {
            throw new ValidationException("To date cannot be null.");
        }
        if (dto.getToDate().isBefore(dto.getFromDate())) {
            throw new ValidationException("To date cannot be before from date.");
        }
    }

    /**
     * Checks whether a given string is blank (null or empty after trimming).
     *
     * @param str the string to check
     * @return {@code true} if the string is blank, {@code false} otherwise
     */
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Validates whether the provided email string conforms to a valid email format.
     * <p>
     * This method uses a predefined regular expression pattern to perform the validation.
     *
     * @param email the email string to validate
     * @return {@code true} if the email matches the valid format, {@code false} otherwise
     */
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates whether the provided type string is either "INCOME" or "EXPENSE".
     *
     * @param type the type string to validate
     * @return {@code true} if the type is valid, {@code false} otherwise
     */
    private boolean isValidType(String type) {
        return "INCOME".equalsIgnoreCase(type) || "EXPENSE".equalsIgnoreCase(type);
    }
}