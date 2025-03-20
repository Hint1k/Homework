package com.demo.finance.in.controller;

import com.demo.finance.out.service.BudgetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {

    @Mock private BudgetService budgetService;
    @InjectMocks private BudgetController budgetController;

    @Test
    @DisplayName("Set budget - Successfully sets the monthly budget")
    void testSetBudget_Success() {
        Long userId = 1L;
        BigDecimal amount = new BigDecimal(1000);

        budgetController.setBudget(userId, amount);

        verify(budgetService, times(1)).setMonthlyBudget(userId, amount);
    }

    @Test
    @DisplayName("View budget - Successfully retrieves and formats the budget")
    void testViewBudget_Success() {
        Long userId = 1L;
        String expectedBudget = "Budget: 500.00/1000.00";

        when(budgetService.getFormattedBudget(userId)).thenReturn(expectedBudget);

        String result = budgetController.viewBudget(userId);

        assertThat(result).isEqualTo(expectedBudget);
        verify(budgetService, times(1)).getFormattedBudget(userId);
    }
}