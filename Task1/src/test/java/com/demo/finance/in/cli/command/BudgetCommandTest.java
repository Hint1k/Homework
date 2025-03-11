package com.demo.finance.in.cli.command;

import com.demo.finance.domain.utils.MaxRetriesReachedException;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.in.controller.BudgetController;
import com.demo.finance.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

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
    void testSetBudget_Success() {
        when(validationUtils.promptForPositiveBigDecimal(any(), any())).thenReturn(new BigDecimal(500));
        when(budgetController.setBudget(2L, new BigDecimal(500))).thenReturn(true);

        budgetCommand.setBudget();

        verify(budgetController, times(1)).setBudget(2L, new BigDecimal(500));
    }

    @Test
    void testSetBudget_Failure() {
        when(validationUtils.promptForPositiveBigDecimal(any(), any())).thenReturn(new BigDecimal(500));
        when(budgetController.setBudget(2L, new BigDecimal(500))).thenReturn(false);

        budgetCommand.setBudget();

        verify(budgetController, times(1)).setBudget(2L, new BigDecimal(500));
    }

    @Test
    void testViewBudget() {
        when(budgetController.viewBudget(2L)).thenReturn("Budget: 200.00/500.00");

        budgetCommand.viewBudget();

        verify(budgetController, times(1)).viewBudget(2L);
    }

    @Test
    void testSetBudget_InvalidAmount_LogsError() {
        when(validationUtils.promptForPositiveBigDecimal(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid amount"));

        budgetCommand.setBudget();

        verify(validationUtils).promptForPositiveBigDecimal(any(), any());
        verify(budgetController, never()).setBudget(anyLong(), any(BigDecimal.class));
    }

    @Test
    void testSetBudget_ServiceFails_LogsError() {
        when(validationUtils.promptForPositiveBigDecimal(any(), any())).thenReturn(new BigDecimal(500));
        when(budgetController.setBudget(2L, new BigDecimal(500))).thenReturn(false);

        budgetCommand.setBudget();

        verify(budgetController, times(1)).setBudget(2L, new BigDecimal(500));
    }

    @Test
    void testViewBudget_NoBudgetSet_ReturnsNoBudgetMessage() {
        when(budgetController.viewBudget(2L)).thenReturn("No budget set.");

        budgetCommand.viewBudget();

        verify(budgetController, times(1)).viewBudget(2L);
    }

    @Test
    void testViewBudget_ServiceFails_LogsError() {
        when(budgetController.viewBudget(2L)).thenReturn("Error fetching budget.");

        budgetCommand.viewBudget();

        verify(budgetController, times(1)).viewBudget(2L);
    }
}