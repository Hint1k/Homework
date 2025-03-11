package com.demo.finance.in.cli.command;

import com.demo.finance.domain.model.Report;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.MaxRetriesReachedException;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.in.controller.ReportController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportCommandTest {

    @Mock private CommandContext context;
    @Mock private ValidationUtils validationUtils;
    @Mock private ReportController reportController;
    @Mock private User currentUser;
    @InjectMocks private ReportCommand reportCommand;

    @BeforeEach
    void setUp() {
        lenient().when(context.getCurrentUser()).thenReturn(currentUser);
        lenient().when(currentUser.getUserId()).thenReturn(2L);
        lenient().when(context.getReportController()).thenReturn(reportController);
    }

    @Test
    void testGenerateFullReport_Success() {
        Report mockReport = new Report(2L, new BigDecimal(5000), new BigDecimal(3000));
        when(reportController.generateReport(2L)).thenReturn(Optional.of(mockReport));

        reportCommand.generateFullReport();

        verify(reportController, times(1)).generateReport(2L);

        Optional<Report> generatedReport = reportController.generateReport(2L);
        assertTrue(generatedReport.isPresent());
        assertEquals("Report: Income=5000, Expense=3000, Balance=2000",
                generatedReport.get().toString());
    }

    @Test
    void testGenerateReportByDate_Success() {
        when(validationUtils.promptForValidDate(any(), any()))
                .thenReturn(LocalDate.of(2025, 3, 1),
                        LocalDate.of(2025, 3, 31));

        Report mockReport = new Report(2L, new BigDecimal(2000), new BigDecimal(1500));
        when(reportController.generateReportByDate(
                eq(2L), eq("2025-03-01"), eq("2025-03-31")
        )).thenReturn(Optional.of(mockReport));

        reportCommand.generateReportByDate();

        verify(reportController, times(1))
                .generateReportByDate(eq(2L), eq("2025-03-01"), eq("2025-03-31"));
    }

    @Test
    void testAnalyzeExpensesByCategory_Success() {
        when(validationUtils.promptForValidDate(any(), any()))
                .thenReturn(LocalDate.of(2025, 3, 1),
                        LocalDate.of(2025, 3, 31));

        when(reportController.analyzeExpensesByCategory(
                eq(2L), eq("2025-03-01"), eq("2025-03-31")
        )).thenReturn(Map.of("Food", new BigDecimal(500), "Transport", new BigDecimal(200)));

        reportCommand.analyzeExpensesByCategory();

        verify(reportController, times(1))
                .analyzeExpensesByCategory(eq(2L), eq("2025-03-01"), eq("2025-03-31"));
    }

    @Test
    void testGenerateFullReport_NoTransactions_ReturnsEmptyReport() {
        when(reportController.generateReport(2L)).thenReturn(Optional.empty());

        reportCommand.generateFullReport();

        verify(reportController, times(1)).generateReport(2L);
    }

    @Test
    void testGenerateReportByDate_InvalidStartDate_LogsError() {
        when(validationUtils.promptForValidDate(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid start date"));

        reportCommand.generateReportByDate();

        verify(validationUtils).promptForValidDate(any(), any());
        verify(reportController, never()).generateReportByDate(anyLong(), anyString(), anyString());
    }

    @Test
    void testGenerateReportByDate_InvalidEndDate_LogsError() {
        when(validationUtils.promptForValidDate(any(), any()))
                .thenReturn(LocalDate.of(2025, 3, 1))
                .thenThrow(new MaxRetriesReachedException("Invalid end date"));

        reportCommand.generateReportByDate();

        verify(validationUtils, times(2)).promptForValidDate(any(), any());
        verify(reportController, never()).generateReportByDate(anyLong(), anyString(), anyString());
    }

    @Test
    void testGenerateReportByDate_NoTransactionsInRange_ReturnsEmptyReport() {
        when(validationUtils.promptForValidDate(any(), any()))
                .thenReturn(LocalDate.of(2025, 3, 1))
                .thenReturn(LocalDate.of(2025, 3, 31));

        when(reportController.generateReportByDate(2L, "2025-03-01", "2025-03-31"))
                .thenReturn(Optional.empty());

        reportCommand.generateReportByDate();

        verify(reportController, times(1))
                .generateReportByDate(2L, "2025-03-01", "2025-03-31");
    }

    @Test
    void testAnalyzeExpensesByCategory_InvalidStartDate_LogsError() {
        when(validationUtils.promptForValidDate(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid start date"));

        reportCommand.analyzeExpensesByCategory();

        verify(validationUtils).promptForValidDate(any(), any());
        verify(reportController, never()).analyzeExpensesByCategory(anyLong(), anyString(), anyString());
    }

    @Test
    void testAnalyzeExpensesByCategory_InvalidEndDate_LogsError() {
        when(validationUtils.promptForValidDate(any(), any()))
                .thenReturn(LocalDate.of(2025, 3, 1))
                .thenThrow(new MaxRetriesReachedException("Invalid end date"));

        reportCommand.analyzeExpensesByCategory();

        verify(validationUtils, times(2)).promptForValidDate(any(), any());
        verify(reportController, never()).analyzeExpensesByCategory(anyLong(), anyString(), anyString());
    }

    @Test
    void testAnalyzeExpensesByCategory_NoExpensesInRange_ReturnsEmptyMap() {
        when(validationUtils.promptForValidDate(any(), any()))
                .thenReturn(LocalDate.of(2025, 3, 1))
                .thenReturn(LocalDate.of(2025, 3, 31));

        when(reportController.analyzeExpensesByCategory(2L, "2025-03-01", "2025-03-31"))
                .thenReturn(Map.of());

        reportCommand.analyzeExpensesByCategory();

        verify(reportController, times(1))
                .analyzeExpensesByCategory(2L, "2025-03-01", "2025-03-31");
    }
}