package com.demo.finance.out.service;

import com.demo.finance.domain.usecase.NotificationUseCase;
import com.demo.finance.out.repository.UserRepository;

public class NotificationService {
    private final NotificationUseCase notificationUseCase;
    private final MockEmailService mockEmailService;
    private final UserRepository userRepository;

    public NotificationService(NotificationUseCase notificationUseCase, MockEmailService mockEmailService,
                               UserRepository userRepository) {
        this.notificationUseCase = notificationUseCase;
        this.mockEmailService = mockEmailService;
        this.userRepository = userRepository;
    }

    public String fetchBudgetNotification(Long userId) {
        String notification = notificationUseCase.getBudgetLimitNotification(userId);
        sendNotificationViaEmail(userId, "Budget Notification", notification);
        return notification;
    }

    public String fetchGoalNotification(Long userId) {
        String notification = notificationUseCase.getGoalCompletionNotification(userId);
        sendNotificationViaEmail(userId, "Goal Notification", notification);
        return notification;
    }

    private void sendNotificationViaEmail(Long userId, String subject, String body) {
        userRepository.findByUserId(userId).ifPresentOrElse(
                user -> mockEmailService.sendEmail(user.getEmail(), subject, body),
                () -> System.out.println("User email not found.")
        );
    }
}