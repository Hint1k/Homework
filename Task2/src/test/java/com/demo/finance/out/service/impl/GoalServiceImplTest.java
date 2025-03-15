package com.demo.finance.out.service.impl;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.utils.BalanceUtils;
import com.demo.finance.out.repository.GoalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {

    @Mock BalanceUtils balanceUtils;
    @Mock private GoalRepository goalRepository;
    @InjectMocks private GoalServiceImpl goalService;

    @Test
    @DisplayName("Test that createGoal saves the goal successfully")
    void testCreateGoal_savesGoalSuccessfully() {
        Goal goal = new Goal(1L, "Car", new BigDecimal(5000), 12);

        goalService.createGoal(1L, "Car", new BigDecimal(5000), 12);

        verify(goalRepository, times(1)).save(goal);
    }

    @Test
    @DisplayName("Test that getGoal returns an existing goal")
    void testGetGoal_existingGoal_returnsGoal() {
        Goal goal = new Goal(2L, "Vacation", new BigDecimal(3000), 6);
        when(goalRepository.findById(3L)).thenReturn(goal);

        Goal result = goalService.getGoal(3L);

        assertThat(result).isEqualTo(goal);
    }

    @Test
    @DisplayName("Test that deleteGoal deletes an existing goal successfully")
    void testDeleteGoal_existingGoal_deletesSuccessfully() {
        when(goalRepository.delete(3L)).thenReturn(true);

        Boolean result = goalService.deleteGoal(2L, 3L);

        assertThat(result).isTrue();
        verify(goalRepository, times(1)).delete(3L);
    }

    @Test
    @DisplayName("Test that getGoal returns empty when the goal does not exist")
    void testGetGoal_whenGoalDoesNotExist_returnsEmpty() {
        when(goalRepository.findById(3L)).thenReturn(null);

        Goal result = goalService.getGoal(3L);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Test that updateGoal successfully updates an existing goal")
    void testUpdateGoal_successfullyUpdatesGoal() {
        Goal existingGoal = new Goal(2L, "Car", new BigDecimal(5000), 12);
        Goal updatedGoal = new Goal(2L, "NewCar", new BigDecimal(7000), 18);

        when(goalRepository.findByUserIdAndGoalId(3L, 2L)).thenReturn(Optional.of(existingGoal));

        goalService.updateGoal(3L, 2L, "NewCar", new BigDecimal(7000), 18);

        verify(goalRepository, times(1)).update(updatedGoal);
    }

    @Test
    @DisplayName("Test that updateGoal throws an exception when the goal does not exist")
    void testUpdateGoal_throwsExceptionWhenGoalDoesNotExist() {
        when(goalRepository.findByUserIdAndGoalId(3L, 2L))
                .thenReturn(Optional.empty());

        try {
            goalService.updateGoal(3L, 2L, "NewGoal",
                    new BigDecimal(7000), 18);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Goal not found.");
        }
    }

    @Test
    @DisplayName("Test that createGoal fails to save the goal due to a runtime exception")
    void testCreateGoal_failsToSaveGoal() {
        Goal goal = new Goal(1L, "Car", new BigDecimal(5000), 12);

        doThrow(new RuntimeException("Save failed")).when(goalRepository).save(goal);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            goalService.createGoal(1L, "Car", new BigDecimal(5000), 12);
        });

        assertThat(exception.getMessage()).isEqualTo("Save failed");
        verify(goalRepository, times(1)).save(goal);
    }

    @Test
    @DisplayName("Test that calculateTotalBalance calls BalanceUtils and returns the correct value")
    void testCalculateTotalBalance_callsBalanceUtilsAndReturnsCorrectValue() {
        Goal goal = new Goal(1L, "Vacation", new BigDecimal(3000), 6);

        when(balanceUtils.calculateBalance(1L, goal)).thenReturn(new BigDecimal(1500));

        BigDecimal result = goalService.calculateTotalBalance(1L, goal);

        assertThat(result).isEqualTo(new BigDecimal(1500));
        verify(balanceUtils, times(1)).calculateBalance(1L, goal);
    }
}