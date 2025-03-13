package com.demo.finance.in.cli.command;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.exception.MaxRetriesReachedException;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.CommandContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Command class for managing goals such as creating, viewing, updating, and deleting goals.
 */
public class GoalCommand {

    private final Scanner scanner;
    private final CommandContext context;
    private final ValidationUtils validationUtils;
    private static final String OR_KEEP_CURRENT_VALUE = " or leave it blank to keep current value: ";

    /**
     * Initializes the GoalCommand with the provided context, validation utilities,
     * and scanner.
     *
     * @param context The CommandContext that holds controllers.
     * @param validationUtils Utility for validation.
     * @param scanner Scanner to capture user input.
     */
    public GoalCommand(CommandContext context, ValidationUtils validationUtils, Scanner scanner) {
        this.context = context;
        this.validationUtils = validationUtils;
        this.scanner = scanner;
    }

    /**
     * Prompts the user to enter the details of a new goal (name, target amount, and duration),
     * checks if the goal name already exists, and attempts to create the goal.
     * If successful, a confirmation message is displayed.
     * If the goal already exists, an error message is shown.
     */
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
            BigDecimal targetAmount = validationUtils.promptForPositiveBigDecimal(message, scanner);
            String message2 = "Enter Duration in Months (e.g. 3): ";
            int duration = validationUtils.promptForPositiveInt(message2, scanner);
            context.getGoalController().createGoal(context.getCurrentUser().getUserId(), name, targetAmount, duration);
            System.out.println("Goal created successfully.");
        } catch (MaxRetriesReachedException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Displays all goals of the logged-in user, along with their details such as target amount,
     * duration, progress, and status (e.g. In Progress, Achieved, or Expired).
     * If no goals are found, a message is shown indicating no goals set.
     */
    public void viewGoals() {
        Long userId = context.getCurrentUser().getUserId();
        List<Goal> goals = context.getGoalController().getAllGoals(userId);
        if (goals.isEmpty()) {
            System.out.println("No goals set.");
            return;
        }
        for (Goal goal : goals) {
            BigDecimal totalBalance = context.getGoalController().calculateTotalBalance(userId, goal);
            BigDecimal progressPercentage = goal.calculateProgress(totalBalance);
            System.out.println("Goal: " + goal.getGoalName());
            System.out.println("Target Amount: " + goal.getTargetAmount());
            System.out.println("Duration: " + goal.getDuration() + " months");
            System.out.println("Progress: " + String.format("%.2f", progressPercentage) + "%");
            if (goal.isExpired()) {
                System.out.println("Status: Expired");
            } else if (progressPercentage.compareTo(BigDecimal.valueOf(100)) >= 0) {
                System.out.println("Status: Achieved");
            } else {
                System.out.println("Status: In Progress");
            }
            System.out.println();
        }
    }

    /**
     * Prompts the user to enter the details of an existing goal to update (name, target amount, and duration).
     * The user can keep the current values by leaving the input blank.
     * If the goal is found and updated successfully, a confirmation message is shown.
     * If the goal is not found, an error message is displayed.
     */
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
        BigDecimal newTargetAmount = validationUtils.promptForOptionalPositiveBigDecimal(message2, scanner);
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

    /**
     * Prompts the user to enter the name of a goal to delete.
     * If the goal is found, it is deleted and a confirmation message is shown.
     * If the goal is not found, an error message is displayed.
     */
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