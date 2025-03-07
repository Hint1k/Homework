package com.demo.finance.domain.usecase;

import com.demo.finance.domain.model.Budget;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.BudgetRepository;
import com.demo.finance.out.repository.TransactionRepository;

import java.util.Optional;
import java.time.LocalDate;
import java.time.YearMonth;

public class BudgetUseCase {
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    public BudgetUseCase(BudgetRepository budgetRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    public boolean setMonthlyBudget(Long userId, double limit) {
        budgetRepository.save(new Budget(userId, limit));
        return true;
    }

    public Optional<Budget> getBudget(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    public void trackExpense(Long userId, double amount) {
        budgetRepository.findByUserId(userId).ifPresent(budget -> budget.addExpense(amount));
    }

    public boolean isBudgetExceeded(Long userId, double transactionAmount) {
        return budgetRepository.findByUserId(userId)
                .map(budget -> budget.getCurrentExpenses() + transactionAmount > budget.getMonthlyLimit())
                .orElse(false);
    }

    public double calculateExpensesForMonth(Long userId, YearMonth currentMonth) {
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        return transactionRepository.findFiltered(userId, startOfMonth, endOfMonth, null, Type.EXPENSE)
                .stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}