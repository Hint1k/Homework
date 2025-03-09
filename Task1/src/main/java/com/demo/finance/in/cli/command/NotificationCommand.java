package com.demo.finance.in.cli.command;

import com.demo.finance.in.cli.CommandContext;

/**
 * Command class for managing notifications related to the user's budget and goals.
 * Provides functionality to check budget and goal notifications.
 */
public class NotificationCommand {

    private final CommandContext context;

    /**
     * Initializes the NotificationCommand with the provided CommandContext.
     *
     * @param context The CommandContext that holds controllers for notifications.
     */
    public NotificationCommand(CommandContext context) {
        this.context = context;
    }

    /**
     * Checks and displays the budget notification for the current user.
     * Retrieves a message indicating whether the user is within the budget or if any action is required.
     */
    public void checkBudgetNotification() {
        Long userId = context.getCurrentUser().getUserId();
        String notification = context.getNotificationController().checkBudgetNotification(userId);
        System.out.println(notification);
    }

    /**
     * Checks and displays the goal notification for the current user.
     * Retrieves a message indicating the status of the user's goals, such as progress or achievements.
     */
    public void checkGoalNotification() {
        Long userId = context.getCurrentUser().getUserId();
        String notification = context.getNotificationController().checkGoalNotification(userId);
        System.out.println(notification);
    }
}