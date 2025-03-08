package com.demo.finance.domain.utils;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.out.repository.TransactionRepository;

import java.time.LocalDate;

public class BalanceUtilsImpl implements BalanceUtils {

    private final TransactionRepository transactionRepository;

    public BalanceUtilsImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

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