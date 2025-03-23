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

/**
 * {@code BudgetServiceImpl} implements the {@code BudgetService} interface and provides the actual business logic
 * for managing a user's budget, including setting a monthly budget, calculating expenses, and generating a formatted
 * string representation of the budget.
 */
public class BudgetServiceImpl implements BudgetService {
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Constructs a new instance of {@code BudgetServiceImpl}.
     *
     * @param budgetRepository      the repository responsible for persisting and retrieving budget data
     * @param transactionRepository the repository responsible for handling transaction data
     */
    public BudgetServiceImpl(BudgetRepository budgetRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Sets the monthly budget for a user.
     *
     * @param userId the ID of the user to set the budget for
     * @param limit  the budget limit for the month
     */
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

    /**
     * Retrieves the current budget for a user.
     *
     * @param userId the ID of the user whose budget is to be retrieved
     * @return an {@code Optional<Budget>} containing the user's budget, or an empty optional if no budget is set
     */
    @Override
    public Budget getBudget(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    /**
     * Calculates the total expenses for a user for a given month.
     *
     * @param userId       the ID of the user whose expenses are to be calculated
     * @param currentMonth the month for which the expenses are to be calculated
     * @return the total expenses for the specified month
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