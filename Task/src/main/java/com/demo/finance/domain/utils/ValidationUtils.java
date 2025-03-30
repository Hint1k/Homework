package com.demo.finance.domain.utils;

/**
 * The {@code ValidationUtils} interface defines a contract for validating various JSON inputs and parameters
 * used in the application. It ensures that input data adheres to expected formats and constraints, providing
 * methods to validate user, transaction, goal, budget, and report-related data. This interface is designed
 * to be implemented by classes that perform detailed validation logic.
 */
public interface ValidationUtils {

    /**
     * Validates the given object based on the specified mode.
     * <p>
     * This method performs a series of validations depending on the type of the object and the mode.
     * It checks for required fields, validates specific constraints for each DTO type, and throws
     * an exception if any validation fails.
     *
     * @param <T>   the type of the object to validate
     * @param object the object to validate
     * @param mode   the mode specifying the validation rules
     * @return the validated object if all validations pass
     */
    <T> T validateRequest(T object, Mode mode);

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
}