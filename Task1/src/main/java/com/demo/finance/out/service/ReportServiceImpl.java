package com.demo.finance.out.service;

import com.demo.finance.domain.model.Report;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.TransactionRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The {@code ReportServiceImpl} class implements the {@link ReportService} interface.
 * It provides methods for generating financial reports and analyzing expenses for users.
 */
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;

    /**
     * Constructor to initialize the {@code ReportServiceImpl} with the given transaction repository.
     *
     * @param transactionRepository the repository for accessing user transactions
     */
    public ReportServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Generates a report for a specific user, including total income and total expense.
     *
     * @param userId the ID of the user for whom the report is generated
     * @return an {@link Optional} containing the generated {@link Report}, or {@code Optional.empty()}
     * if no transactions are found
     */
    @Override
    public Optional<Report> generateUserReport(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        return generateReportFromTransactions(userId, transactions);
    }

    /**
     * Generates a report for a specific user within a given date range.
     *
     * @param userId the ID of the user for whom the report is generated
     * @param from the start date of the range
     * @param to the end date of the range
     * @return an {@link Optional} containing the generated {@link Report}, or {@code Optional.empty()}
     * if no transactions are found
     */
    @Override
    public Optional<Report> generateReportByDate(Long userId, LocalDate from, LocalDate to) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId).stream()
                .filter(t -> t.isWithinDateRange(from, to))
                .collect(Collectors.toList());
        return generateReportFromTransactions(userId, transactions);
    }

    /**
     * Analyzes the user's expenses by category within a specified date range.
     *
     * @param userId the ID of the user whose expenses are analyzed
     * @param from the start date of the range
     * @param to the end date of the range
     * @return a {@link Map} where the key is the expense category and the value is the total amount spent
     * in that category
     */
    @Override
    public Map<String, Double> analyzeExpensesByCategory(Long userId, LocalDate from, LocalDate to) {
        return transactionRepository.findByUserId(userId).stream()
                .filter(t -> t.getType() == Type.EXPENSE)
                .filter(t -> t.isWithinDateRange(from, to))
                .collect(Collectors.groupingBy(Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)));
    }

    /**
     * Generates a report for a specific user within a given date range, using string representations of dates.
     *
     * @param userId the ID of the user for whom the report is generated
     * @param fromDate the start date of the range, represented as a string
     * @param toDate the end date of the range, represented as a string
     * @return an {@link Optional} containing the generated {@link Report}, or {@code Optional.empty()}
     * if no transactions are found
     */
    @Override
    public Optional<Report> generateReportByDate(Long userId, String fromDate, String toDate) {
        LocalDate from = LocalDate.parse(fromDate);
        LocalDate to = LocalDate.parse(toDate);
        return generateReportByDate(userId, from, to);
    }

    /**
     * Analyzes the user's expenses by category within a specified date range, using string representations of dates.
     *
     * @param userId the ID of the user whose expenses are analyzed
     * @param fromDate the start date of the range, represented as a string
     * @param toDate the end date of the range, represented as a string
     * @return a {@link Map} where the key is the expense category and the value is the total amount spent
     * in that category
     */
    @Override
    public Map<String, Double> analyzeExpensesByCategory(Long userId, String fromDate, String toDate) {
        LocalDate from = LocalDate.parse(fromDate);
        LocalDate to = LocalDate.parse(toDate);
        return analyzeExpensesByCategory(userId, from, to);
    }

    /**
     * Generates a report from a list of transactions by calculating total income and total expenses.
     *
     * @param userId the ID of the user for whom the report is generated
     * @param transactions the list of transactions for the user
     * @return an {@link Optional} containing the generated {@link Report}, or {@code Optional.empty()}
     * if no transactions are found
     */
    private Optional<Report> generateReportFromTransactions(Long userId, List<Transaction> transactions) {
        if (transactions.isEmpty()) return Optional.empty();

        double totalIncome = transactions.stream()
                .filter(t -> t.getType() == Type.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = transactions.stream()
                .filter(t -> t.getType() == Type.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        return Optional.of(new Report(userId, totalIncome, totalExpense));
    }
}