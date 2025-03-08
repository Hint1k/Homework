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
}