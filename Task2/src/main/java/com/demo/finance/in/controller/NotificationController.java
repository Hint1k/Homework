package com.demo.finance.in.controller;

import com.demo.finance.out.service.NotificationService;

/**
 * The {@code NotificationController} class handles incoming requests related to notifications. It provides
 * methods to check notifications related to the user's budget and goals.
 */
public class NotificationController {
    private final NotificationService notificationService;

    /**
     * Constructs a {@code NotificationController} with the specified {@code NotificationService}.
     *
     * @param notificationService the {@code NotificationService} used for fetching notifications
     */
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Retrieves the budget notification for a user.
     *
     * @param userId the ID of the user whose budget notification is to be fetched
     * @return the budget notification for the specified user
     */
    public String checkBudgetNotification(Long userId) {
        return notificationService.fetchBudgetNotification(userId);
    }

    /**
     * Retrieves the goal notification for a user.
     *
     * @param userId the ID of the user whose goal notification is to be fetched
     * @return the goal notification for the specified user
     */
    public String checkGoalNotification(Long userId) {
        return notificationService.fetchGoalNotification(userId);
    }
}