package com.demo.finance.in.cli.command;

import com.demo.finance.domain.model.User;
import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.in.controller.NotificationController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class NotificationCommandTest {

    @Mock private CommandContext context;
    @Mock private NotificationController notificationController;
    @Mock private User currentUser;
    @InjectMocks private NotificationCommand notificationCommand;

    @BeforeEach
    void setUp() {
        when(context.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.getUserId()).thenReturn(2L);
        when(context.getNotificationController()).thenReturn(notificationController);
    }

    @Test
    @DisplayName("Check budget notification - Budget is within limits")
    void testCheckBudgetNotification() {
        when(notificationController.checkBudgetNotification(2L)).thenReturn("Budget is within limits.");

        notificationCommand.checkBudgetNotification();

        verify(notificationController, times(1)).checkBudgetNotification(2L);
    }

    @Test
    @DisplayName("Check goal notification - Goal achieved")
    void testCheckGoalNotification() {
        when(notificationController.checkGoalNotification(2L)).thenReturn("Goal achieved: New Car!");

        notificationCommand.checkGoalNotification();

        verify(notificationController, times(1)).checkGoalNotification(2L);
    }

    @Test
    @DisplayName("Check budget notification - No budget set")
    void testCheckBudgetNotification_NoBudgetSet_ReturnsNoBudgetMessage() {
        when(notificationController.checkBudgetNotification(2L)).thenReturn("No budget set for user.");

        notificationCommand.checkBudgetNotification();

        verify(notificationController, times(1)).checkBudgetNotification(2L);
    }

    @Test
    @DisplayName("Check budget notification - Budget exceeded")
    void testCheckBudgetNotification_BudgetExceeded_ReturnsWarningMessage() {
        when(notificationController.checkBudgetNotification(2L))
                .thenReturn("🚨 Budget exceeded! Limit: 500.0, Expenses: 600.0");

        notificationCommand.checkBudgetNotification();

        verify(notificationController, times(1)).checkBudgetNotification(2L);
    }

    @Test
    @DisplayName("Check budget notification - Budget under control")
    void testCheckBudgetNotification_BudgetUnderControl_ReturnsSuccessMessage() {
        when(notificationController.checkBudgetNotification(2L))
                .thenReturn("✅ Budget is under control. Remaining budget: 100.0");

        notificationCommand.checkBudgetNotification();

        verify(notificationController, times(1)).checkBudgetNotification(2L);
    }

    @Test
    @DisplayName("Check goal notification - No goals set")
    void testCheckGoalNotification_NoGoalsSet_ReturnsNoGoalsMessage() {
        when(notificationController.checkGoalNotification(2L)).thenReturn("No goals set.");

        notificationCommand.checkGoalNotification();

        verify(notificationController, times(1)).checkGoalNotification(2L);
    }

    @Test
    @DisplayName("Check goal notification - Goal achieved")
    void testCheckGoalNotification_GoalAchieved_ReturnsSuccessMessage() {
        when(notificationController.checkGoalNotification(2L))
                .thenReturn("🎉 Goal achieved: 'New Car'! Target: 10000.0, Balance: 12000.0");

        notificationCommand.checkGoalNotification();

        verify(notificationController, times(1)).checkGoalNotification(2L);
    }

    @Test
    @DisplayName("Check goal notification - Goal in progress")
    void testCheckGoalNotification_GoalInProgress_ReturnsProgressMessage() {
        when(notificationController.checkGoalNotification(2L))
                .thenReturn("⏳ Goal 'New Car' progress: 75.0% (Balance: 7500.0 / Target: 10000.0)");

        notificationCommand.checkGoalNotification();

        verify(notificationController, times(1)).checkGoalNotification(2L);
    }
}