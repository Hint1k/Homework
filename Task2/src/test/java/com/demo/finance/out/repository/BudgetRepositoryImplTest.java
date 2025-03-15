package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Budget;
import com.demo.finance.out.repository.impl.BudgetRepositoryImpl;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BudgetRepositoryImplTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");

    private BudgetRepositoryImpl repository;

    @BeforeAll
    void setupDatabase() throws Exception {
        System.setProperty("ENV_PATH", "src/test/resources/.env");
        System.setProperty("DB_URL", POSTGRESQL_CONTAINER.getJdbcUrl());
        System.setProperty("DB_USERNAME", POSTGRESQL_CONTAINER.getUsername());
        System.setProperty("DB_PASSWORD", POSTGRESQL_CONTAINER.getPassword());

        repository = new BudgetRepositoryImpl();

        try (Connection conn = DriverManager.getConnection(
                POSTGRESQL_CONTAINER.getJdbcUrl(),
                POSTGRESQL_CONTAINER.getUsername(),
                POSTGRESQL_CONTAINER.getPassword());
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE SCHEMA IF NOT EXISTS finance");

            stmt.execute("CREATE TABLE IF NOT EXISTS finance.budgets (" +
                    "budget_id SERIAL PRIMARY KEY, " +
                    "user_id BIGINT NOT NULL, " +
                    "monthly_limit DECIMAL(19,2) NOT NULL, " +
                    "current_expenses DECIMAL(19,2) NOT NULL" +
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
            stmt.execute("DELETE FROM finance.budgets");
        }
    }

    @Test
    @DisplayName("Save and find budget by ID - Success scenario")
    void testSaveAndFindById() {
        Budget budget = new Budget(null, 1L, new BigDecimal("5000.00"),
                new BigDecimal("1200.00"));
        repository.save(budget);

        Optional<Budget> found = repository.findByUserId(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getMonthlyLimit()).isEqualTo(new BigDecimal("5000.00"));
        assertThat(found.get().getCurrentExpenses()).isEqualTo(new BigDecimal("1200.00"));
    }

    @Test
    @DisplayName("Update budget - Success scenario")
    void testUpdateBudget() {
        Budget budget = new Budget(null, 2L, new BigDecimal("3000.00"),
                new BigDecimal("500.00"));
        repository.save(budget);

        Optional<Budget> existingBudget = repository.findByUserId(2L);
        assertThat(existingBudget).isPresent();
        Long budgetId = existingBudget.get().getBudgetId();

        Budget updatedBudget = new Budget(budgetId, 2L, new BigDecimal("4000.00"),
                new BigDecimal("800.00"));
        boolean updated = repository.update(updatedBudget);

        assertThat(updated).isTrue();
        Optional<Budget> found = repository.findById(budgetId);
        assertThat(found).isPresent();
        assertThat(found.get().getMonthlyLimit()).isEqualTo(new BigDecimal("4000.00"));
        assertThat(found.get().getCurrentExpenses()).isEqualTo(new BigDecimal("800.00"));
    }

    @Test
    @DisplayName("Delete budget - Success scenario")
    void testDeleteBudget() {
        Budget budget = new Budget(null, 3L, new BigDecimal("6000.00"),
                new BigDecimal("1000.00"));
        repository.save(budget);

        Optional<Budget> existingBudget = repository.findByUserId(3L);
        assertThat(existingBudget).isPresent();
        Long budgetId = existingBudget.get().getBudgetId();

        boolean deleted = repository.delete(budgetId);

        assertThat(deleted).isTrue();
        assertThat(repository.findById(budgetId)).isEmpty();
    }

    @Test
    @DisplayName("Find by user ID - Budget exists returns budget")
    void testFindByUserId_BudgetExists_ReturnsBudget() {
        repository.save(new Budget(null, 4L, new BigDecimal("4500.00"),
                new BigDecimal("700.00")));

        Optional<Budget> found = repository.findByUserId(4L);

        assertThat(found).isPresent();
        assertThat(found.get().getMonthlyLimit()).isEqualTo(new BigDecimal("4500.00"));
    }

    @Test
    @DisplayName("Find by user ID - No budget returns empty optional")
    void testFindByUserId_NoBudget_ReturnsEmptyOptional() {
        assertThat(repository.findByUserId(999L)).isEmpty();
    }

    @AfterAll
    static void stopContainer() {
        POSTGRESQL_CONTAINER.stop();
    }
}