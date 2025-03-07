package com.demo.finance.in.cli.command;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.in.cli.CommandContext;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class GoalCommand {
    private final CommandContext context;
    private final Scanner scanner;

    public GoalCommand(CommandContext context, Scanner scanner) {
        this.context = context;
        this.scanner = scanner;
    }

    public void createGoal() {
        String name = promptForNonEmptyString("Enter Goal Name: ");
        double targetAmount = promptForPositiveDouble();
        int duration = promptForPositiveInt();

        context.getGoalController().createGoal(context.getCurrentUser().getUserId(), name, targetAmount, duration);
        System.out.println("Goal created successfully.");
    }

    public void viewGoals() {
        Long userId = context.getCurrentUser().getUserId();
        List<Goal> goals = context.getGoalController().getAllGoals(userId);

        if (goals.isEmpty()) {
            System.out.println("No goals set.");
            return;
        }

        for (Goal goal : goals) {
            double totalBalance = context.getGoalController().calculateTotalBalance(userId, goal);
            double progressPercentage = goal.calculateProgress(totalBalance);

            System.out.println("Goal: " + goal.getGoalName());
            System.out.println("Target Amount: " + goal.getTargetAmount());
            System.out.println("Duration: " + goal.getDuration() + " months");
            System.out.println("Progress: " + String.format("%.2f", progressPercentage) + "%");

            if (goal.isExpired()) {
                System.out.println("Status: Expired");
            } else if (progressPercentage >= 100) {
                System.out.println("Status: Achieved");
            } else {
                System.out.println("Status: In Progress");
            }
            System.out.println();
        }
    }

    public void updateGoal() {
        String oldGoalName = promptForNonEmptyString("Enter the Name of the Goal to Update: ");
        Long userId = context.getCurrentUser().getUserId();
        Optional<Goal> goalToUpdate = context.getGoalController().getGoal(userId, oldGoalName);

        if (goalToUpdate.isEmpty()) {
            System.out.println("Error: Goal not found.");
            return;
        }

        String newGoalName = promptForOptionalString();
        if (newGoalName.isEmpty()) {
            newGoalName = oldGoalName;
        }

        Double newTargetAmount = promptForOptionalPositiveDouble();
        if (newTargetAmount == null) {
            newTargetAmount = goalToUpdate.get().getTargetAmount();
        }

        Integer newDuration = promptForOptionalPositiveInt();
        if (newDuration == null) {
            newDuration = goalToUpdate.get().getDuration();
        }

        context.getGoalController().updateGoal(userId, oldGoalName, newGoalName, newTargetAmount, newDuration);
        System.out.println("Goal updated successfully.");
    }

    public void deleteGoal() {
        String goalName = promptForNonEmptyString("Enter Goal Name to Delete: ");
        Long userId = context.getCurrentUser().getUserId();

        if (context.getGoalController().getGoal(userId, goalName).isPresent()) {
            context.getGoalController().deleteGoal(userId, goalName);
            System.out.println("Goal deleted successfully.");
        } else {
            System.out.println("Error: Goal not found.");
        }
    }

    private String promptForNonEmptyString(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("Error: Input cannot be empty.");
        }
    }

    private String promptForOptionalString() {
        System.out.print("Enter New Goal Name (or leave blank to keep current): ");
        return scanner.nextLine().trim();
    }

    private double promptForPositiveDouble() {
        while (true) {
            try {
                System.out.print("Enter Target Amount: ");
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value > 0) return value;
                System.out.println("Error: Value must be positive.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }

    private Double promptForOptionalPositiveDouble() {
        System.out.print("Enter New Target Amount (or leave blank to keep current): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return null;
        try {
            double value = Double.parseDouble(input);
            if (value > 0) return value;
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid input. Keeping current value.");
        }
        return null;
    }

    private int promptForPositiveInt() {
        while (true) {
            try {
                System.out.print("Enter Duration in Months (e.g. 3): ");
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value > 0) return value;
                System.out.println("Error: Value must be a positive integer.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }

    private Integer promptForOptionalPositiveInt() {
        System.out.print("Enter New Duration in Months (or leave blank to keep current): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return null;
        try {
            int value = Integer.parseInt(input);
            if (value > 0) return value;
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid input. Keeping current value.");
        }
        return null;
    }
}