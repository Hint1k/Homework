package com.demo.finance.out.service;

import com.demo.finance.domain.usecase.NotificationUseCase;

public class NotificationService {

    private final NotificationUseCase notificationUseCase;

    public NotificationService(NotificationUseCase notificationUseCase) {
        this.notificationUseCase = notificationUseCase;
    }

    public String fetchBudgetNotification(String userId) {
        return notificationUseCase.getBudgetLimitNotification(userId);
    }

    public String fetchGoalNotification(String userId) {
        return notificationUseCase.getGoalCompletionNotification(userId);
    }
}