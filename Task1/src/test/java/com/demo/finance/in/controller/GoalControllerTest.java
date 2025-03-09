package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.out.service.GoalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {

    @Mock private GoalService goalService;
    @InjectMocks private GoalController goalController;

    @Test
    void testCreateGoal() {
        Long userId = 1L;
        String name = "Vacation";
        double targetAmount = 5000.0;
        int duration = 6;

        goalController.createGoal(userId, name, targetAmount, duration);

        verify(goalService, times(1)).createGoal(userId, name, targetAmount, duration);
    }

    @Test
    void testGetGoal_Success() {
        Long userId = 1L;
        String goalName = "Vacation";
        Goal mockGoal = new Goal(userId, goalName, 5000.0, 6);

        when(goalService.getGoal(userId, goalName)).thenReturn(Optional.of(mockGoal));

        Optional<Goal> result = goalController.getGoal(userId, goalName);

        assertThat(result).isPresent().contains(mockGoal);
        verify(goalService, times(1)).getGoal(userId, goalName);
    }

    @Test
    void testGetAllGoals() {
        Long userId = 1L;
        List<Goal> mockGoals = Arrays.asList(
                new Goal(userId, "Vacation", 5000.0, 6),
                new Goal(userId, "Car", 10000.0, 12)
        );

        when(goalService.getUserGoals(userId)).thenReturn(mockGoals);

        List<Goal> goals = goalController.getAllGoals(userId);

        assertThat(goals).hasSize(2).containsExactlyElementsOf(mockGoals);
        verify(goalService, times(1)).getUserGoals(userId);
    }

    @Test
    void testUpdateGoal() {
        Long userId = 1L;
        String oldGoalName = "Vacation";
        String newGoalName = "Updated Vacation";
        double newTargetAmount = 7000.0;
        int newDuration = 8;

        goalController.updateGoal(userId, oldGoalName, newGoalName, newTargetAmount, newDuration);

        verify(goalService, times(1))
                .updateGoal(userId, oldGoalName, newGoalName, newTargetAmount, newDuration);
    }

    @Test
    void testDeleteGoal() {
        Long userId = 1L;
        String goalName = "Vacation";

        goalController.deleteGoal(userId, goalName);

        verify(goalService, times(1)).deleteGoal(userId, goalName);
    }

    @Test
    void testCalculateTotalBalance() {
        Long userId = 1L;
        Goal mockGoal = new Goal(userId, "Vacation", 5000.0, 6);
        double expectedBalance = 3000.0;

        when(goalService.calculateTotalBalance(userId, mockGoal)).thenReturn(expectedBalance);

        double balance = goalController.calculateTotalBalance(userId, mockGoal);

        assertThat(balance).isEqualTo(expectedBalance);
        verify(goalService, times(1)).calculateTotalBalance(userId, mockGoal);
    }
}