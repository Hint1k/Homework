package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.ReportDto;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.ReportMapper;
import com.demo.finance.domain.model.Report;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.ValidationException;
import com.demo.finance.out.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * The {@code ReportServlet} class handles incoming HTTP requests related to report generation and analysis.
 * It provides endpoints for generating user reports and analyzing expenses by category.
 */
@WebServlet("/api/reports/*")
public class ReportServlet extends HttpServlet {

    private final ReportService reportService;
    private final ObjectMapper objectMapper;
    private final ValidationUtils validationUtils;

    public ReportServlet(ReportService reportService, ObjectMapper objectMapper, ValidationUtils validationUtils) {
        this.reportService = reportService;
        this.objectMapper = objectMapper;
        this.validationUtils = validationUtils;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/by-date".equals(pathInfo)) {
            try {
                UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
                Long userId = userDto.getUserId();
                String json = readRequestBody(request);
                Map<String, LocalDate> reportDates = validationUtils.validateReport(json, Mode.REPORT, userId);

                Report report = reportService
                        .generateReportByDate(userId, reportDates.get("fromDate"), reportDates.get("toDate"));
                if (report != null) {
                    ReportDto reportDto = ReportMapper.INSTANCE.toDto(report);
                    Map<String, Object> responseBody = Map.of(
                            "message", "Report by dates generated successfully",
                            "data", reportDto,
                            "timestamp", java.time.Instant.now().toString()
                    );
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                            "No transactions found for the user in the specified date range.");
                }
            } catch (ValidationException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            } catch (Exception e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid JSON format or input.");
            }
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/expenses-by-category".equals(pathInfo)) {
            try {
                UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
                Long userId = userDto.getUserId();
                String json = readRequestBody(request);
                Map<String, LocalDate> reportDates = validationUtils.validateReport(json, Mode.REPORT, userId);
                Map<String, BigDecimal> expensesByCategory = reportService
                        .analyzeExpensesByCategory(userId, reportDates.get("fromDate"), reportDates.get("toDate"));
                if (!expensesByCategory.isEmpty()) {
                    Map<String, Object> responseBody = Map.of(
                            "message", "Expenses generated successfully",
                            "data", expensesByCategory,
                            "timestamp", java.time.Instant.now().toString()
                    );
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                            "No expenses found for the user in the specified date range.");
                }
            } catch (ValidationException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            } catch (NumberFormatException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid user ID or date format.");
            } catch (Exception e) {
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "An error occurred while analyzing expenses by category.");
            }
        } else if ("/report".equals(pathInfo)) {
            try {
                UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
                Long userId = userDto.getUserId();
                Report report = reportService.generateUserReport(userId);
                if (report != null) {
                    ReportDto reportDto = ReportMapper.INSTANCE.toDto(report);
                    Map<String, Object> responseBody = Map.of(
                            "message", "General report generated successfully",
                            "data", reportDto,
                            "timestamp", java.time.Instant.now().toString()
                    );
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                            "No reports found for the user.");
                }
            } catch (Exception e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid JSON format or input.");
            }
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder json = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
        }
        return json.toString();
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String errorMessage)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        Map<String, String> errorResponse = Map.of("error", errorMessage);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}