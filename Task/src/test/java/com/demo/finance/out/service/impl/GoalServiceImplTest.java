package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.GoalDto;
import com.demo.finance.domain.model.Goal;
import com.demo.finance.out.repository.GoalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {

    @Mock private GoalRepository goalRepository;
    @InjectMocks private GoalServiceImpl goalService;

    @Test
    @DisplayName("Create goal - valid goal - returns goal ID")
    void testCreateGoal_validGoal_returnsGoalId() {
        GoalDto dto = new GoalDto();
        dto.setUserId(12L);
        dto.setGoalName("Car");
        dto.setTargetAmount(new BigDecimal(5000));
        dto.setDuration(12);

        when(goalRepository.save(any(Goal.class))).thenReturn(123L);

        Long result = goalService.createGoal(dto, 12L);

        assertThat(result).isEqualTo(123L);
        verify(goalRepository).save(argThat(goal ->
                goal.getGoalName().equals("Car") &&
                        goal.getTargetAmount().equals(new BigDecimal(5000)) &&
                        goal.getSavedAmount().equals(BigDecimal.ZERO)
        ));
    }

    @Test
    @DisplayName("Get goal - existing goal - returns goal")
    void testGetGoal_existingGoal_returnsGoal() {
        Goal goal = new Goal(2L, 1L, "Vacation", new BigDecimal(3000), BigDecimal.ZERO,
                6, LocalDate.now());
        when(goalRepository.findById(2L)).thenReturn(goal);

        Goal result = goalService.getGoal(2L);

        assertThat(result).isEqualTo(goal);
    }

    @Test
    @DisplayName("Get goal by user and ID - existing goal - returns goal")
    void testGetGoalByUserIdAndGoalId_existingGoal_returnsGoal() {
        Goal goal = new Goal(2L, 1L, "Car", new BigDecimal(5000), BigDecimal.ZERO,
                12, LocalDate.now());
        when(goalRepository.findByUserIdAndGoalId(1L, 2L)).thenReturn(goal);

        Goal result = goalService.getGoalByUserIdAndGoalId(1L, 2L);

        assertThat(result).isEqualTo(goal);
    }

    @Test
    @DisplayName("Update goal - existing goal - updates successfully")
    void testUpdateGoal_existingGoal_updatesSuccessfully() {
        GoalDto dto = new GoalDto();
        dto.setGoalId(2L);
        dto.setUserId(1L);
        dto.setGoalName("NewCar");
        dto.setTargetAmount(new BigDecimal(7000));
        dto.setDuration(18);

        Goal existing = new Goal(2L, 1L, "Car", new BigDecimal(5000), BigDecimal.ZERO,
                12, LocalDate.now());
        when(goalRepository.findByUserIdAndGoalId(1L, 2L)).thenReturn(existing);
        when(goalRepository.update(any(Goal.class))).thenReturn(true);

        boolean result = goalService.updateGoal(dto, 1L);

        assertThat(result).isTrue();
        verify(goalRepository).update(argThat(goal ->
                goal.getGoalName().equals("NewCar") &&
                        goal.getTargetAmount().equals(new BigDecimal(7000))
        ));
    }

    @Test
    @DisplayName("Delete goal - existing goal - deletes successfully")
    void testDeleteGoal_existingGoal_deletesSuccessfully() {
        Goal goal = new Goal(2L, 1L, "Car", new BigDecimal(5000), BigDecimal.ZERO,
                12, LocalDate.now());
        when(goalRepository.findByUserIdAndGoalId(1L, 2L)).thenReturn(goal);
        when(goalRepository.delete(2L)).thenReturn(true);

        boolean result = goalService.deleteGoal(1L, 2L);

        assertThat(result).isTrue();
        verify(goalRepository).delete(2L);
    }

    @Test
    @DisplayName("Get paginated goals - valid request - returns paginated response")
    void testGetPaginatedGoalsForUser_validRequest_returnsPaginatedResponse() {
        List<Goal> goals = List.of(
                new Goal(1L, 1L, "Car", new BigDecimal(5000), BigDecimal.ZERO,
                        12, LocalDate.now()),
                new Goal(2L, 1L, "Vacation", new BigDecimal(3000), BigDecimal.ZERO,
                        6, LocalDate.now())
        );

        when(goalRepository.findByUserId(1L, 0, 10)).thenReturn(goals);
        when(goalRepository.getTotalGoalCountForUser(1L)).thenReturn(2);

        var result = goalService.getPaginatedGoalsForUser(1L, 1, 10);

        assertThat(result.data()).hasSize(2);
        assertThat(result.totalItems()).isEqualTo(2);
        assertThat(result.totalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("Update goal - non-existing goal - returns false")
    void testUpdateGoal_nonExistingGoal_returnsFalse() {
        GoalDto dto = new GoalDto();
        dto.setGoalId(2L);
        dto.setUserId(1L);

        when(goalRepository.findByUserIdAndGoalId(1L, 2L)).thenReturn(null);

        boolean result = goalService.updateGoal(dto, 1L);

        assertThat(result).isFalse();
        verify(goalRepository, never()).update(any());
    }

    @Test
    @DisplayName("Delete goal - non-existing goal - returns false")
    void testDeleteGoal_nonExistingGoal_returnsFalse() {
        when(goalRepository.findByUserIdAndGoalId(1L, 2L)).thenReturn(null);

        boolean result = goalService.deleteGoal(1L, 2L);

        assertThat(result).isFalse();
        verify(goalRepository, never()).delete(any());
    }
}