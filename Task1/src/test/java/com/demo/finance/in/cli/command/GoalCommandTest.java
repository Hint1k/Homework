package com.demo.finance.in.cli.command;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.MaxRetriesReachedException;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.in.controller.GoalController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

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
    void testCreateGoal_Success() {
        when(validationUtils.promptForNonEmptyString(any(), any())).thenReturn("New Car");
        when(validationUtils.promptForPositiveDouble(any(), any())).thenReturn(10000.0);
        when(validationUtils.promptForPositiveInt(any(), any())).thenReturn(12);

        goalCommand.createGoal();

        verify(goalController, times(1))
                .createGoal(2L, "New Car", 10000.0, 12);
    }

    @Test
    void testViewGoals() {
        when(goalController.getAllGoals(2L))
                .thenReturn(List.of(new Goal(2L, "Vacation", 5000.0, 6)));

        goalCommand.viewGoals();

        verify(goalController, times(1)).getAllGoals(2L);
    }

    @Test
    void testDeleteGoal_Success() {
        when(validationUtils.promptForNonEmptyString(any(), any())).thenReturn("Vacation");
        Goal mockGoal = new Goal(2L, "Vacation", 5000.0, 6);
        when(goalController.getGoal(2L, "Vacation")).thenReturn(Optional.of(mockGoal));

        goalCommand.deleteGoal();

        verify(goalController, times(1)).deleteGoal(2L, "Vacation");
    }

    @Test
    void testCreateGoal_InvalidTargetAmount_LogsError() {
        when(validationUtils.promptForNonEmptyString(any(), any())).thenReturn("New Car");
        when(validationUtils.promptForPositiveDouble(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid target amount"));

        goalCommand.createGoal();

        verify(validationUtils).promptForPositiveDouble(any(), any());
        verify(goalController, never()).createGoal(anyLong(), anyString(), anyDouble(), anyInt());
    }

    @Test
    void testCreateGoal_InvalidDuration_LogsError() {
        when(validationUtils.promptForNonEmptyString(any(), any())).thenReturn("New Car");
        when(validationUtils.promptForPositiveDouble(any(), any())).thenReturn(10000.0);
        when(validationUtils.promptForPositiveInt(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid duration"));

        goalCommand.createGoal();

        verify(validationUtils).promptForPositiveInt(any(), any());
        verify(goalController, never()).createGoal(anyLong(), anyString(), anyDouble(), anyInt());
    }

    @Test
    void testDeleteGoal_InvalidGoalName_LogsError() {
        when(validationUtils.promptForNonEmptyString(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid goal name"));

        goalCommand.deleteGoal();

        verify(validationUtils).promptForNonEmptyString(any(), any());
        verify(goalController, never()).deleteGoal(anyLong(), anyString());
    }

    @Test
    void testDeleteGoal_GoalNotFound_LogsError() {
        when(validationUtils.promptForNonEmptyString(any(), any())).thenReturn("Vacation");
        when(goalController.getGoal(2L, "Vacation")).thenReturn(Optional.empty());

        goalCommand.deleteGoal();

        verify(goalController, never()).deleteGoal(anyLong(), anyString());
    }

    @Test
    void testUpdateGoal_InvalidTargetAmount_LogsError() {
        when(validationUtils.promptForNonEmptyString(any(), any())).thenReturn("Vacation");
        Goal mockGoal = new Goal(2L, "Vacation", 5000.0, 6);
        when(goalController.getGoal(2L, "Vacation")).thenReturn(Optional.of(mockGoal));

        when(validationUtils.promptForOptionalPositiveDouble(any(), any()))
                .thenAnswer(invocation -> {
                    System.out.println("Simulating invalid input for target amount.");
                    return null; // Return null to simulate keeping the current value
                });

        when(validationUtils.promptForOptionalPositiveInt(any(), any()))
                .thenReturn(12);

        goalCommand.updateGoal();

        verify(validationUtils).promptForOptionalPositiveDouble(any(), any());
        verify(validationUtils).promptForOptionalPositiveInt(any(), any());
        verify(goalController, times(1))
                .updateGoal(eq(2L), eq("Vacation"), eq("Vacation"), eq(5000.0), eq(12));
    }

    @Test
    void testUpdateGoal_InvalidDuration_LogsError() {
        when(validationUtils.promptForNonEmptyString(any(), any())).thenReturn("Vacation");
        Goal mockGoal = new Goal(2L, "Vacation", 5000.0, 6);
        when(goalController.getGoal(2L, "Vacation")).thenReturn(Optional.of(mockGoal));

        when(validationUtils.promptForOptionalPositiveDouble(any(), any()))
                .thenReturn(10000.0);

        when(validationUtils.promptForOptionalPositiveInt(any(), any()))
                .thenAnswer(invocation -> {
                    System.out.println("Simulating invalid input for duration.");
                    return null; // Return null to simulate keeping the current value
                });

        goalCommand.updateGoal();

        verify(validationUtils).promptForOptionalPositiveDouble(any(), any());
        verify(validationUtils).promptForOptionalPositiveInt(any(), any());
        verify(goalController, times(1))
                .updateGoal(eq(2L), eq("Vacation"), eq("Vacation"), eq(10000.0), eq(6));
    }
}