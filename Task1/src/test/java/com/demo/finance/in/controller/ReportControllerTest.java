package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Report;
import com.demo.finance.out.service.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock private ReportService reportService;
    @InjectMocks private ReportController reportController;

    @Test
    @DisplayName("Generate report - Success scenario")
    void testGenerateReport_Success() {
        Long userId = 1L;
        Report mockReport = new Report(userId, new BigDecimal(5000), new BigDecimal(3000));

        when(reportService.generateUserReport(userId)).thenReturn(Optional.of(mockReport));

        Optional<Report> result = reportController.generateReport(userId);

        assertThat(result).isPresent().contains(mockReport);
        verify(reportService, times(1)).generateUserReport(userId);
    }

    @Test
    @DisplayName("Generate report - No report found scenario")
    void testGenerateReport_NoReportFound() {
        Long userId = 1L;

        when(reportService.generateUserReport(userId)).thenReturn(Optional.empty());

        Optional<Report> result = reportController.generateReport(userId);

        assertThat(result).isEmpty();
        verify(reportService, times(1)).generateUserReport(userId);
    }

    @Test
    @DisplayName("Generate report by date - Success scenario")
    void testGenerateReportByDate_Success() {
        Long userId = 1L;
        String fromDate = "2023-01-01";
        String toDate = "2023-12-31";
        Report mockReport = new Report(userId, new BigDecimal(5000), new BigDecimal(3000));

        when(reportService.generateReportByDate(userId, fromDate, toDate)).thenReturn(Optional.of(mockReport));

        Optional<Report> result = reportController.generateReportByDate(userId, fromDate, toDate);

        assertThat(result).isPresent().contains(mockReport);
        verify(reportService, times(1)).generateReportByDate(userId, fromDate, toDate);
    }

    @Test
    @DisplayName("Generate report by date - No report found scenario")
    void testGenerateReportByDate_NoReportFound() {
        Long userId = 1L;
        String fromDate = "2023-01-01";
        String toDate = "2023-12-31";

        when(reportService.generateReportByDate(userId, fromDate, toDate)).thenReturn(Optional.empty());

        Optional<Report> result = reportController.generateReportByDate(userId, fromDate, toDate);

        assertThat(result).isEmpty();
        verify(reportService, times(1)).generateReportByDate(userId, fromDate, toDate);
    }

    @Test
    @DisplayName("Analyze expenses by category - Success scenario")
    void testAnalyzeExpensesByCategory() {
        Long userId = 1L;
        String fromDate = "2023-01-01";
        String toDate = "2023-12-31";
        Map<String, BigDecimal> mockAnalysis = Map.of(
                "Groceries", new BigDecimal(500), "Utilities", new BigDecimal(300)
        );

        when(reportService.analyzeExpensesByCategory(userId, fromDate, toDate)).thenReturn(mockAnalysis);

        Map<String, BigDecimal> result = reportController.analyzeExpensesByCategory(userId, fromDate, toDate);

        assertThat(result).hasSize(2).containsExactlyEntriesOf(mockAnalysis);
        verify(reportService, times(1)).analyzeExpensesByCategory(userId, fromDate, toDate);
    }
}