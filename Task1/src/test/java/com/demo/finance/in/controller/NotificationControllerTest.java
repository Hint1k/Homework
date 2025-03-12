package com.demo.finance.in.controller;

import com.demo.finance.out.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock private NotificationService notificationService;
    @InjectMocks private NotificationController notificationController;

    @Test
    @DisplayName("Check budget notification - Returns correct notification message")
    void testCheckBudgetNotification() {
        Long userId = 1L;
        String expectedNotification = "Budget exceeded!";

        when(notificationService.fetchBudgetNotification(userId)).thenReturn(expectedNotification);

        String result = notificationController.checkBudgetNotification(userId);

        assertThat(result).isEqualTo(expectedNotification);
        verify(notificationService, times(1)).fetchBudgetNotification(userId);
    }

    @Test
    @DisplayName("Check goal notification - Returns correct notification message")
    void testCheckGoalNotification() {
        Long userId = 1L;
        String expectedNotification = "Goal achieved!";

        when(notificationService.fetchGoalNotification(userId)).thenReturn(expectedNotification);

        String result = notificationController.checkGoalNotification(userId);

        assertThat(result).isEqualTo(expectedNotification);
        verify(notificationService, times(1)).fetchGoalNotification(userId);
    }
}