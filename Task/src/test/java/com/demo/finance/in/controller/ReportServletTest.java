package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.ReportMapper;
import com.demo.finance.domain.model.Report;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.out.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServletTest {

    @Mock private ReportService reportService;
    @Mock private ValidationUtils validationUtils;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private PrintWriter printWriter;
    @Spy private ReportMapper reportMapper = ReportMapper.INSTANCE;
    private ReportServlet reportServlet;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        reportServlet = new ReportServlet(reportService, objectMapper, validationUtils);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    @DisplayName("Generate report by date - Success scenario")
    void testGenerateReportByDate_Success() throws Exception {
        String requestBody = "{\"fromDate\": \"2023-10-01\", \"toDate\": \"2023-10-31\"}";
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        Map<String, LocalDate> reportDates = Map.of(
                "fromDate", LocalDate.parse("2023-10-01"),
                "toDate", LocalDate.parse("2023-10-31")
        );

        when(request.getPathInfo()).thenReturn("/by-date");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(validationUtils.validateReportJson(any(), eq(Mode.REPORT), eq(1L))).thenReturn(reportDates);
        when(reportService.generateReportByDate(eq(1L), any(), any())).thenReturn(new Report());

        reportServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("Report by dates generated successfully"));
    }

    @Test
    @DisplayName("Analyze expenses by category - Success scenario")
    void testAnalyzeExpensesByCategory_Success() throws Exception {
        String requestBody = "{\"fromDate\": \"2023-10-01\", \"toDate\": \"2023-10-31\"}";
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        Map<String, LocalDate> reportDates = Map.of(
                "fromDate", LocalDate.parse("2023-10-01"),
                "toDate", LocalDate.parse("2023-10-31")
        );

        Map<String, BigDecimal> expensesByCategory = new HashMap<>();
        expensesByCategory.put("Food", BigDecimal.valueOf(500.0));
        expensesByCategory.put("Transport", BigDecimal.valueOf(200.0));

        when(request.getPathInfo()).thenReturn("/expenses-by-category");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(validationUtils.validateReportJson(any(), eq(Mode.REPORT), eq(1L))).thenReturn(reportDates);
        when(reportService.analyzeExpensesByCategory(eq(1L), any(), any()))
                .thenReturn(expensesByCategory);

        reportServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("Expenses generated successfully"));
    }

    @Test
    @DisplayName("Generate general report - Success scenario")
    void testGenerateUserReport_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        when(request.getPathInfo()).thenReturn("/report");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(reportService.generateUserReport(eq(1L))).thenReturn(new Report());

        reportServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("General report generated successfully"));
    }

    @Test
    @DisplayName("Generate report by date - ValidationException")
    void testGenerateReportByDate_ValidationException() throws Exception {
        when(request.getPathInfo()).thenReturn("/by-date");

        reportServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(printWriter).write(contains("Invalid JSON format or input."));
    }

    @Test
    @DisplayName("Analyze expenses by category - No expenses found")
    void testAnalyzeExpensesByCategory_NoExpensesFound() throws Exception {
        String requestBody = "{\"fromDate\": \"2023-10-01\", \"toDate\": \"2023-10-31\"}";
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        Map<String, LocalDate> reportDates = Map.of(
                "fromDate", LocalDate.parse("2023-10-01"),
                "toDate", LocalDate.parse("2023-10-31")
        );

        when(request.getPathInfo()).thenReturn("/expenses-by-category");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(validationUtils.validateReportJson(any(), eq(Mode.REPORT), eq(1L))).thenReturn(reportDates);
        when(reportService.analyzeExpensesByCategory(eq(1L), any(), any()))
                .thenReturn(new HashMap<>());

        reportServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(printWriter).write(contains("No expenses found for the user in the specified date range."));
    }

    @Test
    @DisplayName("Generate general report - No reports found")
    void testGenerateUserReport_NoReportsFound() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        when(request.getPathInfo()).thenReturn("/report");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(reportService.generateUserReport(eq(1L))).thenReturn(null);

        reportServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(printWriter).write(contains("No reports found for the user."));
    }

    @Test
    @DisplayName("Endpoint not found - POST request")
    void testDoPost_EndpointNotFound() throws Exception {
        when(request.getPathInfo()).thenReturn("/unknown");

        reportServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(printWriter).write(contains("Endpoint not found"));
    }

    @Test
    @DisplayName("Endpoint not found - GET request")
    void testDoGet_EndpointNotFound() throws Exception {
        when(request.getPathInfo()).thenReturn("");

        reportServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(printWriter).write(contains("Endpoint not found"));
    }
}