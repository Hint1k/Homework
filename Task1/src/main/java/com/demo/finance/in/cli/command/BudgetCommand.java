package com.demo.finance.in.cli.command;

import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.domain.model.Budget;

import java.util.Optional;
import java.util.Scanner;

public class BudgetCommand {
    private final CommandContext context;
    private final Scanner scanner;

    public BudgetCommand(CommandContext context, Scanner scanner) {
        this.context = context;
        this.scanner = scanner;
    }

    public void setBudget() {
        System.out.print("Enter Monthly Budget: ");
        double amount = Double.parseDouble(scanner.nextLine());

        if (context.getBudgetController().setBudget(context.getCurrentUser().getId(), amount)) {
            System.out.println("Budget set successfully.");
        } else {
            System.out.println("Failed to set budget.");
        }
    }

    public void viewBudget() {
        Optional<Budget> budget = context.getBudgetController().getBudget(context.getCurrentUser().getId());
        budget.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("No budget set for the user.")
        );
    }

    public void checkBudgetLimit() {
        System.out.print("Enter Transaction Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());

        if (context.getBudgetController().checkBudgetLimit(context.getCurrentUser().getId(), amount)) {
            System.out.println("⚠️ Budget limit will be exceeded!");
        } else {
            System.out.println("✅ Budget is under control.");
        }
    }
}