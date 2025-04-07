package com.demo.finance.out.service.impl;

import com.demo.finance.domain.model.Budget;
import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.BalanceUtils;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.BudgetRepository;
import com.demo.finance.out.repository.GoalRepository;
import com.demo.finance.out.repository.TransactionRepository;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.EmailService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BalanceUtils balanceUtils;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private NotificationServiceImpl notificationService;
    private Budget budget;
    private Goal goal;
    private User user;
    private Long userId;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        userId = 1L;
        user = Instancio.create(User.class);
        user.setEmail("user@example.com");
        budget = Instancio.create(Budget.class);
        budget.setMonthlyLimit(new BigDecimal("1000"));
        goal = Instancio.create(Goal.class);
        goal.setTargetAmount(new BigDecimal(3000));
        goal.setGoalName("Vacation");
        transaction = Instancio.create(Transaction.class);
        transaction.setDate(LocalDate.now());
        transaction.setType(Type.EXPENSE);
    }


    @Test
    @DisplayName("Test that fetchBudgetNotification returns 'No budget set' when no budget is found for the user")
    void testFetchBudgetNotification_noBudgetSet_returnsNoBudgetMessage() {
        when(budgetRepository.findByUserId(userId)).thenReturn(null);
        when(userRepository.findById(userId)).thenReturn(new User());

        String notification = notificationService.fetchBudgetNotification(userId);

        assertThat(notification).isEqualTo("No budget set for user.");
        verify(emailService).sendEmail(any(), eq("Budget Notification"), eq("No budget set for user."));
    }

    @Test
    @DisplayName("Test that fetchGoalNotification returns 'No goals set' when no goals exist for the user")
    void testFetchGoalNotification_noGoalsSet_returnsNoGoalsMessage() {
        when(goalRepository.findByUserId(userId)).thenReturn(List.of());
        when(userRepository.findById(userId)).thenReturn(new User());

        String notification = notificationService.fetchGoalNotification(userId);

        assertThat(notification).isEqualTo("No goals set.");
        verify(emailService).sendEmail(any(), eq("Goal Notification"), eq("No goals set."));
    }

    @Test
    @DisplayName("Test that fetchBudgetNotification detects budget overuse and sends a warning email")
    void testFetchBudgetNotification_budgetExceeded_sendsWarningEmail() {
        transaction.setAmount(new BigDecimal(1500));

        when(budgetRepository.findByUserId(userId)).thenReturn(budget);
        when(transactionRepository.findByUserId(userId)).thenReturn(List.of(transaction));
        when(userRepository.findById(userId)).thenReturn(user);

        String notification = notificationService.fetchBudgetNotification(userId);

        assertThat(notification).contains("🚨 Budget exceeded!");
        verify(emailService).sendEmail(eq("user@example.com"), eq("Budget Notification"),
                contains("🚨 Budget exceeded!"));
    }

    @Test
    @DisplayName("Test that fetchBudgetNotification confirms budget is under control and sends a success email")
    void testFetchBudgetNotification_budgetUnderControl_sendsSuccessEmail() {
        transaction.setAmount(new BigDecimal(500));

        when(budgetRepository.findByUserId(userId)).thenReturn(budget);
        when(transactionRepository.findByUserId(userId)).thenReturn(List.of(transaction));
        when(userRepository.findById(userId)).thenReturn(user);

        String notification = notificationService.fetchBudgetNotification(userId);

        assertThat(notification).contains("✅ Budget is under control.");
        verify(emailService).sendEmail(eq("user@example.com"), eq("Budget Notification"),
                contains("✅ Budget is under control."));
    }

    @Test
    @DisplayName("Test that fetchBudgetNotification throws exception when user email is not found")
    void testSendNotificationViaEmail_userNotFound_throwsException() {
        when(userRepository.findById(userId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> notificationService.fetchBudgetNotification(userId));
        verify(emailService, never()).sendEmail(any(), any(), any());
    }

    @Test
    @DisplayName("Test that fetchGoalNotification detects goal progress and sends a progress update email")
    void testFetchGoalNotification_goalNotAchieved_sendsProgressEmail() {
        when(goalRepository.findByUserId(userId)).thenReturn(List.of(goal));
        when(balanceUtils.calculateBalance(userId, goal)).thenReturn(new BigDecimal(1500));
        when(userRepository.findById(userId)).thenReturn(user);

        String notification = notificationService.fetchGoalNotification(userId);

        assertThat(notification).contains("⏳ Goal 'Vacation' progress: 50.00%");
        verify(emailService).sendEmail(eq("user@example.com"), eq("Goal Notification"),
                contains("⏳ Goal 'Vacation' progress: 50.00%"));
    }

    @Test
    @DisplayName("Test that fetchGoalNotification detects goal completion and sends an achievement email")
    void testFetchGoalNotification_goalAchieved_sendsAchievementEmail() {
        when(goalRepository.findByUserId(userId)).thenReturn(List.of(goal));
        when(balanceUtils.calculateBalance(userId, goal)).thenReturn(new BigDecimal(3000));
        when(userRepository.findById(userId)).thenReturn(user);

        String notification = notificationService.fetchGoalNotification(userId);

        assertThat(notification).contains("🎉 Goal achieved: 'Vacation'!");
        verify(emailService).sendEmail(eq("user@example.com"), eq("Goal Notification"),
                contains("🎉 Goal achieved: 'Vacation'!"));
    }
}