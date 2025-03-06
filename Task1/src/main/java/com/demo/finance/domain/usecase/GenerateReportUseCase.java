package com.demo.finance.domain.usecase;

import com.demo.finance.domain.model.Report;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.out.repository.TransactionRepository;

import java.util.List;
import java.util.Optional;

public class GenerateReportUseCase {
    private final TransactionRepository transactionRepository;

    public GenerateReportUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Optional<Report> generateUserReport(String userId) {
        List<Transaction> userTransactions = transactionRepository.findByUserId(userId);

        if (userTransactions.isEmpty()) {
            return Optional.empty();
        }

        double totalIncome = userTransactions.stream()
                .filter(t -> t.getType() == Transaction.Type.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = userTransactions.stream()
                .filter(t -> t.getType() == Transaction.Type.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        return Optional.of(new Report(userId, totalIncome, totalExpense));
    }
}