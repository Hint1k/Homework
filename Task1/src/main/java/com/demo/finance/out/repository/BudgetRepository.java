package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Budget;

import java.util.Optional;

public interface BudgetRepository {

    void save(Budget budget);

    Optional<Budget> findByUserId(Long userId);
}

