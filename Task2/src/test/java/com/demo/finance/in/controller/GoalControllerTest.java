package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.out.service.GoalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {

    @Mock private GoalService goalService;
    @InjectMocks private GoalController goalController;

    @Test
    @DisplayName("Create goal - Successfully creates a new goal")
    void testCreateGoal() {
        Long userId = 1L;
        String name = "Vacation";
        BigDecimal targetAmount = new BigDecimal(5000);
        int duration = 6;

        goalController.createGoal(userId, name, targetAmount, duration);

        verify(goalService, times(1)).createGoal(userId, name, targetAmount, duration);
    }

    @Test
    @DisplayName("Get goal - Successfully retrieves a goal by user and goal name")
    void testGetGoal_Success() {
        Long goalId = 3L;
        String goalName = "Vacation";
        Goal mockGoal = new Goal(2L, goalName, new BigDecimal(5000), 6);

        when(goalService.getGoal(goalId)).thenReturn(mockGoal);

        Goal result = goalController.getGoal(goalId);

        assertThat(result).isEqualTo(mockGoal);
        verify(goalService, times(1)).getGoal(goalId);
    }

    @Test
    @DisplayName("Get all goals - Successfully retrieves all goals for a user")
    void testGetAllGoals() {
        Long userId = 1L;
        List<Goal> mockGoals = Arrays.asList(
                new Goal(userId, "Vacation", new BigDecimal(5000), 6),
                new Goal(userId, "Car", new BigDecimal(10000), 12)
        );

        when(goalService.getGoalsByUserId(userId)).thenReturn(mockGoals);

        List<Goal> goals = goalController.getAllGoalsByUserId(userId);

        assertThat(goals).hasSize(2).containsExactlyElementsOf(mockGoals);
        verify(goalService, times(1)).getGoalsByUserId(userId);
    }

    @Test
    @DisplayName("Update goal - Successfully updates an existing goal")
    void testUpdateGoal() {
        Long goalId = 3L;
        Long userId = 2L;
        String newGoalName = "Updated Vacation";
        BigDecimal newTargetAmount = new BigDecimal(7000);
        int newDuration = 8;

        goalController.updateGoal(goalId, userId, newGoalName, newTargetAmount, newDuration);

        verify(goalService, times(1))
                .updateGoal(goalId, userId, newGoalName, newTargetAmount, newDuration);
    }

    @Test
    @DisplayName("Delete goal - Successfully deletes a goal")
    void testDeleteGoal() {
        Long goalId = 2L;
        Long userId = 3L;

        goalController.deleteGoal(userId, goalId);

        verify(goalService, times(1)).deleteGoal(userId, goalId);
    }

    @Test
    @DisplayName("Calculate total balance - Successfully calculates the total balance of a goal")
    void testCalculateTotalBalance() {
        Long userId = 1L;
        Goal mockGoal = new Goal(userId, "Vacation", new BigDecimal(5000), 6);
        BigDecimal expectedBalance = new BigDecimal(3000);

        when(goalService.calculateTotalBalance(userId, mockGoal)).thenReturn(expectedBalance);

        BigDecimal balance = goalController.calculateTotalBalance(userId, mockGoal);

        assertThat(balance).isEqualTo(expectedBalance);
        verify(goalService, times(1)).calculateTotalBalance(userId, mockGoal);
    }
}