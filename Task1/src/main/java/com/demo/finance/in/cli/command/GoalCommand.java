package com.demo.finance.in.cli.command;

import com.demo.finance.in.cli.CommandContext;

import java.util.Scanner;

public class GoalCommand {
    private final CommandContext context;
    private final Scanner scanner;

    public GoalCommand(CommandContext context, Scanner scanner) {
        this.context = context;
        this.scanner = scanner;
    }

    public void createGoal() {
        System.out.print("Enter Goal Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Target Amount: ");
        double targetAmount = Double.parseDouble(scanner.nextLine());

        context.getGoalController().createGoal(context.getCurrentUser().getId(), name, targetAmount);
        System.out.println("Goal created successfully.");
    }

    public void viewGoals() {
        context.getGoalController().getAllGoals(context.getCurrentUser().getId())
                .forEach(System.out::println);
    }

    public void updateGoalProgress() {
        System.out.print("Enter Goal Name: ");
        String goalName = scanner.nextLine();
        System.out.print("Enter Amount Saved: ");
        double amount = Double.parseDouble(scanner.nextLine());

        context.getGoalController().updateGoalProgress(context.getCurrentUser().getId(), goalName, amount);
        System.out.println("Goal progress updated successfully.");
    }

    public void deleteGoal() {
        System.out.print("Enter Goal Name to Delete: ");
        String goalName = scanner.nextLine();
        if (context.getGoalController().getGoal(context.getCurrentUser().getId(), goalName).isPresent()) {
            context.getGoalController().getAllGoals(context.getCurrentUser().getId())
                    .removeIf(goal -> goal.getGoalName().equals(goalName));
            System.out.println("Goal deleted successfully.");
        } else {
            System.out.println("Goal not found.");
        }
    }
}