package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.app.config.DatabaseConfig;
import com.demo.finance.domain.model.Budget;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BudgetRepositoryImplTest extends AbstractContainerBaseSetup {

    private BudgetRepositoryImpl repository;
    private Budget budget;

    @BeforeEach
    void setUp() {
        budget = Instancio.create(Budget.class);
    }

    @BeforeAll
    void setupRepository() {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.init();
        DataSourceManager dataSourceManager = new DataSourceManager(databaseConfig);
        repository = new BudgetRepositoryImpl(dataSourceManager);
    }

    @Test
    @DisplayName("Save and find budget by ID - Success scenario")
    void testSaveAndFindById() {
        budget.setUserId(1L);
        budget.setMonthlyLimit(new BigDecimal("5000.00"));
        budget.setCurrentExpenses(new BigDecimal("1200.00"));

        repository.save(budget);

        Budget found = repository.findByUserId(1L);

        assertThat(found).isNotNull();
        assertThat(found.getMonthlyLimit()).isEqualTo(new BigDecimal("5000.00"));
        assertThat(found.getCurrentExpenses()).isEqualTo(new BigDecimal("1200.00"));
    }

    @Test
    @DisplayName("Update budget - Success scenario")
    void testUpdateBudget() {
        budget.setUserId(2L);
        budget.setMonthlyLimit(new BigDecimal("3000.00"));
        budget.setCurrentExpenses(new BigDecimal("500.00"));

        repository.save(budget);

        Budget existingBudget = repository.findByUserId(2L);
        assertThat(existingBudget).isNotNull();
        Long budgetId = existingBudget.getBudgetId();

        budget.setMonthlyLimit(new BigDecimal("4000.00"));
        budget.setCurrentExpenses(new BigDecimal("800.00"));

        boolean updated = repository.update(budget);

        assertThat(updated).isTrue();
        Budget found = repository.findById(budgetId);
        assertThat(found).isNotNull();
        assertThat(found.getMonthlyLimit()).isEqualTo(new BigDecimal("4000.00"));
        assertThat(found.getCurrentExpenses()).isEqualTo(new BigDecimal("800.00"));
    }

    @Test
    @DisplayName("Delete budget - Success scenario")
    void testDeleteBudget() {
        budget.setUserId(3L);
        budget.setMonthlyLimit(new BigDecimal("6000.00"));
        budget.setCurrentExpenses(new BigDecimal("1000.00"));

        repository.save(budget);

        Budget existingBudget = repository.findByUserId(3L);
        assertThat(existingBudget).isNotNull();
        Long budgetId = existingBudget.getBudgetId();

        boolean deleted = repository.delete(budgetId);

        assertThat(deleted).isTrue();
        assertThat(repository.findById(budgetId)).isNull();
    }

    @Test
    @DisplayName("Find by user ID - Budget exists returns budget")
    void testFindByUserId_BudgetExists_ReturnsBudget() {
        budget.setUserId(4L);
        budget.setMonthlyLimit(new BigDecimal("4500.00"));
        budget.setCurrentExpenses(new BigDecimal("700.00"));

        repository.save(budget);

        Budget found = repository.findByUserId(4L);

        assertThat(found).isNotNull();
        assertThat(found.getMonthlyLimit()).isEqualTo(new BigDecimal("4500.00"));
    }

    @Test
    @DisplayName("Find by user ID - No budget returns empty optional")
    void testFindByUserId_NoBudget_ReturnsEmptyOptional() {
        assertThat(repository.findByUserId(999L)).isNull();
    }
}