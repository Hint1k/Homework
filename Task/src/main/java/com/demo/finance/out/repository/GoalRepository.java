package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Goal;

import java.util.List;

public interface GoalRepository {

    Long save(Goal goal);

    boolean update(Goal goal);

    boolean delete(Long goalId);

    Goal findById(Long goalId);

    List<Goal> findByUserId(Long userId);

    List<Goal> findByUserId(Long userId, int offset, int size);

    Goal findByUserIdAndGoalId(Long userId, Long goalId);

    int getTotalGoalCountForUser(Long userId);
}