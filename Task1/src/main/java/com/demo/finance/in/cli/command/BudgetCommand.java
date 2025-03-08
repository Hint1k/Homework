package com.demo.finance.in.cli.command;

import com.demo.finance.domain.utils.MaxRetriesReachedException;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.CommandContext;

import java.util.Scanner;

public class BudgetCommand {

    private final CommandContext context;
    private final ValidationUtils validationUtils;
    private final Scanner scanner;

    public BudgetCommand(CommandContext context, ValidationUtils validationUtils, Scanner scanner) {
        this.context = context;
        this.validationUtils = validationUtils;
        this.scanner = scanner;
    }

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

    public void viewBudget() {
        Long userId = context.getCurrentUser().getUserId();
        String budgetStatus = context.getBudgetController().viewBudget(userId);
        System.out.println(budgetStatus);
    }
}