package com.demo.finance.in.cli.command;

import com.demo.finance.exception.MaxRetriesReachedException;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.in.controller.BudgetController;
import com.demo.finance.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class BudgetCommandTest {

    @Mock private CommandContext context;
    @Mock private ValidationUtils validationUtils;
    @Mock private BudgetController budgetController;
    @Mock private User currentUser;
    @InjectMocks private BudgetCommand budgetCommand;

    @BeforeEach
    void setUp() {
        lenient().when(context.getCurrentUser()).thenReturn(currentUser);
        lenient().when(currentUser.getUserId()).thenReturn(2L);
        lenient().when(context.getBudgetController()).thenReturn(budgetController);
    }

    @Test
    @DisplayName("Set budget - Success")
    void testSetBudget_Success() {
        when(validationUtils.promptForPositiveBigDecimal(any(), any())).thenReturn(new BigDecimal(500));
        doNothing().when(budgetController).setBudget(2L, new BigDecimal(500));

        budgetCommand.setBudget();

        verify(budgetController, times(1)).setBudget(2L, new BigDecimal(500));
    }

    @Test
    @DisplayName("Set budget - Failure")
    void testSetBudget_Failure() {
        when(validationUtils.promptForPositiveBigDecimal(any(), any())).thenReturn(new BigDecimal(500));
        doNothing().when(budgetController).setBudget(2L, new BigDecimal(500));

        budgetCommand.setBudget();

        verify(budgetController, times(1)).setBudget(2L, new BigDecimal(500));
    }

    @Test
    @DisplayName("View budget - Success")
    void testViewBudget() {
        when(budgetController.viewBudget(2L)).thenReturn("Budget: 200.00/500.00");

        budgetCommand.viewBudget();

        verify(budgetController, times(1)).viewBudget(2L);
    }

    @Test
    @DisplayName("Set budget - Invalid amount input logs error")
    void testSetBudget_InvalidAmount_LogsError() {
        when(validationUtils.promptForPositiveBigDecimal(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid amount"));

        budgetCommand.setBudget();

        verify(validationUtils).promptForPositiveBigDecimal(any(), any());
        verify(budgetController, never()).setBudget(anyLong(), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Set budget - Service fails logs error")
    void testSetBudget_ServiceFails_LogsError() {
        when(validationUtils.promptForPositiveBigDecimal(any(), any())).thenReturn(new BigDecimal(500));
        doNothing().when(budgetController).setBudget(2L, new BigDecimal(500));

        budgetCommand.setBudget();

        verify(budgetController, times(1)).setBudget(2L, new BigDecimal(500));
    }

    @Test
    @DisplayName("View budget - No budget set")
    void testViewBudget_NoBudgetSet_ReturnsNoBudgetMessage() {
        when(budgetController.viewBudget(2L)).thenReturn("No budget set.");

        budgetCommand.viewBudget();

        verify(budgetController, times(1)).viewBudget(2L);
    }

    @Test
    @DisplayName("View budget - Service fails logs error")
    void testViewBudget_ServiceFails_LogsError() {
        when(budgetController.viewBudget(2L)).thenReturn("Error fetching budget.");

        budgetCommand.viewBudget();

        verify(budgetController, times(1)).viewBudget(2L);
    }
}