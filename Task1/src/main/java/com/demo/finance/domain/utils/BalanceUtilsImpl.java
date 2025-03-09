package com.demo.finance.domain.utils;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.out.repository.TransactionRepository;

import java.time.LocalDate;

/**
 * Implementation of the {@link BalanceUtils} interface for calculating the balance
 * towards a user's financial goal by considering both income and expenses within the goal's timeframe.
 */
public class BalanceUtilsImpl implements BalanceUtils {

    private final TransactionRepository transactionRepository;

    /**
     * Constructor for initializing the BalanceUtilsImpl with a transaction repository.
     *
     * @param transactionRepository The repository to fetch the user's transactions.
     */
    public BalanceUtilsImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Calculates the balance for a given user and goal by considering total income and total expenses
     * within the goal's start and end dates.
     *
     * @param userId The ID of the user whose balance is to be calculated.
     * @param goal The goal for which the balance is being calculated.
     * @return The calculated balance, which is the total income minus total expenses within the goal's timeframe.
     */
    @Override
    public double calculateBalance(Long userId, Goal goal) {
        LocalDate startDate = goal.getStartTime();
        LocalDate endDate = startDate.plusMonths(goal.getDuration());

        // Calculate total income within the period
        double totalIncome = transactionRepository.findByUserId(userId).stream()
                .filter(t -> t.getType() == Type.INCOME)
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .mapToDouble(Transaction::getAmount)
                .sum();

        // Calculate total expenses within the period
        double totalExpenses = transactionRepository.findByUserId(userId).stream()
                .filter(t -> t.getType() == Type.EXPENSE)
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .mapToDouble(Transaction::getAmount)
                .sum();

        return totalIncome - totalExpenses;
    }
}