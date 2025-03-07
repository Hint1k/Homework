package com.demo.finance.in.cli.command;

import com.demo.finance.in.cli.CommandContext;

import java.util.Scanner;

public class BudgetCommand {
    private final CommandContext context;
    private final Scanner scanner;

    public BudgetCommand(CommandContext context, Scanner scanner) {
        this.context = context;
        this.scanner = scanner;
    }

    public void setBudget() {
        double amount = promptForPositiveDouble();
        if (context.getBudgetController().setBudget(context.getCurrentUser().getUserId(), amount)) {
            System.out.println("Budget set successfully.");
        } else {
            System.out.println("Failed to set budget.");
        }
    }

    public void viewBudget() {
        Long userId = context.getCurrentUser().getUserId();
        String budgetStatus = context.getBudgetController().viewBudget(userId);
        System.out.println(budgetStatus);
    }

    private double promptForPositiveDouble() {
        while (true) {
            try {
                System.out.print("Enter Monthly Budget: ");
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value > 0) return value;
                System.out.println("Error: Value must be positive.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }
}