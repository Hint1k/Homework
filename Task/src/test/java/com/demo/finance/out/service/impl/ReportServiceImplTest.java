package com.demo.finance.out.service.impl;

import com.demo.finance.domain.model.Report;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    @DisplayName("Generate user report - transactions exist - returns report")
    void testGenerateUserReport_transactionsExist_returnsReport() {
        Long userId = 1L;
        List<Transaction> transactions = List.of(
                new Transaction(1L, userId, new BigDecimal(1000), "Salary", LocalDate.now(),
                        "Monthly income", Type.INCOME),
                new Transaction(2L, userId, new BigDecimal(200), "Shopping",
                        LocalDate.now(), "Clothes", Type.EXPENSE)
        );

        when(transactionRepository.findByUserId(userId)).thenReturn(transactions);

        Report report = reportService.generateUserReport(userId);

        assertThat(report).isNotNull();
        assertThat(report.getTotalIncome()).isEqualTo(new BigDecimal(1000));
        assertThat(report.getTotalExpense()).isEqualTo(new BigDecimal(200));
        assertThat(report.getBalance()).isEqualTo(new BigDecimal(800));
    }

    @Test
    @DisplayName("Analyze expenses by category - valid range - returns expense summary")
    void testAnalyzeExpensesByCategory_validRange_returnsExpenseSummary() {
        Long userId = 1L;
        LocalDate from = LocalDate.of(2025, 3, 1);
        LocalDate to = LocalDate.of(2025, 3, 31);
        List<Transaction> transactions = List.of(
                new Transaction(1L, userId, new BigDecimal(150), "Food",
                        LocalDate.of(2025, 3, 5), "Groceries", Type.EXPENSE),
                new Transaction(2L, userId, new BigDecimal(200), "Transport",
                        LocalDate.of(2025, 3, 10), "Taxi", Type.EXPENSE)
        );

        when(transactionRepository.findByUserId(userId)).thenReturn(transactions);

        Map<String, BigDecimal> result = reportService.analyzeExpensesByCategory(userId, from, to);

        assertThat(result).hasSize(2)
                .containsEntry("Food", new BigDecimal(150))
                .containsEntry("Transport", new BigDecimal(200));
    }

    @Test
    @DisplayName("Generate user report - no transactions - returns null")
    void testGenerateUserReport_noTransactions_returnsNull() {
        Long userId = 1L;
        when(transactionRepository.findByUserId(userId)).thenReturn(List.of());

        Report report = reportService.generateUserReport(userId);

        assertThat(report).isNull();
    }

    @Test
    @DisplayName("Generate report by date - transactions within range - returns report")
    void testGenerateReportByDate_transactionsWithinRange_returnsReport() {
        Long userId = 1L;
        LocalDate from = LocalDate.of(2025, 3, 1);
        LocalDate to = LocalDate.of(2025, 3, 31);
        List<Transaction> transactions = List.of(
                new Transaction(1L, userId, new BigDecimal(500), "Freelance",
                        LocalDate.of(2025, 3, 5), "Side job", Type.INCOME),
                new Transaction(2L, userId, new BigDecimal(100), "Entertainment",
                        LocalDate.of(2025, 3, 20), "Movies", Type.EXPENSE)
        );

        when(transactionRepository.findByUserId(userId)).thenReturn(transactions);

        Report report = reportService.generateReportByDate(userId, from, to);

        assertThat(report).isNotNull();
        assertThat(report.getTotalIncome()).isEqualTo(new BigDecimal(500));
        assertThat(report.getTotalExpense()).isEqualTo(new BigDecimal(100));
        assertThat(report.getBalance()).isEqualTo(new BigDecimal(400));
    }

    @Test
    @DisplayName("Analyze expenses by category - no expenses - returns empty map")
    void testAnalyzeExpensesByCategory_noExpenses_returnsEmptyMap() {
        Long userId = 1L;
        LocalDate from = LocalDate.of(2025, 3, 1);
        LocalDate to = LocalDate.of(2025, 3, 31);
        List<Transaction> transactions = List.of(
                new Transaction(1L, userId, new BigDecimal(1000), "Salary",
                        LocalDate.of(2025, 3, 5), "Monthly pay", Type.INCOME)
        );

        when(transactionRepository.findByUserId(userId)).thenReturn(transactions);

        Map<String, BigDecimal> result = reportService.analyzeExpensesByCategory(userId, from, to);

        assertThat(result).isEmpty();
    }
}