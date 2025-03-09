package com.demo.finance.in.cli.command;

import com.demo.finance.domain.utils.MaxRetriesReachedException;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.CommandContext;

import java.util.Scanner;

/**
 * Command class for handling budget-related actions such as setting and viewing the monthly budget.
 */
public class BudgetCommand {

    private final CommandContext context;
    private final ValidationUtils validationUtils;
    private final Scanner scanner;

    /**
     * Initializes the BudgetCommand with the provided context, validation utilities,
     * and scanner.
     *
     * @param context The CommandContext that holds controllers.
     * @param validationUtils Utility for validation.
     * @param scanner Scanner to capture user input.
     */
    public BudgetCommand(CommandContext context, ValidationUtils validationUtils, Scanner scanner) {
        this.context = context;
        this.validationUtils = validationUtils;
        this.scanner = scanner;
    }

    /**
     * Prompts the user to enter a monthly budget amount and attempts to set the budget for the current user.
     * If successful, a confirmation message is displayed.
     * If the budget setting fails, an error message is shown.
     */
    public void setBudget() {
        try {
            String message = "Enter Monthly Budget: ";
            double amount = validationUtils.promptForPositiveDouble(message, scanner);
            if (context.getBudgetController().setBudget(context.getCurrentUser().getUserId(), amount)) {
                System.out.println("Budget set successfully.");
            } else {
                System.out.println("Failed to set budget.");
            }
        } catch (MaxRetriesReachedException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Displays the current budget status for the logged-in user.
     * The budget details are retrieved from the BudgetController.
     */
    public void viewBudget() {
        Long userId = context.getCurrentUser().getUserId();
        String budgetStatus = context.getBudgetController().viewBudget(userId);
        System.out.println(budgetStatus);
    }
}