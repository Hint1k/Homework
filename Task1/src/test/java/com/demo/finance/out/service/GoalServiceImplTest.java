package com.demo.finance.out.service;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.utils.BalanceUtils;
import com.demo.finance.out.repository.GoalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {

    @Mock BalanceUtils balanceUtils;
    @Mock private GoalRepository goalRepository;
    @InjectMocks private GoalServiceImpl goalService;

    @Test
    void testCreateGoal_savesGoalSuccessfully() {
        Goal goal = new Goal(1L, "Car", 5000.0, 12);

        goalService.createGoal(1L, "Car", 5000.0, 12);

        verify(goalRepository, times(1)).save(goal);
    }

    @Test
    void testGetGoal_existingGoal_returnsGoal() {
        Goal goal = new Goal(1L, "Vacation", 3000.0, 6);
        when(goalRepository.findByUserIdAndName(1L, "Vacation")).thenReturn(Optional.of(goal));

        Optional<Goal> result = goalService.getGoal(1L, "Vacation");

        assertThat(result).contains(goal);
    }

    @Test
    void testDeleteGoal_existingGoal_deletesSuccessfully() {
        doNothing().when(goalRepository).deleteByUserIdAndName(1L, "Vacation");

        goalService.deleteGoal(1L, "Vacation");

        verify(goalRepository, times(1)).deleteByUserIdAndName(1L, "Vacation");
    }

    @Test
    void testGetGoal_whenGoalDoesNotExist_returnsEmpty() {
        when(goalRepository.findByUserIdAndName(1L, "NonExistentGoal")).thenReturn(Optional.empty());

        Optional<Goal> result = goalService.getGoal(1L, "NonExistentGoal");

        assertThat(result).isEmpty();
    }

    @Test
    void testUpdateGoal_successfullyUpdatesGoal() {
        Goal existingGoal = new Goal(1L, "Car", 5000.0, 12);
        Goal updatedGoal = new Goal(1L, "NewCar", 7000.0, 18);

        when(goalRepository.findByUserIdAndName(1L, "Car")).thenReturn(Optional.of(existingGoal));

        goalService.updateGoal(1L, "Car", "NewCar",
                7000.0, 18);

        verify(goalRepository, times(1)).updateGoal(1L, "Car", updatedGoal);
    }

    @Test
    void testUpdateGoal_throwsExceptionWhenGoalDoesNotExist() {
        when(goalRepository.findByUserIdAndName(1L, "NonExistentGoal"))
                .thenReturn(Optional.empty());

        try {
            goalService.updateGoal(1L, "NonExistentGoal", "NewGoal",
                    7000.0, 18);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Goal not found.");
        }
    }

    @Test
    void testCreateGoal_failsToSaveGoal() {
        Goal goal = new Goal(1L, "Car", 5000.0, 12);

        doThrow(new RuntimeException("Save failed")).when(goalRepository).save(goal);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            goalService.createGoal(1L, "Car", 5000.0, 12);
        });

        assertThat(exception.getMessage()).isEqualTo("Save failed");
        verify(goalRepository, times(1)).save(goal);
    }

    @Test
    void testCalculateTotalBalance_callsBalanceUtilsAndReturnsCorrectValue() {
        Goal goal = new Goal(1L, "Vacation", 3000.0, 6);

        when(balanceUtils.calculateBalance(1L, goal)).thenReturn(1500.0);

        double result = goalService.calculateTotalBalance(1L, goal);

        assertThat(result).isEqualTo(1500.0);
        verify(balanceUtils, times(1)).calculateBalance(1L, goal);
    }
}