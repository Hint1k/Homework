package com.demo.finance.out.service;

import com.demo.finance.domain.model.Report;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock private TransactionRepository transactionRepository;
    @InjectMocks private ReportServiceImpl reportService;

    @Test
    void testGenerateUserReport_transactionsExist_returnsReport() {
        Long userId = 1L;
        List<Transaction> transactions = List.of(
                new Transaction(1L, userId, 1000.0, "Salary", LocalDate.now(),
                        "Monthly income", Type.INCOME),
                new Transaction(2L, userId, 200.0, "Shopping", LocalDate.now(),
                        "Clothes", Type.EXPENSE)
        );
        when(transactionRepository.findByUserId(userId)).thenReturn(transactions);

        Optional<Report> report = reportService.generateUserReport(userId);

        assertThat(report).isPresent();
        assertThat(report.get().toString()).contains("Income=1000.0", "Expense=200.0", "Balance=800.0");
    }

    @Test
    void testAnalyzeExpensesByCategory_validRange_returnsExpenseSummary() {
        Long userId = 1L;
        LocalDate from = LocalDate.of(2025, 3, 1);
        LocalDate to = LocalDate.of(2025, 3, 31);
        List<Transaction> transactions = List.of(
                new Transaction(1L, userId, 150.0, "Food", LocalDate.of(2025, 3,
                        5), "Groceries", Type.EXPENSE),
                new Transaction(2L, userId, 200.0, "Transport", LocalDate.of(2025,
                        3, 10), "Taxi", Type.EXPENSE)
        );

        when(transactionRepository.findByUserId(userId)).thenReturn(transactions);

        var result = reportService.analyzeExpensesByCategory(userId, from, to);

        assertThat(result).containsEntry("Food", 150.0).containsEntry("Transport", 200.0);
    }

    @Test
    void testGenerateUserReport_noTransactions_returnsEmpty() {
        Long userId = 1L;
        when(transactionRepository.findByUserId(userId)).thenReturn(List.of());

        Optional<Report> report = reportService.generateUserReport(userId);

        assertThat(report).isEmpty();
    }

    @Test
    void testGenerateReportByDate_transactionsWithinRange_returnsReport() {
        Long userId = 1L;
        LocalDate from = LocalDate.of(2025, 3, 1);
        LocalDate to = LocalDate.of(2025, 3, 31);
        List<Transaction> transactions = List.of(
                new Transaction(1L, userId, 500.0, "Freelance",
                        LocalDate.of(2025, 3, 5), "Side job", Type.INCOME),
                new Transaction(2L, userId, 100.0, "Entertainment",
                        LocalDate.of(2025, 3, 20), "Movies", Type.EXPENSE)
        );

        when(transactionRepository.findByUserId(userId)).thenReturn(transactions);

        Optional<Report> report = reportService.generateReportByDate(userId, from, to);

        assertThat(report).isPresent();
        assertThat(report.get().toString()).contains("Income=500.0", "Expense=100.0", "Balance=400.0");
    }

    @Test
    void testGenerateReportByDate_noTransactionsWithinRange_returnsEmpty() {
        Long userId = 1L;
        LocalDate from = LocalDate.of(2025, 3, 1);
        LocalDate to = LocalDate.of(2025, 3, 31);

        when(transactionRepository.findByUserId(userId)).thenReturn(List.of());

        Optional<Report> report = reportService.generateReportByDate(userId, from, to);

        assertThat(report).isEmpty();
    }

    @Test
    void testAnalyzeExpensesByCategory_noExpenses_returnsEmptyMap() {
        Long userId = 1L;
        LocalDate from = LocalDate.of(2025, 3, 1);
        LocalDate to = LocalDate.of(2025, 3, 31);

        List<Transaction> transactions = List.of(
                new Transaction(1L, userId, 1000.0, "Salary",
                        LocalDate.of(2025, 3, 5), "Monthly pay", Type.INCOME)
        );

        when(transactionRepository.findByUserId(userId)).thenReturn(transactions);

        var result = reportService.analyzeExpensesByCategory(userId, from, to);

        assertThat(result).isEmpty();
    }

    @Test
    void testGenerateReportByDate_stringDates_returnsCorrectReport() {
        Long userId = 1L;
        String fromDate = "2025-03-01";
        String toDate = "2025-03-31";
        List<Transaction> transactions = List.of(
                new Transaction(1L, userId, 800.0, "Freelance",
                        LocalDate.of(2025, 3, 10), "Project", Type.INCOME),
                new Transaction(2L, userId, 300.0, "Bills",
                        LocalDate.of(2025, 3, 15), "Electricity", Type.EXPENSE)
        );

        when(transactionRepository.findByUserId(userId)).thenReturn(transactions);

        Optional<Report> report = reportService.generateReportByDate(userId, fromDate, toDate);

        assertThat(report).isPresent();
        assertThat(report.get().toString()).contains("Income=800.0", "Expense=300.0", "Balance=500.0");
    }

    @Test
    void testAnalyzeExpensesByCategory_stringDates_returnsCorrectSummary() {
        Long userId = 1L;
        String fromDate = "2025-03-01";
        String toDate = "2025-03-31";
        List<Transaction> transactions = List.of(
                new Transaction(1L, userId, 250.0, "Dining", LocalDate.of(2025,
                        3, 5), "Restaurant", Type.EXPENSE),
                new Transaction(2L, userId, 120.0, "Transport", LocalDate.of(2025,
                        3, 20), "Bus fare", Type.EXPENSE)
        );

        when(transactionRepository.findByUserId(userId)).thenReturn(transactions);

        var result = reportService.analyzeExpensesByCategory(userId, fromDate, toDate);

        assertThat(result).containsEntry("Dining", 250.0).containsEntry("Transport", 120.0);
    }
}