package com.demo.finance.in.controller;

import com.demo.finance.out.service.BudgetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {

    @Mock private BudgetService budgetService;
    @InjectMocks private BudgetController budgetController;

    @Test
    void testSetBudget_Success() {
        Long userId = 1L;
        double amount = 1000.0;

        when(budgetService.setMonthlyBudget(userId, amount)).thenReturn(true);

        boolean result = budgetController.setBudget(userId, amount);

        assertThat(result).isTrue();
        verify(budgetService, times(1)).setMonthlyBudget(userId, amount);
    }

    @Test
    void testViewBudget_Success() {
        Long userId = 1L;
        String expectedBudget = "Budget: 500.00/1000.00";

        when(budgetService.getFormattedBudget(userId)).thenReturn(expectedBudget);

        String result = budgetController.viewBudget(userId);

        assertThat(result).isEqualTo(expectedBudget);
        verify(budgetService, times(1)).getFormattedBudget(userId);
    }
}