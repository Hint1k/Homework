package com.demo.finance.in.cli.command;

import com.demo.finance.in.cli.CommandContext;

import java.util.Scanner;

public class NotificationCommand {

    private final CommandContext context;
    private final Scanner scanner;

    public NotificationCommand(CommandContext context, Scanner scanner) {
        this.context = context;
        this.scanner = scanner;
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