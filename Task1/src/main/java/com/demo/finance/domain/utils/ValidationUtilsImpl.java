package com.demo.finance.domain.utils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ValidationUtilsImpl implements ValidationUtils {

    private static final String ERROR_VALUE_MUST_BE_POSITIVE = "Error: Value must be positive.";
    private static final String ERROR_INVALID_NUMBER = "Error: Please enter a valid number.";
    private static final String ERROR_INVALID_INPUT_KEEPING_CURRENT = "Error: Invalid input. Keeping current value.";
    private static final String PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    @Override
    public Double promptForPositiveDouble(String message, Scanner scanner) {
        while (true) {
            try {
                System.out.print(message);
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value > 0) return value;
                System.out.println(ERROR_VALUE_MUST_BE_POSITIVE);
            } catch (NumberFormatException e) {
                System.out.println(ERROR_INVALID_NUMBER);
            }
        }
    }

    @Override
    public Double promptForOptionalPositiveDouble(String message, Scanner scanner) {
        System.out.print(message);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return null;
        try {
            double value = Double.parseDouble(input);
            if (value > 0) {
                return value;
            } else {
                System.out.println(ERROR_INVALID_INPUT_KEEPING_CURRENT);
            }
        } catch (NumberFormatException e) {
            System.out.println(ERROR_INVALID_INPUT_KEEPING_CURRENT);
        }
        return null;
    }

    @Override
    public Long promptForPositiveLong(String message, Scanner scanner) {
        while (true) {
            try {
                System.out.print(message);
                long value = Long.parseLong(scanner.nextLine().trim());
                if (value > 0) return value;
                System.out.println(ERROR_VALUE_MUST_BE_POSITIVE);
            } catch (NumberFormatException e) {
                System.out.println(ERROR_INVALID_NUMBER);
            }
        }
    }

    @Override
    public String promptForNonEmptyString(String message, Scanner scanner) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("Error: Input cannot be empty.");
        }
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
        while (true) {
            System.out.print(message);
            String email = scanner.nextLine().trim();
            if (emailPattern.matcher(email).matches()) return email;
            System.out.println("Error: Invalid email format. Please enter a valid email (e.g. user@demo.com).");
        }
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
        while (true) {
            System.out.print(message);
            String password = scanner.nextLine().trim();
            if (password.length() >= 3) return password;
            System.out.println("Error: Password must be at least 3 characters Long.");
        }
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
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Error: Please enter a valid date in YYYY-MM-DD format.");
            }
        }
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
        while (true) {
            System.out.print("Input Type (i = INCOME / e = EXPENSE): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equalsIgnoreCase("i") || input.equalsIgnoreCase("income")) {
                return Type.INCOME;
            }
            if (input.equalsIgnoreCase("e") || input.equalsIgnoreCase("expense")) {
                return Type.EXPENSE;
            }
            System.out.println("Error: Please enter 'i' for INCOME, 'e' for EXPENSE");
        }
    }

    @Override
    public Type promptForOptionalTransactionType(Scanner scanner) {
        while (true) {
            System.out.print("Input type (i = INCOME / e = EXPENSE) or leave empty: ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equalsIgnoreCase("i") || input.equalsIgnoreCase("income")) {
                return Type.INCOME;
            }
            if (input.equalsIgnoreCase("e") || input.equalsIgnoreCase("expense")) {
                return Type.EXPENSE;
            }
            return null;
        }
    }

    @Override
    public Integer promptForIntInRange(String message, Integer min, Integer max, Scanner scanner) {
        while (true) {
            try {
                System.out.print(message);
                Integer value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) return value;
                System.out.println("Error: Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println(ERROR_INVALID_NUMBER);
            }
        }
    }

    @Override
    public Integer promptForPositiveInt(String message, Scanner scanner) {
        while (true) {
            try {
                System.out.print(message);
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value > 0) return value;
                System.out.println(ERROR_VALUE_MUST_BE_POSITIVE);
            } catch (NumberFormatException e) {
                System.out.println(ERROR_INVALID_NUMBER);
            }
        }
    }

    @Override
    public Integer promptForOptionalPositiveInt(String message, Scanner scanner) {
        System.out.print(message);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return null;
        try {
            int value = Integer.parseInt(input);
            if (value > 0) return value;
        } catch (NumberFormatException e) {
            System.out.println(ERROR_INVALID_INPUT_KEEPING_CURRENT);
        }
        return null;
    }
}