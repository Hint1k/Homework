package com.demo.finance.out.repository.impl;

import com.demo.finance.domain.model.Goal;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GoalRepositoryImplTest extends AbstractContainerBaseSetup {

    private GoalRepositoryImpl repository;

    @BeforeAll
    void setupRepository() {
        repository = new GoalRepositoryImpl();
    }

    @Test
    @DisplayName("Save and find goal by ID - Success scenario")
    void testSaveAndFindById() {
        Goal goal = new Goal(null, 1L, "Buy a Car", new BigDecimal("10000.00"),
                new BigDecimal("2000.00"), 12, LocalDate.of(2025, 3, 1));
        repository.save(goal);

        List<Goal> savedGoals = repository.findByUserId(1L);
        assertThat(savedGoals).isNotEmpty();

        Goal savedGoal = savedGoals.get(0);
        Long goalId = savedGoal.getGoalId();

        assertThat(goalId).isNotNull();

        Goal found = repository.findById(goalId);

        assertThat(found).isNotNull();
        assertThat(found.getGoalName()).isEqualTo("Buy a Car");
        assertThat(found.getTargetAmount()).isEqualTo(new BigDecimal("10000.00"));
    }

    @Test
    @DisplayName("Update goal - Success scenario")
    void testUpdateGoal() {
        Goal goal = new Goal(null, 2L, "Save for Vacation", new BigDecimal("5000.00"),
                new BigDecimal("500.00"), 6, LocalDate.of(2025, 6, 1));
        repository.save(goal);

        List<Goal> savedGoals = repository.findByUserId(2L);
        assertThat(savedGoals).isNotEmpty();

        Goal savedGoal = savedGoals.get(0);
        Long goalId = savedGoal.getGoalId();
        assertThat(goalId).isNotNull();

        Goal updatedGoal = new Goal(goalId, 2L, "Vacation Fund", new BigDecimal("6000.00"),
                new BigDecimal("1000.00"), 8, LocalDate.of(2025, 6, 1));
        boolean updated = repository.update(updatedGoal);

        assertThat(updated).isTrue();

        Goal found = repository.findById(goalId);
        assertThat(found).isNotNull();
        assertThat(found.getGoalName()).isEqualTo("Vacation Fund");
        assertThat(found.getTargetAmount()).isEqualTo(new BigDecimal("6000.00"));
    }

    @Test
    @DisplayName("Delete goal - Success scenario")
    void testDeleteGoal() {
        Goal goal = new Goal(null, 3L, "Emergency Fund", new BigDecimal("5000.00"),
                new BigDecimal("2500.00"), 10, LocalDate.of(2025, 5, 1));
        repository.save(goal);

        List<Goal> goals = repository.findByUserId(3L);
        assertThat(goals).isNotEmpty();

        Goal savedGoal = goals.get(0);
        Long goalId = savedGoal.getGoalId();

        assertThat(goalId).isNotNull();

        boolean deleted = repository.delete(goalId);

        assertThat(deleted).isTrue();
        assertThat(repository.findById(goalId)).isNull();
    }

    @Test
    @DisplayName("Find by user ID - Goals exist returns goals")
    void testFindByUserId_GoalsExist_ReturnsGoals() {
        repository.save(new Goal(null, 4L, "Retirement", new BigDecimal("100000.00"),
                new BigDecimal("50000.00"), 120, LocalDate.of(2025, 1, 1)));
        repository.save(new Goal(null, 4L, "House Down Payment", new BigDecimal("40000.00"),
                new BigDecimal("5000.00"), 24, LocalDate.of(2025, 4, 1)));

        List<Goal> goals = repository.findByUserId(4L);

        assertThat(goals).hasSize(2);
    }

    @Test
    @DisplayName("Find by user ID - No goals returns empty list")
    void testFindByUserId_NoGoals_ReturnsEmptyList() {
        List<Goal> goals = repository.findByUserId(999L);
        assertThat(goals).isEmpty();
    }

    @Test
    @DisplayName("Find by user and goal ID - Success scenario")
    void testFindByUserIdAndGoalId() {
        Goal goal = new Goal(5L, 3L, "New Laptop", new BigDecimal("2000.00"),
                new BigDecimal("500.00"), 6, LocalDate.of(2025, 7, 1));
        repository.save(goal);

        List<Goal> savedGoals = repository.findByUserId(3L);
        assertThat(savedGoals).isNotEmpty();

        Goal savedGoal = savedGoals.get(0);
        Long goalId = savedGoal.getGoalId();

        assertThat(goalId).isNotNull();

        Goal found = repository.findByUserIdAndGoalId(3L, goalId);

        assertThat(found).isNotNull();
        assertThat(found.getGoalName()).isEqualTo("New Laptop");
    }
}