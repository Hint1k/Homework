package com.demo.finance.domain.utils;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.out.repository.TransactionRepository;

import java.time.LocalDate;

public class BalanceUtils {

    public static double calculateTotalBalance(Long userId, LocalDate startDate, LocalDate endDate,
                                               TransactionRepository transactionRepository) {
        double totalIncome = transactionRepository.findByUserId(userId).stream()
                .filter(t -> t.getType() == Type.INCOME)
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpenses = transactionRepository.findByUserId(userId).stream()
                .filter(t -> t.getType() == Type.EXPENSE)
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .mapToDouble(Transaction::getAmount)
                .sum();

        return totalIncome - totalExpenses;
    }
}