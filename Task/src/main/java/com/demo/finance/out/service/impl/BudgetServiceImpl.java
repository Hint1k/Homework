package com.demo.finance.out.service.impl;

import com.demo.finance.domain.model.Budget;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.BudgetRepository;
import com.demo.finance.out.repository.TransactionRepository;
import com.demo.finance.out.service.BudgetService;

import java.math.BigDecimal;
import java.util.Map;
import java.time.LocalDate;
import java.time.YearMonth;

public class BudgetServiceImpl implements BudgetService {
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    public BudgetServiceImpl(BudgetRepository budgetRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Budget setMonthlyBudget(Long userId, BigDecimal limit) {
        Budget existingBudget = budgetRepository.findByUserId(userId);
        boolean success = false;
        if (existingBudget != null) {
            existingBudget.setMonthlyLimit(limit);
            success = budgetRepository.update(existingBudget);
        } else {
            Budget newBudget = new Budget(userId, limit);
             success = budgetRepository.save(newBudget);
        }
        if (success) {
            return budgetRepository.findByUserId(userId);
        }
        return null;
    }

    @Override
    public Budget getBudget(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    @Override
    public BigDecimal calculateExpensesForMonth(Long userId, YearMonth currentMonth) {
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        return transactionRepository.findFiltered(userId, startOfMonth, endOfMonth, null, Type.EXPENSE)
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<String, Object> getBudgetData(Long userId) {
        YearMonth currentMonth = YearMonth.now();
        BigDecimal totalExpenses = calculateExpensesForMonth(userId, currentMonth);
        Budget budget = getBudget(userId);
        if (budget == null) {
            throw new RuntimeException("No budget set for the user.");
        }
        BigDecimal monthlyLimit = budget.getMonthlyLimit();
        String formattedBudget = String.format("Budget: %.2f/%.2f", totalExpenses, monthlyLimit);
        Map<String, Object> budgetData = Map.of(
                "monthlyLimit", monthlyLimit,
                "currentExpenses", totalExpenses
        );
        return Map.of(
                "formattedBudget", formattedBudget,
                "budgetData", budgetData
        );
    }
}