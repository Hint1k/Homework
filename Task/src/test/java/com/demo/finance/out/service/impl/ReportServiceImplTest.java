package com.demo.finance.out.service.impl;

import com.demo.finance.domain.model.Report;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.TransactionRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private ReportServiceImpl reportService;
    private Transaction transaction1;
    private Transaction transaction2;
    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        transaction1 = Instancio.create(Transaction.class);
        transaction1.setType(Type.INCOME);
        transaction1.setAmount(new BigDecimal("1000"));
        transaction1.setCategory("Food");
        transaction1.setUserId(userId);
        transaction1.setDate(LocalDate.of(2025, 3, 15));
        transaction2 = Instancio.create(Transaction.class);
        transaction2.setType(Type.EXPENSE);
        transaction2.setAmount(new BigDecimal("200"));
        transaction2.setCategory("Transport");
        transaction2.setUserId(userId);
        transaction2.setDate(LocalDate.of(2025, 3, 16));
    }

    @Test
    @DisplayName("Generate user report - transactions exist - returns report")
    void testGenerateUserReport_transactionsExist_returnsReport() {
        List<Transaction> transactions = List.of(transaction1, transaction2);

        when(transactionRepository.findByUserId(userId)).thenReturn(transactions);

        Report report = reportService.generateUserReport(userId);

        assertThat(report).isNotNull();
        assertThat(report.getTotalIncome()).isEqualTo(new BigDecimal(1000));
        assertThat(report.getTotalExpense()).isEqualTo(new BigDecimal(200));
        assertThat(report.getBalance()).isEqualTo(new BigDecimal(800));
        verify(transactionRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Analyze expenses by category - valid range - returns expense summary")
    void testAnalyzeExpensesByCategory_validRange_returnsExpenseSummary() {
        LocalDate from = LocalDate.of(2025, 3, 1);
        LocalDate to = LocalDate.of(2025, 3, 31);
        transaction1.setType(Type.EXPENSE);
        List<Transaction> transactions = List.of(transaction1, transaction2);

        when(transactionRepository.findByUserId(userId)).thenReturn(transactions);

        Map<String, BigDecimal> result = reportService.analyzeExpensesByCategory(userId, from, to);

        assertThat(result).hasSize(2).containsEntry("Food", new BigDecimal(1000))
                .containsEntry("Transport", new BigDecimal(200));
        verify(transactionRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Generate user report - no transactions - returns null")
    void testGenerateUserReport_noTransactions_returnsNull() {
        when(transactionRepository.findByUserId(userId)).thenReturn(List.of());

        Report report = reportService.generateUserReport(userId);

        assertThat(report).isNull();
        verify(transactionRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Generate report by date - transactions within range - returns report")
    void testGenerateReportByDate_transactionsWithinRange_returnsReport() {
        LocalDate from = LocalDate.of(2025, 3, 1);
        LocalDate to = LocalDate.of(2025, 3, 31);
        List<Transaction> transactions = List.of(transaction1, transaction2);

        when(transactionRepository.findByUserId(userId)).thenReturn(transactions);

        Report report = reportService.generateReportByDate(userId, from, to);

        assertThat(report).isNotNull();
        assertThat(report.getTotalIncome()).isEqualTo(new BigDecimal(1000));
        assertThat(report.getTotalExpense()).isEqualTo(new BigDecimal(200));
        assertThat(report.getBalance()).isEqualTo(new BigDecimal(800));
        verify(transactionRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Analyze expenses by category - no expenses - returns empty map")
    void testAnalyzeExpensesByCategory_noExpenses_returnsEmptyMap() {
        LocalDate from = LocalDate.of(2025, 3, 1);
        LocalDate to = LocalDate.of(2025, 3, 31);
        List<Transaction> transactions = List.of(transaction1);

        when(transactionRepository.findByUserId(userId)).thenReturn(transactions);

        Map<String, BigDecimal> result = reportService.analyzeExpensesByCategory(userId, from, to);

        assertThat(result).isEmpty();
        verify(transactionRepository, times(1)).findByUserId(userId);
    }
}