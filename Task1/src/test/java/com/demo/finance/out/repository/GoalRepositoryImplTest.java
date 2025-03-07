package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Goal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalRepositoryImplTest {

    @InjectMocks
    private GoalRepositoryImpl repository;

    @Test
    void testSaveAndFindGoal() {
        Goal goal = new Goal(1L, "Buy a Car", 5000, 12);
        repository.save(goal);

        Optional<Goal> found = repository.findByUserIdAndName(1L, "Buy a Car");

        assertThat(found).isPresent().contains(goal);
    }

    @Test
    void testUpdateGoal() {
        Goal goal = new Goal(2L, "Vacation", 3000, 6);
        repository.save(goal);

        Goal updatedGoal = new Goal(2L, "Vacation", 3500, 7);
        repository.updateGoal(2L, "Vacation", updatedGoal);

        assertThat(repository.findByUserIdAndName(2L, "Vacation")).contains(updatedGoal);
    }

    @Test
    void testDeleteGoal() {
        Goal goal = new Goal(3L, "Emergency Fund", 10000, 24);
        repository.save(goal);

        repository.deleteByUserIdAndName(3L, "Emergency Fund");

        assertThat(repository.findByUserIdAndName(3L, "Emergency Fund")).isEmpty();
    }
}