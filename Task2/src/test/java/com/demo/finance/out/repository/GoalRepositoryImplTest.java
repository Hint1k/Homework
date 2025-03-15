package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.out.repository.impl.GoalRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GoalRepositoryImplTest {

    @InjectMocks private GoalRepositoryImpl repository;

    @Test
    @DisplayName("Save and find goal by user ID and name - Success scenario")
    void testSaveAndFindGoal() {
        Goal goal = new Goal(1L, "Buy a Car", new BigDecimal(5000), 12);
        repository.save(goal);

        Optional<Goal> found = repository.findByUserIdAndName(1L, "Buy a Car");

        assertThat(found).isPresent().contains(goal);
    }

    @Test
    @DisplayName("Update existing goal - Success scenario")
    void testUpdateGoal() {
        Goal goal = new Goal(2L, "Vacation", new BigDecimal(3000), 6);
        repository.save(goal);

        Goal updatedGoal = new Goal(2L, "Vacation", new BigDecimal(3500), 7);
        repository.updateGoal(2L, "Vacation", updatedGoal);

        assertThat(repository.findByUserIdAndName(2L, "Vacation")).contains(updatedGoal);
    }

    @Test
    @DisplayName("Delete goal - Success scenario")
    void testDeleteGoal() {
        Goal goal = new Goal(3L, "Emergency Fund", new BigDecimal(10000), 24);
        repository.save(goal);

        repository.deleteByUserIdAndName(3L, "Emergency Fund");

        assertThat(repository.findByUserIdAndName(3L, "Emergency Fund")).isEmpty();
    }

    @Test
    @DisplayName("Find goals by user ID - No goals for user")
    void testFindByUserId_noGoalsForUser_returnsEmptyList() {
        assertThat(repository.findByUserId(99L)).isEmpty();
    }

    @Test
    @DisplayName("Find goals by user ID - Multiple goals for user")
    void testFindByUserId_multipleGoalsForUser_returnsAllGoals() {
        Goal goal1 = new Goal(4L, "New Laptop", new BigDecimal(2000), 6);
        Goal goal2 = new Goal(4L, "Home Renovation", new BigDecimal(5000), 12);
        repository.save(goal1);
        repository.save(goal2);

        assertThat(repository.findByUserId(4L)).containsExactlyInAnyOrder(goal1, goal2);
    }

    @Test
    @DisplayName("Update non-existing goal - No duplicate added")
    void testUpdateGoal_nonExistingGoal_doesNotAddDuplicate() {
        Goal updatedGoal = new Goal(5L, "Fitness", new BigDecimal(1500), 4);

        repository.updateGoal(5L, "Fitness", updatedGoal);

        assertThat(repository.findByUserIdAndName(5L, "Fitness")).isPresent().contains(updatedGoal);
    }

    @Test
    @DisplayName("Delete non-existing goal - Does nothing")
    void testDeleteGoal_nonExistingGoal_doesNothing() {
        repository.deleteByUserIdAndName(6L, "NonExistingGoal");

        assertThat(repository.findByUserIdAndName(6L, "NonExistingGoal")).isEmpty();
    }
}