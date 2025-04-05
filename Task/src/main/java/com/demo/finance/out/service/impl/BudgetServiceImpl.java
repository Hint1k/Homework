package com.demo.finance.out.service.impl;

import com.demo.finance.domain.model.Budget;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.BudgetRepository;
import com.demo.finance.out.repository.TransactionRepository;
import com.demo.finance.out.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * The {@code BudgetServiceImpl} class implements the {@link BudgetService} interface
 * and provides concrete implementations for budget-related operations.
 * It interacts with the database through the {@link BudgetRepository} and {@link TransactionRepository}
 * to manage budgets, calculate expenses, and retrieve budget data for users.
 */
@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Sets or updates the monthly budget limit for a specific user.
     * If a budget already exists for the user, it updates the limit; otherwise, it creates a new budget.
     *
     * @param userId the unique identifier of the user
     * @param limit  the maximum amount ({@link BigDecimal}) allowed for the user's monthly budget
     * @return the updated or newly created {@link Budget} object, or {@code null} if the operation fails
     */
    @Override
    public Budget setMonthlyBudget(Long userId, BigDecimal limit) {
        Budget existingBudget = budgetRepository.findByUserId(userId);
        boolean success;
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

    /**
     * Retrieves the current budget information for a specific user.
     *
     * @param userId the unique identifier of the user
     * @return the {@link Budget} object containing the user's budget details, or {@code null} if no budget exists
     */
    @Override
    public Budget getBudget(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    /**
     * Calculates the total expenses incurred by a user for a specific month.
     *
     * @param userId       the unique identifier of the user
     * @param currentMonth the month and year ({@link YearMonth}) for which expenses are calculated
     * @return the total expenses as a {@link BigDecimal} value for the specified month
     */
    @Override
    public BigDecimal calculateExpensesForMonth(Long userId, YearMonth currentMonth) {
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        return transactionRepository.findFiltered(userId, startOfMonth, endOfMonth, null, Type.EXPENSE)
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Retrieves a comprehensive set of budget-related data for a specific user.
     * Includes the formatted budget string, total expenses, and monthly limit.
     *
     * @param userId the unique identifier of the user
     * @return a {@link Map} containing the formatted budget string and detailed budget data
     * @throws RuntimeException if no budget is set for the user
     */
    @Override
    public Map<String, Object> getBudgetData(Long userId) {
        YearMonth currentMonth = YearMonth.now();
        BigDecimal totalExpenses = calculateExpensesForMonth(userId, currentMonth);
        Budget budget = getBudget(userId);
        if (budget == null) {
            return Map.of("message", "Budget is not set", "data", Map.of(
                    "monthlyLimit", BigDecimal.ZERO,
                    "currentExpenses", totalExpenses));
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