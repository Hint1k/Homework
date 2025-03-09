package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Report;
import com.demo.finance.out.service.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock private ReportService reportService;
    @InjectMocks private ReportController reportController;

    @Test
    void testGenerateReport_Success() {
        Long userId = 1L;
        Report mockReport = new Report(userId, 5000.0, 3000.0);

        when(reportService.generateUserReport(userId)).thenReturn(Optional.of(mockReport));

        Optional<Report> result = reportController.generateReport(userId);

        assertThat(result).isPresent().contains(mockReport);
        verify(reportService, times(1)).generateUserReport(userId);
    }

    @Test
    void testGenerateReport_NoReportFound() {
        Long userId = 1L;

        when(reportService.generateUserReport(userId)).thenReturn(Optional.empty());

        Optional<Report> result = reportController.generateReport(userId);

        assertThat(result).isEmpty();
        verify(reportService, times(1)).generateUserReport(userId);
    }

    @Test
    void testGenerateReportByDate_Success() {
        Long userId = 1L;
        String fromDate = "2023-01-01";
        String toDate = "2023-12-31";
        Report mockReport = new Report(userId, 5000.0, 3000.0);

        when(reportService.generateReportByDate(userId, fromDate, toDate)).thenReturn(Optional.of(mockReport));

        Optional<Report> result = reportController.generateReportByDate(userId, fromDate, toDate);

        assertThat(result).isPresent().contains(mockReport);
        verify(reportService, times(1)).generateReportByDate(userId, fromDate, toDate);
    }

    @Test
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
    void testAnalyzeExpensesByCategory() {
        Long userId = 1L;
        String fromDate = "2023-01-01";
        String toDate = "2023-12-31";
        Map<String, Double> mockAnalysis = Map.of("Groceries", 500.0, "Utilities", 300.0);

        when(reportService.analyzeExpensesByCategory(userId, fromDate, toDate)).thenReturn(mockAnalysis);

        Map<String, Double> result = reportController.analyzeExpensesByCategory(userId, fromDate, toDate);

        assertThat(result).hasSize(2).containsExactlyEntriesOf(mockAnalysis);
        verify(reportService, times(1)).analyzeExpensesByCategory(userId, fromDate, toDate);
    }
}