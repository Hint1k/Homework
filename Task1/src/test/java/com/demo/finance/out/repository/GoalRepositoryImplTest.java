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

    @Test
    void testFindByUserId_noGoalsForUser_returnsEmptyList() {
        assertThat(repository.findByUserId(99L)).isEmpty();
    }

    @Test
    void testFindByUserId_multipleGoalsForUser_returnsAllGoals() {
        Goal goal1 = new Goal(4L, "New Laptop", 2000, 6);
        Goal goal2 = new Goal(4L, "Home Renovation", 5000, 12);
        repository.save(goal1);
        repository.save(goal2);

        assertThat(repository.findByUserId(4L)).containsExactlyInAnyOrder(goal1, goal2);
    }

    @Test
    void testUpdateGoal_nonExistingGoal_doesNotAddDuplicate() {
        Goal updatedGoal = new Goal(5L, "Fitness", 1500, 4);

        repository.updateGoal(5L, "Fitness", updatedGoal);

        assertThat(repository.findByUserIdAndName(5L, "Fitness")).isPresent().contains(updatedGoal);
    }

    @Test
    void testDeleteGoal_nonExistingGoal_doesNothing() {
        repository.deleteByUserIdAndName(6L, "NonExistingGoal");

        assertThat(repository.findByUserIdAndName(6L, "NonExistingGoal")).isEmpty();
    }
}