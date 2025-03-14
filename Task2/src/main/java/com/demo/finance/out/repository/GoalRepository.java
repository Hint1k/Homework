package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Goal;

import java.util.List;
import java.util.Optional;

/**
 * The GoalRepository interface defines the methods for managing Goal entities in the repository.
 * It provides CRUD operations and query methods for Goal objects.
 */
public interface GoalRepository {

    /**
     * Saves a Goal entity to the repository.
     *
     * @param goal the Goal entity to be saved
     */
    void save(Goal goal);

    boolean update(Goal goal);

    boolean delete(Long goalId);

    Goal findById(Long goalId);

    /**
     * Finds all Goal entities associated with a specific user ID.
     *
     * @param userId the ID of the user
     * @return a list of Goal entities associated with the user
     */
    List<Goal> findByUserId(Long userId);

    Optional<Goal> findUserIdAndGoalId(Long goalId, Long userId);
}