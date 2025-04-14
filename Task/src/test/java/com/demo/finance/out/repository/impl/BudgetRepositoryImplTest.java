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
    @DisplayName("Update budget - Success scenario")
    void testUpdateBudget() {
        Long testUserId = 2L;
        budget.setUserId(testUserId);
        budget.setMonthlyLimit(new BigDecimal("3000.00"));
        budget.setCurrentExpenses(new BigDecimal("500.00"));

        boolean saveResult = repository.save(budget);
        assertThat(saveResult).isTrue();

        Budget existingBudget = repository.findByUserId(testUserId);
        assertThat(existingBudget).isNotNull();
        assertThat(existingBudget.getMonthlyLimit()).isEqualTo(new BigDecimal("3000.00"));
        assertThat(existingBudget.getCurrentExpenses()).isEqualTo(new BigDecimal("500.00"));

        existingBudget.setMonthlyLimit(new BigDecimal("4000.00"));
        existingBudget.setCurrentExpenses(new BigDecimal("800.00"));

        boolean updated = repository.update(existingBudget);
        assertThat(updated).isTrue();

        Budget updatedBudget = repository.findByUserId(testUserId);
        assertThat(updatedBudget).isNotNull();
        assertThat(updatedBudget.getMonthlyLimit()).isEqualTo(new BigDecimal("4000.00"));
        assertThat(updatedBudget.getCurrentExpenses()).isEqualTo(new BigDecimal("800.00"));
    }

    @Test
    @DisplayName("Find by user ID - Budget exists returns budget")
    void testFindByUserId_BudgetExists_ReturnsBudget() {
        Long testUserId = 4L;
        budget.setUserId(testUserId);
        budget.setMonthlyLimit(new BigDecimal("4500.00"));
        budget.setCurrentExpenses(new BigDecimal("700.00"));

        boolean saveResult = repository.save(budget);
        assertThat(saveResult).isTrue();

        Budget found = repository.findByUserId(testUserId);
        assertThat(found).isNotNull();
        assertThat(found.getUserId()).isEqualTo(testUserId);
        assertThat(found.getMonthlyLimit()).isEqualTo(new BigDecimal("4500.00"));
        assertThat(found.getCurrentExpenses()).isEqualTo(new BigDecimal("700.00"));
    }

    @Test
    @DisplayName("Find by user ID - No budget returns null")
    void testFindByUserId_NoBudget_ReturnsNull() {
        Long nonExistentUserId = 999L;
        Budget found = repository.findByUserId(nonExistentUserId);
        assertThat(found).isNull();
    }

    @Test
    @DisplayName("Save budget - Success scenario")
    void testSaveBudget() {
        Long testUserId = 5L;
        budget.setUserId(testUserId);
        budget.setMonthlyLimit(new BigDecimal("5000.00"));
        budget.setCurrentExpenses(new BigDecimal("1000.00"));

        boolean saveResult = repository.save(budget);
        assertThat(saveResult).isTrue();

        Budget savedBudget = repository.findByUserId(testUserId);
        assertThat(savedBudget).isNotNull();
        assertThat(savedBudget.getUserId()).isEqualTo(testUserId);
        assertThat(savedBudget.getMonthlyLimit()).isEqualTo(new BigDecimal("5000.00"));
        assertThat(savedBudget.getCurrentExpenses()).isEqualTo(new BigDecimal("1000.00"));
    }

    @Test
    @DisplayName("Update budget - Non-existing budget returns false")
    void testUpdateBudget_NonExisting_ReturnsFalse() {
        Budget nonExistingBudget = new Budget();
        nonExistingBudget.setBudgetId(9999L);
        nonExistingBudget.setUserId(10L);
        nonExistingBudget.setMonthlyLimit(new BigDecimal("6000.00"));
        nonExistingBudget.setCurrentExpenses(new BigDecimal("200.00"));

        boolean updated = repository.update(nonExistingBudget);
        assertThat(updated).isFalse();
    }
}