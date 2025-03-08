package com.demo.finance.domain.utils;

import java.time.LocalDate;
import java.util.Scanner;

public interface ValidationUtils {

    Double promptForPositiveDouble(String message, Scanner scanner);

    Double promptForOptionalPositiveDouble(String message, Scanner scanner);

    Long promptForPositiveLong(String message, Scanner scanner);

    String promptForNonEmptyString(String message, Scanner scanner);

    String promptForOptionalString(String message, Scanner scanner);

    String promptForValidEmail(String message, Scanner scanner);

    String promptForOptionalEmail(String message, Scanner scanner);

    String promptForValidPassword(String message, Scanner scanner);

    String promptForOptionalPassword(String message, Scanner scanner);

    LocalDate promptForValidDate(String message, Scanner scanner);

    LocalDate promptForOptionalDate(String message, Scanner scanner);

    Type promptForTransactionType(Scanner scanner);

    Type promptForOptionalTransactionType(Scanner scanner);

    Integer promptForIntInRange(String message, Integer min, Integer max, Scanner scanner);

    Integer promptForPositiveInt(String message, Scanner scanner);

    Integer promptForOptionalPositiveInt(String message, Scanner scanner);
}