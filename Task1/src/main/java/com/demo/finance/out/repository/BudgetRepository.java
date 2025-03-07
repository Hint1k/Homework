package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Budget;

import java.util.Optional;

public interface BudgetRepository {

    boolean save(Budget budget);

    Optional<Budget> findByUserId(Long userId);
}

