package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.ReportDatesDto;
import com.demo.finance.domain.dto.ReportDto;
import com.demo.finance.out.service.ReportService;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.ReportMapper;
import com.demo.finance.domain.model.Report;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.custom.ValidationException;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    private MockMvc mockMvc;
    @Mock
    private ReportService reportService;
    @Mock
    private ValidationUtils validationUtils;
    @Mock
    private ReportMapper reportMapper;
    @InjectMocks
    private ReportController reportController;
    private UserDto currentUser;
    private ReportDatesDto reportDatesDto;
    private Report report;
    private ReportDto reportDto;
    private Map<String, BigDecimal> expensesByCategory;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reportController).build();
        currentUser = Instancio.create(UserDto.class);
        currentUser.setUserId(1L);
        reportDatesDto = Instancio.create(ReportDatesDto.class);
        reportDatesDto.setFromDate(LocalDate.parse("2023-10-01"));
        reportDatesDto.setToDate(LocalDate.parse("2023-10-31"));
        report = Instancio.create(Report.class);
        reportDto = Instancio.create(ReportDto.class);
        expensesByCategory = new HashMap<>();
        expensesByCategory.put("Food", BigDecimal.valueOf(500.0));
        expensesByCategory.put("Transport", BigDecimal.valueOf(200.0));
    }

    @Test
    @DisplayName("Generate report by date - Success scenario")
    void testGenerateReportByDate_Success() throws Exception {
        when(validationUtils.validateRequest(any(ReportDatesDto.class), eq(Mode.REPORT))).thenReturn(reportDatesDto);
        when(reportService.generateReportByDate(eq(1L), any(), any())).thenReturn(report);
        when(reportMapper.toDto(report)).thenReturn(reportDto);

        mockMvc.perform(post("/api/reports/by-date")
                        .requestAttr("currentUser", currentUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromDate\":\"2023-10-01\",\"toDate\":\"2023-10-31\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Report by dates generated successfully"))
                .andExpect(jsonPath("$.data").exists());

        verify(validationUtils, times(1))
                .validateRequest(any(ReportDatesDto.class), eq(Mode.REPORT));
        verify(reportService, times(1)).generateReportByDate(eq(1L), any(), any());
        verify(reportMapper, times(1)).toDto(report);
    }

    @Test
    @DisplayName("Analyze expenses by category - Success scenario")
    void testAnalyzeExpensesByCategory_Success() throws Exception {
        when(validationUtils.validateRequest(any(ReportDatesDto.class), eq(Mode.REPORT))).thenReturn(reportDatesDto);
        when(reportService.analyzeExpensesByCategory(eq(1L), any(), any())).thenReturn(expensesByCategory);

        mockMvc.perform(post("/api/reports/expenses-by-category")
                        .requestAttr("currentUser", currentUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromDate\":\"2023-10-01\",\"toDate\":\"2023-10-31\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Expenses generated successfully"))
                .andExpect(jsonPath("$.data.Food").value(500.0))
                .andExpect(jsonPath("$.data.Transport").value(200.0));

        verify(validationUtils, times(1))
                .validateRequest(any(ReportDatesDto.class), eq(Mode.REPORT));
        verify(reportService, times(1)).analyzeExpensesByCategory(eq(1L), any(), any());
    }

    @Test
    @DisplayName("Generate general report - Success scenario")
    void testGenerateGeneralReport_Success() throws Exception {
        when(reportService.generateUserReport(1L)).thenReturn(report);
        when(reportMapper.toDto(report)).thenReturn(reportDto);

        mockMvc.perform(get("/api/reports/report")
                        .requestAttr("currentUser", currentUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("General report generated successfully"))
                .andExpect(jsonPath("$.data").exists());

        verify(reportService, times(1)).generateUserReport(1L);
        verify(reportMapper, times(1)).toDto(report);
    }

    @Test
    @DisplayName("Generate report by date - ValidationException")
    void testGenerateReportByDate_ValidationException() throws Exception {
        when(validationUtils.validateRequest(any(ReportDatesDto.class), eq(Mode.REPORT)))
                .thenThrow(new ValidationException("To date cannot be before from date"));

        mockMvc.perform(post("/api/reports/by-date")
                        .requestAttr("currentUser", currentUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromDate\":\"2023-10-31\",\"toDate\":\"2023-10-01\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("To date cannot be before from date"));

        verify(validationUtils, times(1))
                .validateRequest(any(ReportDatesDto.class), eq(Mode.REPORT));
        verify(reportService, never()).generateReportByDate(anyLong(), any(), any());
    }

    @Test
    @DisplayName("Analyze expenses by category - No expenses found")
    void testAnalyzeExpensesByCategory_NoExpensesFound() throws Exception {
        when(validationUtils.validateRequest(any(ReportDatesDto.class), eq(Mode.REPORT))).thenReturn(reportDatesDto);
        when(reportService.analyzeExpensesByCategory(eq(1L), any(), any())).thenReturn(new HashMap<>());

        mockMvc.perform(post("/api/reports/expenses-by-category")
                        .requestAttr("currentUser", currentUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromDate\":\"2023-10-01\",\"toDate\":\"2023-10-31\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("No expenses found for the user in the specified date range."));

        verify(validationUtils, times(1))
                .validateRequest(any(ReportDatesDto.class), eq(Mode.REPORT));
        verify(reportService, times(1)).analyzeExpensesByCategory(eq(1L), any(), any());
    }

    @Test
    @DisplayName("Generate general report - No reports found")
    void testGenerateGeneralReport_NoReportsFound() throws Exception {
        when(reportService.generateUserReport(1L)).thenReturn(null);

        mockMvc.perform(get("/api/reports/report")
                        .requestAttr("currentUser", currentUser))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No reports found for the user."));

        verify(reportService, times(1)).generateUserReport(1L);
        verify(reportMapper, never()).toDto(any());
    }
}