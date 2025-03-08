package com.demo.finance.domain.utils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.Optional;
import java.util.function.Function;

public class ValidationUtilsImpl implements ValidationUtils {

    private static final String ERROR_VALUE_MUST_BE_POSITIVE = "Error: Value must be positive.";
    private static final String ERROR_INVALID_INPUT_KEEPING_CURRENT = "Error: Invalid input. Keeping current value.";
    private static final String PATTERN = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$";
    private static final int MAX_RETRIES = 3;

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

    @Override
    public Double promptForPositiveDouble(String message, Scanner scanner) {
        return promptWithRetries(message, scanner, input -> {
            try {
                double value = Double.parseDouble(input);
                if (value > 0) return Optional.of(value);
            } catch (NumberFormatException e) {
                // Ignore and return empty
            }
            return Optional.empty();
        }, ERROR_VALUE_MUST_BE_POSITIVE);
    }

    @Override
    public Double promptForOptionalPositiveDouble(String message, Scanner scanner) {
        System.out.print(message);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return null;
        try {
            double value = Double.parseDouble(input);
            if (value > 0) return value;
        } catch (NumberFormatException e) {
            // Ignore and return null
        }
        System.out.println(ERROR_INVALID_INPUT_KEEPING_CURRENT);
        return null;
    }

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

    @Override
    public String promptForNonEmptyString(String message, Scanner scanner) {
        return promptWithRetries(message, scanner, input -> {
            if (!input.isEmpty()) return Optional.of(input);
            return Optional.empty();
        }, "Error: Input cannot be empty.");
    }

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

    @Override
    public String promptForValidEmail(String message, Scanner scanner) {
        Pattern emailPattern = Pattern.compile(PATTERN);
        return promptWithRetries(message, scanner, input -> {
            if (emailPattern.matcher(input).matches()) return Optional.of(input);
            return Optional.empty();
        }, "Error: Invalid email format. Please enter a valid email (e.g. user@demo.com).");
    }

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

    @Override
    public String promptForValidPassword(String message, Scanner scanner) {
        return promptWithRetries(message, scanner, input -> {
            if (input.length() >= 3) return Optional.of(input);
            return Optional.empty();
        }, "Error: Password must be at least 3 characters long.");
    }

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