package com.demo.finance.domain.utils.impl;

import com.demo.finance.domain.utils.Type;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.MaxRetriesReachedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementation of the {@link ValidationUtils} interface.
 * This class provides various utility methods for prompting the user for validated input.
 * It supports input validation with retry mechanisms and handling of optional values where applicable.
 */
public class ValidationUtilsImpl implements ValidationUtils {

    private static final String ERROR_VALUE_MUST_BE_POSITIVE = "Error: Value must be positive.";
    private static final String ERROR_INVALID_INPUT_KEEPING_CURRENT = "Error: Invalid input. Keeping current value.";
    private static final String PATTERN = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$";
    private static final int MAX_RETRIES = 3;

    /**
     * Prompts the user for input and retries a specified number of times if invalid input is provided.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @param parser A function that parses the input into a desired object, returning an Optional.
     * @param errorMessage The error message to show when input is invalid.
     * @param <T> The type of object to return after successful input parsing.
     * @return The successfully parsed object.
     * @throws MaxRetriesReachedException if the maximum number of retries is exceeded.
     */
    private <T> T promptWithRetries(String message, Scanner scanner, Function<String, Optional<T>> parser,
                                    String errorMessage) {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            Optional<T> result = parser.apply(input);
            if (result.isPresent()) {
                return result.get();
            }
            System.out.println(errorMessage);
            retries++;
        }
        throw new MaxRetriesReachedException("Maximum retries reached. Returning back to the menu.");
    }

    /**
     * Prompts the user for a positive BigDecimal value.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A positive BigDecimal entered by the user.
     */
    @Override
    public BigDecimal promptForPositiveBigDecimal(String message, Scanner scanner) {
        return promptWithRetries(message, scanner, input -> {
            try {
                BigDecimal value = new BigDecimal(input);
                if (value.compareTo(BigDecimal.ZERO) > 0) {
                    return Optional.of(value);
                }
            } catch (NumberFormatException e) {
                // Ignore and return empty
            }
            return Optional.empty();
        }, ERROR_VALUE_MUST_BE_POSITIVE);
    }

    /**
     * Prompts the user for an optional positive BigDecimal value. If no input is given, returns null.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A positive BigDecimal entered by the user, or null if no input is provided.
     */
    @Override
    public BigDecimal promptForOptionalPositiveBigDecimal(String message, Scanner scanner) {
        System.out.print(message);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return null;
        try {
            BigDecimal value = new BigDecimal(input);
            if (value.compareTo(BigDecimal.ZERO) > 0)
                return value;
        } catch (NumberFormatException e) {
            // Ignore and return null
        }
        System.out.println(ERROR_INVALID_INPUT_KEEPING_CURRENT);
        return null;
    }

    /**
     * Prompts the user for a positive long value.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A positive long entered by the user.
     */
    @Override
    public Long promptForPositiveLong(String message, Scanner scanner) {
        return promptWithRetries(message, scanner, input -> {
            try {
                long value = Long.parseLong(input);
                if (value > 0) return Optional.of(value);
            } catch (NumberFormatException e) {
                // Ignore and return empty
            }
            return Optional.empty();
        }, ERROR_VALUE_MUST_BE_POSITIVE);
    }

    /**
     * Prompts the user for a non-empty string value.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A non-empty string entered by the user.
     */
    @Override
    public String promptForNonEmptyString(String message, Scanner scanner) {
        return promptWithRetries(message, scanner, input -> {
            if (!input.isEmpty()) return Optional.of(input);
            return Optional.empty();
        }, "Error: Input cannot be empty.");
    }

    /**
     * Prompts the user for an optional string value. If no input is provided, returns null.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A string entered by the user, or null if no input is provided.
     */
    @Override
    public String promptForOptionalString(String message, Scanner scanner) {
        System.out.print(message);
        String string = scanner.nextLine().trim();
        if (!string.isEmpty()) {
            return string;
        } else {
            return null;
        }
    }

    /**
     * Prompts the user for a valid email address.
     * The email must match the regular expression for a valid email.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A valid email entered by the user.
     */
    @Override
    public String promptForValidEmail(String message, Scanner scanner) {
        Pattern emailPattern = Pattern.compile(PATTERN);
        return promptWithRetries(message, scanner, input -> {
            if (emailPattern.matcher(input).matches()) return Optional.of(input);
            return Optional.empty();
        }, "Error: Invalid email format. Please enter a valid email (e.g. user@demo.com).");
    }

    /**
     * Prompts the user for an optional email address. If no input is given, returns null.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A valid email entered by the user, or null if no input is provided.
     */
    @Override
    public String promptForOptionalEmail(String message, Scanner scanner) {
        System.out.println(message);
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) {
            Pattern emailPattern = Pattern.compile(PATTERN);
            if (emailPattern.matcher(email).matches()) {
                return email;
            } else {
                System.out.println("Error: Invalid email format. Keeping current email.");
                return null;
            }
        }
        return null;
    }

    /**
     * Prompts the user for a valid password. The password must be at least 3 characters long.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A valid password entered by the user.
     */
    @Override
    public String promptForValidPassword(String message, Scanner scanner) {
        return promptWithRetries(message, scanner, input -> {
            if (input.length() >= 3) return Optional.of(input);
            return Optional.empty();
        }, "Error: Password must be at least 3 characters long.");
    }

    /**
     * Prompts the user for an optional password. If no input is given, returns null.
     * If input is provided but invalid (too short), the old value is kept.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A valid password entered by the user, or null if no input is provided.
     */
    @Override
    public String promptForOptionalPassword(String message, Scanner scanner) {
        System.out.println(message);
        String password = scanner.nextLine().trim();
        if (!password.isEmpty()) {
            if (password.length() >= 3) {
                return password;
            } else {
                System.out.println("Error: Invalid password length. Keeping current password.");
                return null;
            }
        }
        return null;
    }

    /**
     * Prompts the user for a valid date in the format YYYY-MM-DD.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A valid date entered by the user.
     */
    @Override
    public LocalDate promptForValidDate(String message, Scanner scanner) {
        return promptWithRetries(message, scanner, input -> {
            try {
                return Optional.of(LocalDate.parse(input));
            } catch (DateTimeParseException e) {
                return Optional.empty();
            }
        }, "Error: Please enter a valid date in YYYY-MM-DD format.");
    }

    /**
     * Prompts the user for an optional date. If no input is given, returns null.
     * If an invalid date is entered, the old value is kept.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A valid date entered by the user, or null if no input is provided.
     */
    @Override
    public LocalDate promptForOptionalDate(String message, Scanner scanner) {
        System.out.print(message);
        String input = scanner.nextLine().trim();
        try {
            return input.isEmpty() ? null : LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date format. Skipping input.");
            return null;
        }
    }

    /**
     * Prompts the user to select a transaction type (INCOME or EXPENSE).
     *
     * @param scanner The scanner used for user input.
     * @return The selected transaction type.
     */
    @Override
    public Type promptForTransactionType(Scanner scanner) {
        return promptWithRetries("Input Type (i = INCOME / e = EXPENSE): ", scanner, input -> {
            if (input.equalsIgnoreCase("i") || input.equalsIgnoreCase("income")) {
                return Optional.of(Type.INCOME);
            }
            if (input.equalsIgnoreCase("e") || input.equalsIgnoreCase("expense")) {
                return Optional.of(Type.EXPENSE);
            }
            return Optional.empty();
        }, "Error: Please enter 'i' for INCOME, 'e' for EXPENSE");
    }

    /**
     * Prompts the user for an optional transaction type. If no input is given, returns null.
     * If an invalid input is provided, the old value is kept.
     *
     * @param scanner The scanner used for user input.
     * @return The selected transaction type, or null if no input is provided.
     */
    @Override
    public Type promptForOptionalTransactionType(Scanner scanner) {
        System.out.print("Input type (i = INCOME / e = EXPENSE) or leave empty: ");
        String input = scanner.nextLine().trim().toLowerCase();
        if (input.equalsIgnoreCase("i") || input.equalsIgnoreCase("income")) {
            return Type.INCOME;
        }
        if (input.equalsIgnoreCase("e") || input.equalsIgnoreCase("expense")) {
            return Type.EXPENSE;
        }
        System.out.println("Invalid input type. Keeping current type.");
        return null;
    }

    /**
     * Prompts the user for an integer value within a specified range.
     *
     * @param message The message to display when prompting the user.
     * @param min The minimum valid value.
     * @param max The maximum valid value.
     * @param scanner The scanner used for user input.
     * @return A valid integer within the specified range.
     */
    @Override
    public Integer promptForIntInRange(String message, Integer min, Integer max, Scanner scanner) {
        return promptWithRetries(message, scanner, input -> {
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) return Optional.of(value);
            } catch (NumberFormatException e) {
                // Ignore and return empty
            }
            return Optional.empty();
        }, "Error: Please enter a number between " + min + " and " + max + ".");
    }

    /**
     * Prompts the user for a positive integer value.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A positive integer entered by the user.
     */
    @Override
    public Integer promptForPositiveInt(String message, Scanner scanner) {
        return promptWithRetries(message, scanner, input -> {
            try {
                int value = Integer.parseInt(input);
                if (value > 0) return Optional.of(value);
            } catch (NumberFormatException e) {
                // Ignore and return empty
            }
            return Optional.empty();
        }, ERROR_VALUE_MUST_BE_POSITIVE);
    }

    /**
     * Prompts the user for an optional positive integer. If no input is given, returns null.
     * If an invalid input is provided, the old value is kept.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A positive integer entered by the user, or null if no input is provided.
     */
    @Override
    public Integer promptForOptionalPositiveInt(String message, Scanner scanner) {
        System.out.print(message);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return null;
        try {
            int value = Integer.parseInt(input);
            if (value > 0) {
                return value;
            } else {
                System.out.println("Error: Negative value. Keeping current value.");
            }
        } catch (NumberFormatException e) {
            System.out.println(ERROR_INVALID_INPUT_KEEPING_CURRENT);
        }
        return null;
    }
}