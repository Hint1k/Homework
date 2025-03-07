package com.demo.finance.in.cli.command;

import com.demo.finance.in.cli.CommandContext;

public class NotificationCommand {

    private final CommandContext context;

    public NotificationCommand(CommandContext context) {
        this.context = context;
    }

    public void checkBudgetNotification() {
        Long userId = context.getCurrentUser().getUserId();
        String notification = context.getNotificationController().checkBudgetNotification(userId);
        System.out.println(notification);
    }

    public void checkGoalNotification() {
        Long userId = context.getCurrentUser().getUserId();
        String notification = context.getNotificationController().checkGoalNotification(userId);
        System.out.println(notification);
    }
}