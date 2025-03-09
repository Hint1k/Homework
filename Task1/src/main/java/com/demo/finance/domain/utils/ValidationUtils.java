package com.demo.finance.domain.utils;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * Utility interface for validating and prompting various user inputs.
 * It provides methods to ensure valid input from the user for various data types and conditions.
 */
public interface ValidationUtils {

    /**
     * Prompts the user for a positive double value.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A positive double entered by the user.
     */
    Double promptForPositiveDouble(String message, Scanner scanner);

    /**
     * Prompts the user for an optional positive double value.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A positive double entered by the user, or null if no input is provided.
     */
    Double promptForOptionalPositiveDouble(String message, Scanner scanner);

    /**
     * Prompts the user for a positive long value.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A positive long entered by the user.
     */
    Long promptForPositiveLong(String message, Scanner scanner);

    /**
     * Prompts the user for a non-empty string.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A non-empty string entered by the user.
     */
    String promptForNonEmptyString(String message, Scanner scanner);

    /**
     * Prompts the user for an optional string.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A string entered by the user, or null if no input is provided.
     */
    String promptForOptionalString(String message, Scanner scanner);

    /**
     * Prompts the user for a valid email address.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A valid email entered by the user.
     */
    String promptForValidEmail(String message, Scanner scanner);

    /**
     * Prompts the user for an optional email address.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A valid email entered by the user, or null if no input is provided.
     */
    String promptForOptionalEmail(String message, Scanner scanner);

    /**
     * Prompts the user for a valid password.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A valid password entered by the user.
     */
    String promptForValidPassword(String message, Scanner scanner);

    /**
     * Prompts the user for an optional password.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A valid password entered by the user, or null if no input is provided.
     */
    String promptForOptionalPassword(String message, Scanner scanner);

    /**
     * Prompts the user for a valid date.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A valid date entered by the user.
     */
    LocalDate promptForValidDate(String message, Scanner scanner);

    /**
     * Prompts the user for an optional date.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A valid date entered by the user, or null if no input is provided.
     */
    LocalDate promptForOptionalDate(String message, Scanner scanner);

    /**
     * Prompts the user for a valid transaction type (INCOME or EXPENSE).
     *
     * @param scanner The scanner used for user input.
     * @return A valid transaction type (INCOME or EXPENSE).
     */
    Type promptForTransactionType(Scanner scanner);

    /**
     * Prompts the user for an optional transaction type (INCOME or EXPENSE).
     *
     * @param scanner The scanner used for user input.
     * @return A valid transaction type (INCOME or EXPENSE), or null if no input is provided.
     */
    Type promptForOptionalTransactionType(Scanner scanner);

    /**
     * Prompts the user for an integer value within a specific range.
     *
     * @param message The message to display when prompting the user.
     * @param min The minimum valid value.
     * @param max The maximum valid value.
     * @param scanner The scanner used for user input.
     * @return An integer within the specified range.
     */
    Integer promptForIntInRange(String message, Integer min, Integer max, Scanner scanner);

    /**
     * Prompts the user for a positive integer value.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A positive integer entered by the user.
     */
    Integer promptForPositiveInt(String message, Scanner scanner);

    /**
     * Prompts the user for an optional positive integer value.
     *
     * @param message The message to display when prompting the user.
     * @param scanner The scanner used for user input.
     * @return A positive integer entered by the user, or null if no input is provided.
     */
    Integer promptForOptionalPositiveInt(String message, Scanner scanner);
}