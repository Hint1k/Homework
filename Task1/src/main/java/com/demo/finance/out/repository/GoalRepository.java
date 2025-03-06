package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Goal;

import java.util.List;
import java.util.Optional;

public interface GoalRepository {

    void save(Goal goal);

    Optional<Goal> findByUserIdAndName(String userId, String goalName);

    List<Goal> findByUserId(String userId);
}