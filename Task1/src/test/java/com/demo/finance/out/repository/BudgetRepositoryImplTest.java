package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Budget;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BudgetRepositoryImplTest {

    @InjectMocks
    private BudgetRepositoryImpl repository;

    @Test
    void testSaveAndFindByUserId() {
        Budget budget = new Budget(1L, 1000.0);
        boolean saved = repository.save(budget);

        assertThat(saved).isTrue();

        Optional<Budget> found = repository.findByUserId(1L);

        assertThat(found).isPresent().contains(budget);
    }

    @Test
    void testFindByUserId_NoBudgetExists() {
        Optional<Budget> found = repository.findByUserId(2L);

        assertThat(found).isEmpty();
    }

    @Test
    void testSave_OverwriteExistingBudget() {
        Budget initialBudget = new Budget(1L, 1000.0);
        repository.save(initialBudget);

        Budget updatedBudget = new Budget(1L, 1500.0);
        boolean saved = repository.save(updatedBudget);

        assertThat(saved).isTrue();

        Optional<Budget> found = repository.findByUserId(1L);

        assertThat(found).isPresent().contains(updatedBudget);
    }
}