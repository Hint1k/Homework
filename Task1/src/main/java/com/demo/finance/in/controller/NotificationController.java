package com.demo.finance.in.controller;

import com.demo.finance.out.service.NotificationService;

public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public String checkBudgetNotification(Long userId) {
        return notificationService.fetchBudgetNotification(userId);
    }

    public String checkGoalNotification(Long userId) {
        return notificationService.fetchGoalNotification(userId);
    }
}