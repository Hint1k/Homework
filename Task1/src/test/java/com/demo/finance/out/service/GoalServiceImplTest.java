package com.demo.finance.out.service;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.out.repository.GoalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {

    @Mock private GoalRepository goalRepository;
    @InjectMocks private GoalServiceImpl goalService;

    @Test
    void testCreateGoal_savesGoalSuccessfully() {
        Goal goal = new Goal(1L, "Car", 5000.0, 12);

        goalService.createGoal(1L, "Car", 5000.0, 12);

        verify(goalRepository).save(goal);
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

        verify(goalRepository).deleteByUserIdAndName(1L, "Vacation");
    }
}