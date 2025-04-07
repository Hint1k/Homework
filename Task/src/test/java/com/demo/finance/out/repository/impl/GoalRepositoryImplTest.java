package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.app.config.DatabaseConfig;
import com.demo.finance.domain.model.Goal;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GoalRepositoryImplTest extends AbstractContainerBaseSetup {

    private GoalRepositoryImpl repository;
    private Goal goal;

    @BeforeEach
    void setUp() {
        goal = Instancio.create(Goal.class);
    }

    @BeforeAll
    void setupRepository() {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.init();
        DataSourceManager dataSourceManager = new DataSourceManager(databaseConfig);
        repository = new GoalRepositoryImpl(dataSourceManager);
    }

    @Test
    @DisplayName("Save and find goal by ID - Success scenario")
    void testSaveAndFindById() {
        goal.setUserId(1L);
        goal.setTargetAmount(new BigDecimal("10000.00"));
        goal.setGoalName("Buy a Car");
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
        goal.setUserId(2L);
        repository.save(goal);

        List<Goal> savedGoals = repository.findByUserId(2L);
        assertThat(savedGoals).isNotEmpty();

        Goal savedGoal = savedGoals.get(0);
        Long goalId = savedGoal.getGoalId();
        assertThat(goalId).isNotNull();

        goal.setGoalId(goalId);
        goal.setGoalName("Vacation Fund");
        goal.setTargetAmount(new BigDecimal("6000.00"));

        boolean updated = repository.update(goal);

        assertThat(updated).isTrue();

        Goal found = repository.findById(goalId);
        assertThat(found).isNotNull();
        assertThat(found.getGoalName()).isEqualTo("Vacation Fund");
        assertThat(found.getTargetAmount()).isEqualTo(new BigDecimal("6000.00"));
    }

    @Test
    @DisplayName("Delete goal - Success scenario")
    void testDeleteGoal() {
        goal.setUserId(3L);
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
        goal.setUserId(4L);
        goal.setGoalName("Buy a Boat");
        repository.save(goal);

        goal.setGoalName("Buy a Car");
        repository.save(goal);

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
        goal.setUserId(5L);
        goal.setGoalName("New Laptop");
        repository.save(goal);

        List<Goal> savedGoals = repository.findByUserId(5L);
        assertThat(savedGoals).isNotEmpty();

        Goal savedGoal = savedGoals.get(0);
        Long goalId = savedGoal.getGoalId();

        assertThat(goalId).isNotNull();

        Goal found = repository.findByUserIdAndGoalId(5L, goalId);

        assertThat(found).isNotNull();
        assertThat(found.getGoalName()).isEqualTo("New Laptop");
    }
}