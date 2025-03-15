package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Budget;
import com.demo.finance.out.repository.impl.BudgetRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BudgetRepositoryImplTest {

    @InjectMocks private BudgetRepositoryImpl repository;

    @Test
    @DisplayName("Save and find by user ID - Success scenario")
    void testSaveAndFindByUserId() {
        Budget budget = new Budget(1L, new BigDecimal(1000));
        boolean saved = repository.save(budget);

        assertThat(saved).isTrue();

        Optional<Budget> found = repository.findByUserId(1L);

        assertThat(found).isPresent().contains(budget);
    }

    @Test
    @DisplayName("Find by user ID - No budget exists scenario")
    void testFindByUserId_NoBudgetExists() {
        Optional<Budget> found = repository.findByUserId(2L);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Save - Overwrite existing budget scenario")
    void testSave_OverwriteExistingBudget() {
        Budget initialBudget = new Budget(1L, new BigDecimal(1000));
        repository.save(initialBudget);

        Budget updatedBudget = new Budget(1L, new BigDecimal(1500));
        boolean saved = repository.save(updatedBudget);

        assertThat(saved).isTrue();

        Optional<Budget> found = repository.findByUserId(1L);

        assertThat(found).isPresent().contains(updatedBudget);
    }
}