package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.out.repository.impl.GoalRepositoryImpl;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GoalRepositoryImplTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");

    private GoalRepositoryImpl repository;

    @BeforeAll
    void setupDatabase() throws Exception {
        System.setProperty("ENV_PATH", "src/test/resources/.env");
        System.setProperty("DB_URL", POSTGRESQL_CONTAINER.getJdbcUrl());
        System.setProperty("DB_USERNAME", POSTGRESQL_CONTAINER.getUsername());
        System.setProperty("DB_PASSWORD", POSTGRESQL_CONTAINER.getPassword());

        repository = new GoalRepositoryImpl();

        try (Connection conn = DriverManager.getConnection(
                POSTGRESQL_CONTAINER.getJdbcUrl(),
                POSTGRESQL_CONTAINER.getUsername(),
                POSTGRESQL_CONTAINER.getPassword());
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE SCHEMA IF NOT EXISTS finance");

            stmt.execute("CREATE TABLE IF NOT EXISTS finance.goals (" +
                    "goal_id SERIAL PRIMARY KEY, " +
                    "user_id BIGINT NOT NULL, " +
                    "goal_name VARCHAR(255) NOT NULL, " +
                    "target_amount DECIMAL(19,2) NOT NULL, " +
                    "saved_amount DECIMAL(19,2) NOT NULL, " +
                    "duration INT NOT NULL, " +
                    "start_time DATE NOT NULL" +
                    ");");
        }
    }

    @BeforeEach
    void cleanDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection(
                POSTGRESQL_CONTAINER.getJdbcUrl(),
                POSTGRESQL_CONTAINER.getUsername(),
                POSTGRESQL_CONTAINER.getPassword());
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM finance.goals");
        }
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
        Goal goal = new Goal(null, 5L, "New Laptop", new BigDecimal("2000.00"),
                new BigDecimal("500.00"), 6, LocalDate.of(2025, 7, 1));
        repository.save(goal);

        List<Goal> savedGoals = repository.findByUserId(5L);
        assertThat(savedGoals).isNotEmpty();

        Goal savedGoal = savedGoals.get(0);
        Long goalId = savedGoal.getGoalId();

        assertThat(goalId).isNotNull();

        Optional<Goal> found = repository.findByUserIdAndGoalId(goalId, 5L);

        assertThat(found).isPresent();
        assertThat(found.get().getGoalName()).isEqualTo("New Laptop");
    }

    @AfterAll
    static void stopContainer() {
        POSTGRESQL_CONTAINER.stop();
    }
}