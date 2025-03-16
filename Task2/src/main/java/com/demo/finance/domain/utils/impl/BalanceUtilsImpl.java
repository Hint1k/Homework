package com.demo.finance.domain.utils.impl;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.BalanceUtils;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
     * @param goal   The goal for which the balance is being calculated.
     * @return The calculated balance, which is the total income minus total expenses within the goal's timeframe.
     */
    @Override
    public BigDecimal calculateBalance(Long userId, Goal goal) {
        LocalDate startDate = goal.getStartTime();
        LocalDate endDate = startDate.plusMonths(goal.getDuration());

        List<Transaction> transactions = transactionRepository.findByUserId(userId);

        // Calculate total income within the period
        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == Type.INCOME)
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate total expenses within the period
        BigDecimal totalExpenses = transactions.stream()
                .filter(t -> t.getType() == Type.EXPENSE)
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalIncome.subtract(totalExpenses);
    }
}