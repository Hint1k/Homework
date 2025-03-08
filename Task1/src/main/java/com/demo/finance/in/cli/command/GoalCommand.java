package com.demo.finance.in.cli.command;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.utils.MaxRetriesReachedException;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.CommandContext;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class GoalCommand {

    private final Scanner scanner;
    private final CommandContext context;
    private final ValidationUtils validationUtils;
    private static final String OR_KEEP_CURRENT_VALUE = " or leave it blank to keep current value: ";


    public GoalCommand(CommandContext context, ValidationUtils validationUtils, Scanner scanner) {
        this.context = context;
        this.validationUtils = validationUtils;
        this.scanner = scanner;
    }

    public void createGoal() {
        try {
            String name = validationUtils.promptForNonEmptyString("Enter Goal Name: ", scanner);
            Long userId = context.getCurrentUser().getUserId();
            List<Goal> goalList = context.getGoalController().getAllGoals(userId);
            if (goalList.stream().anyMatch(goal -> goal.getGoalName().equalsIgnoreCase(name))) {
                System.out.println("A goal with the name '" + name + "' already exists.");
                return;
            }
            String message = "Enter Target Amount: ";
            double targetAmount = validationUtils.promptForPositiveDouble(message, scanner);
            String message2 = "Enter Duration in Months (e.g. 3): ";
            int duration = validationUtils.promptForPositiveInt(message2, scanner);
            context.getGoalController().createGoal(context.getCurrentUser().getUserId(), name, targetAmount, duration);
            System.out.println("Goal created successfully.");
        } catch (MaxRetriesReachedException e) {
            System.out.println(e.getMessage());
        }
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
        String oldGoalName;
        try {
            oldGoalName = validationUtils
                    .promptForNonEmptyString("Enter the Name of the Goal to Update: ", scanner);
        } catch (MaxRetriesReachedException e) {
            System.out.println(e.getMessage());
            return;
        }
        Long userId = context.getCurrentUser().getUserId();
        Optional<Goal> goalToUpdate = context.getGoalController().getGoal(userId, oldGoalName);

        if (goalToUpdate.isEmpty()) {
            System.out.println("Error: Goal not found.");
            return;
        }

        String message = "Enter New Goal Name" + OR_KEEP_CURRENT_VALUE;
        String newGoalName = validationUtils.promptForOptionalString(message, scanner);
        if (newGoalName == null || newGoalName.isEmpty()) {
            newGoalName = oldGoalName;
        }

        String message2 = "Enter New Target Amount" + OR_KEEP_CURRENT_VALUE;
        Double newTargetAmount = validationUtils.promptForOptionalPositiveDouble(message2, scanner);
        if (newTargetAmount == null) {
            newTargetAmount = goalToUpdate.get().getTargetAmount();
        }

        String message3 = "Enter New Duration in Months" + OR_KEEP_CURRENT_VALUE;
        Integer newDuration = validationUtils.promptForOptionalPositiveInt(message3, scanner);
        if (newDuration == null) {
            newDuration = goalToUpdate.get().getDuration();
        }

        context.getGoalController().updateGoal(userId, oldGoalName, newGoalName, newTargetAmount, newDuration);
        System.out.println("Goal updated successfully.");
    }

    public void deleteGoal() {
        String goalName;
        try {
            goalName = validationUtils.promptForNonEmptyString("Enter Goal Name to Delete: ", scanner);
        } catch (MaxRetriesReachedException e) {
            System.out.println(e.getMessage());
            return;
        }
        Long userId = context.getCurrentUser().getUserId();

        if (context.getGoalController().getGoal(userId, goalName).isPresent()) {
            context.getGoalController().deleteGoal(userId, goalName);
            System.out.println("Goal deleted successfully.");
        } else {
            System.out.println("Error: Goal not found.");
        }
    }
}