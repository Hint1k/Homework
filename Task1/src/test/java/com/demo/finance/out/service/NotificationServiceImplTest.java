package com.demo.finance.out.service;

import com.demo.finance.domain.model.Budget;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.MockEmailUtils;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.BudgetRepository;
import com.demo.finance.out.repository.GoalRepository;
import com.demo.finance.out.repository.TransactionRepository;
import com.demo.finance.out.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock private BudgetRepository budgetRepository;
    @Mock private GoalRepository goalRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private UserRepository userRepository;
    @Mock private MockEmailUtils mockEmailUtils;
    @InjectMocks private NotificationServiceImpl notificationService;

    @Test
    void testFetchBudgetNotification_noBudgetSet_returnsNoBudgetMessage() {
        Long userId = 1L;
        when(budgetRepository.findByUserId(userId)).thenReturn(Optional.empty());

        String notification = notificationService.fetchBudgetNotification(userId);

        assertThat(notification).isEqualTo("No budget set for user.");
        verify(mockEmailUtils, never()).sendEmail(any(), any(), any());
    }

    @Test
    void testFetchGoalNotification_noGoalsSet_returnsNoGoalsMessage() {
        Long userId = 1L;
        when(goalRepository.findByUserId(userId)).thenReturn(List.of());

        String notification = notificationService.fetchGoalNotification(userId);

        assertThat(notification).isEqualTo("No goals set.");
        verify(mockEmailUtils, never()).sendEmail(any(), any(), any());
    }

    @Test
    void testFetchBudgetNotification_budgetExceeded_sendsWarningEmail() {
        Long userId = 1L;
        Budget budget = new Budget(userId, 500.0);
        when(budgetRepository.findByUserId(userId)).thenReturn(Optional.of(budget));
        when(transactionRepository.findByUserId(userId)).thenReturn(List.of(
                new Transaction(1L, userId, 600.0, "Shopping", LocalDate.now(),
                        "Exceeded budget", Type.EXPENSE)
        ));
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(new User(userId, "John Doe",
                "john@example.com", "password", false, null)));

        String notification = notificationService.fetchBudgetNotification(userId);

        assertThat(notification).contains("🚨 Budget exceeded!");
        verify(mockEmailUtils).sendEmail(eq("john@example.com"), any(), contains("Budget exceeded"));
    }
}