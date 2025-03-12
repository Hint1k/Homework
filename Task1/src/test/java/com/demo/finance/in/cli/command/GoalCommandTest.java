package com.demo.finance.in.cli.command;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.MaxRetriesReachedException;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.in.controller.GoalController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class GoalCommandTest {

    @Mock private CommandContext context;
    @Mock private ValidationUtils validationUtils;
    @Mock private GoalController goalController;
    @Mock private User currentUser;
    @InjectMocks private GoalCommand goalCommand;

    @BeforeEach
    void setUp() {
        lenient().when(context.getCurrentUser()).thenReturn(currentUser);
        lenient().when(currentUser.getUserId()).thenReturn(2L);
        lenient().when(context.getGoalController()).thenReturn(goalController);
    }

    @Test
    @DisplayName("Create goal - Success")
    void testCreateGoal_Success() {
        when(validationUtils.promptForNonEmptyString(any(), any())).thenReturn("New Car");
        when(validationUtils.promptForPositiveBigDecimal(any(), any())).thenReturn(new BigDecimal(10000));
        when(validationUtils.promptForPositiveInt(any(), any())).thenReturn(12);

        goalCommand.createGoal();

        verify(goalController, times(1))
                .createGoal(2L, "New Car", new BigDecimal(10000), 12);
    }

    @Test
    @DisplayName("View goals - Success")
    void testViewGoals() {
        when(goalController.getAllGoals(2L))
                .thenReturn(List.of(new Goal(2L, "Vacation", new BigDecimal(5000), 6)));

        goalCommand.viewGoals();

        verify(goalController, times(1)).getAllGoals(2L);
    }

    @Test
    @DisplayName("Delete goal - Success")
    void testDeleteGoal_Success() {
        when(validationUtils.promptForNonEmptyString(any(), any())).thenReturn("Vacation");
        Goal mockGoal = new Goal(2L, "Vacation", new BigDecimal(5000), 6);
        when(goalController.getGoal(2L, "Vacation")).thenReturn(Optional.of(mockGoal));

        goalCommand.deleteGoal();

        verify(goalController, times(1)).deleteGoal(2L, "Vacation");
    }

    @Test
    @DisplayName("Create goal - Invalid target amount logs error")
    void testCreateGoal_InvalidTargetAmount_LogsError() {
        when(validationUtils.promptForNonEmptyString(any(), any())).thenReturn("New Car");
        when(validationUtils.promptForPositiveBigDecimal(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid target amount"));

        goalCommand.createGoal();

        verify(validationUtils).promptForPositiveBigDecimal(any(), any());
        verify(goalController, never()).createGoal(anyLong(), anyString(), any(BigDecimal.class), anyInt());
    }

    @Test
    @DisplayName("Create goal - Invalid duration logs error")
    void testCreateGoal_InvalidDuration_LogsError() {
        when(validationUtils.promptForNonEmptyString(any(), any())).thenReturn("New Car");
        when(validationUtils.promptForPositiveBigDecimal(any(), any())).thenReturn(new BigDecimal(10000));
        when(validationUtils.promptForPositiveInt(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid duration"));

        goalCommand.createGoal();

        verify(validationUtils).promptForPositiveInt(any(), any());
        verify(goalController, never()).createGoal(anyLong(), anyString(), any(BigDecimal.class), anyInt());
    }

    @Test
    @DisplayName("Delete goal - Invalid goal name logs error")
    void testDeleteGoal_InvalidGoalName_LogsError() {
        when(validationUtils.promptForNonEmptyString(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid goal name"));

        goalCommand.deleteGoal();

        verify(validationUtils).promptForNonEmptyString(any(), any());
        verify(goalController, never()).deleteGoal(anyLong(), anyString());
    }

    @Test
    @DisplayName("Delete goal - Goal not found logs error")
    void testDeleteGoal_GoalNotFound_LogsError() {
        when(validationUtils.promptForNonEmptyString(any(), any())).thenReturn("Vacation");
        when(goalController.getGoal(2L, "Vacation")).thenReturn(Optional.empty());

        goalCommand.deleteGoal();

        verify(goalController, never()).deleteGoal(anyLong(), anyString());
    }

    @Test
    @DisplayName("Update goal - Invalid target amount logs error")
    void testUpdateGoal_InvalidTargetAmount_LogsError() {
        when(validationUtils.promptForNonEmptyString(any(), any())).thenReturn("Vacation");
        Goal mockGoal = new Goal(2L, "Vacation", new BigDecimal(5000), 6);
        when(goalController.getGoal(2L, "Vacation")).thenReturn(Optional.of(mockGoal));

        when(validationUtils.promptForOptionalPositiveBigDecimal(any(), any()))
                .thenAnswer(invocation -> {
                    System.out.println("Simulating invalid input for target amount.");
                    return null; // Return null to simulate keeping the current value
                });

        when(validationUtils.promptForOptionalPositiveInt(any(), any()))
                .thenReturn(12);

        goalCommand.updateGoal();

        verify(validationUtils).promptForOptionalPositiveBigDecimal(any(), any());
        verify(validationUtils).promptForOptionalPositiveInt(any(), any());
        verify(goalController, times(1)).updateGoal(eq(2L), eq("Vacation"),
                eq("Vacation"), eq(new BigDecimal(5000)), eq(12));
    }

    @Test
    @DisplayName("Update goal - Invalid duration logs error")
    void testUpdateGoal_InvalidDuration_LogsError() {
        when(validationUtils.promptForNonEmptyString(any(), any())).thenReturn("Vacation");
        Goal mockGoal = new Goal(2L, "Vacation", new BigDecimal(5000), 6);
        when(goalController.getGoal(2L, "Vacation")).thenReturn(Optional.of(mockGoal));

        when(validationUtils.promptForOptionalPositiveBigDecimal(any(), any()))
                .thenReturn(new BigDecimal(10000));

        when(validationUtils.promptForOptionalPositiveInt(any(), any()))
                .thenAnswer(invocation -> {
                    System.out.println("Simulating invalid input for duration.");
                    return null; // Return null to simulate keeping the current value
                });

        goalCommand.updateGoal();

        verify(validationUtils).promptForOptionalPositiveBigDecimal(any(), any());
        verify(validationUtils).promptForOptionalPositiveInt(any(), any());
        verify(goalController, times(1)).updateGoal(eq(2L), eq("Vacation"),
                eq("Vacation"), eq(new BigDecimal(10000)), eq(6));
    }
}