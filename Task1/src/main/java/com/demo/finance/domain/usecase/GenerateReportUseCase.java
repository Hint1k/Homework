package com.demo.finance.domain.usecase;

import com.demo.finance.domain.model.Report;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.out.repository.TransactionRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

public class GenerateReportUseCase {
    private final TransactionRepository transactionRepository;

    public GenerateReportUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Optional<Report> generateUserReport(String userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        return generateReportFromTransactions(userId, transactions);
    }

    public Optional<Report> generateReportByDate(String userId, LocalDate from, LocalDate to) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId).stream()
                .filter(t -> t.isWithinDateRange(from, to))
                .collect(Collectors.toList());
        return generateReportFromTransactions(userId, transactions);
    }

    public Map<String, Double> analyzeExpensesByCategory(String userId, LocalDate from, LocalDate to) {
        return transactionRepository.findByUserId(userId).stream()
                .filter(t -> t.getType() == Transaction.Type.EXPENSE)
                .filter(t -> t.isWithinDateRange(from, to))
                .collect(Collectors.groupingBy(Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)));
    }

    private Optional<Report> generateReportFromTransactions(String userId, List<Transaction> transactions) {
        if (transactions.isEmpty()) return Optional.empty();

        double totalIncome = transactions.stream()
                .filter(t -> t.getType() == Transaction.Type.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = transactions.stream()
                .filter(t -> t.getType() == Transaction.Type.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        return Optional.of(new Report(userId, totalIncome, totalExpense));
    }
}