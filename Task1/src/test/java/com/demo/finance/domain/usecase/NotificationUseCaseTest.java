package com.demo.finance.domain.usecase;

import com.demo.finance.out.repository.BudgetRepository;
import com.demo.finance.out.repository.GoalRepository;
import com.demo.finance.out.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationUseCaseTest {

    @InjectMocks
    private NotificationUseCase notificationUseCase;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Test
    void testReturnNoBudgetSetMessage() {
        Mockito.when(budgetRepository.findByUserId(1L)).thenReturn(Optional.empty());

        String result = notificationUseCase.getBudgetLimitNotification(1L);

        assertThat(result).isEqualTo("No budget set for user.");
    }
}