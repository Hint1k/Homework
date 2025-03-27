package com.demo.finance.out.service.impl;

import com.demo.finance.domain.model.Report;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.TransactionRepository;
import com.demo.finance.out.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The {@code ReportServiceImpl} class implements the {@link ReportService} interface
 * and provides concrete implementations for generating reports and analyzing financial data.
 * It interacts with the database through the {@link TransactionRepository} and handles logic for
 * creating user-specific reports and analyzing expenses by category.
 */
@Service
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;

    /**
     * Constructs a new instance of {@code ReportServiceImpl} with the provided repository.
     *
     * @param transactionRepository the repository used to interact with transaction data in the database
     */
    @Autowired
    public ReportServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Generates a comprehensive financial report for a specific user based on all their transactions.
     *
     * @param userId the unique identifier of the user for whom the report is generated
     * @return a {@link Report} object containing the user's total income and expenses
     */
    @Override
    public Report generateUserReport(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        return generateReportFromTransactions(userId, transactions);
    }

    /**
     * Generates a financial report for a specific user within a given date range.
     *
     * @param userId the unique identifier of the user
     * @param from   the start date of the report period (inclusive)
     * @param to     the end date of the report period (inclusive)
     * @return a {@link Report} object containing the user's total income and expenses within the specified date range
     */
    @Override
    public Report generateReportByDate(Long userId, LocalDate from, LocalDate to) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId).stream()
                .filter(t -> t.isWithinDateRange(from, to))
                .collect(Collectors.toList());
        return generateReportFromTransactions(userId, transactions);
    }

    /**
     * Analyzes and aggregates expenses by category for a specific user within a given date range.
     *
     * @param userId the unique identifier of the user
     * @param from   the start date of the analysis period (inclusive)
     * @param to     the end date of the analysis period (inclusive)
     * @return a {@link Map} where the keys represent expense categories and the values represent the total amount spent in each category
     */
    @Override
    public Map<String, BigDecimal> analyzeExpensesByCategory(Long userId, LocalDate from, LocalDate to) {
        return transactionRepository.findByUserId(userId).stream()
                .filter(t -> t.getType() == Type.EXPENSE)
                .filter(t -> t.isWithinDateRange(from, to))
                .collect(Collectors.groupingBy(Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
    }

    /**
     * Generates a financial report from a list of transactions for a specific user.
     * Calculates the total income and expenses from the provided transactions.
     *
     * @param userId       the unique identifier of the user
     * @param transactions the list of transactions to include in the report
     * @return a {@link Report} object containing the user's total income and expenses, or {@code null} if no transactions exist
     */
    private Report generateReportFromTransactions(Long userId, List<Transaction> transactions) {
        if (transactions.isEmpty()) return null;

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == Type.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> t.getType() == Type.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Report(userId, totalIncome, totalExpense);
    }
}