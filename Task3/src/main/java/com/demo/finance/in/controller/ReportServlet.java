package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.ReportDto;
import com.demo.finance.domain.mapper.ReportMapper;
import com.demo.finance.out.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * The {@code ReportServlet} class handles incoming HTTP requests related to report generation and analysis.
 * It provides endpoints for generating user reports and analyzing expenses by category.
 */
@WebServlet("/api/reports/*")
public class ReportServlet extends HttpServlet {

    private final ReportService reportService;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a {@code ReportServlet} with the specified {@code ReportService}.
     *
     * @param reportService the {@code ReportService} used for generating reports and analyzing data
     */
    public ReportServlet(ReportService reportService) {
        this.reportService = reportService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Handles POST requests to generate a report for a user.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/report".equals(pathInfo)) {
            try {
                StringBuilder jsonBody = new StringBuilder();
                String line;
                try (BufferedReader reader = request.getReader()) {
                    while ((line = reader.readLine()) != null) {
                        jsonBody.append(line);
                    }
                }
                ReportDto reportDto = objectMapper.readValue(jsonBody.toString(), ReportDto.class);
                Long userId = reportDto.getUserId();
                if (userId == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Invalid input. User ID is mandatory.");
                    return;
                }
                var reportOptional = reportService.generateUserReport(userId);
                if (reportOptional.isPresent()) {
                    ReportDto generatedReport = ReportMapper.INSTANCE.toDto(reportOptional.get());
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(generatedReport));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("No transactions found for the user.");
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid JSON format or input.");
            }
        } else if ("/by-date".equals(pathInfo)) {
            try {
                StringBuilder jsonBody = new StringBuilder();
                String line;
                try (BufferedReader reader = request.getReader()) {
                    while ((line = reader.readLine()) != null) {
                        jsonBody.append(line);
                    }
                }
                ReportDto reportDto = objectMapper.readValue(jsonBody.toString(), ReportDto.class);
                Long userId = reportDto.getUserId();
                String fromDate = request.getParameter("fromDate");
                String toDate = request.getParameter("toDate");
                if (userId == null || fromDate == null || toDate == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Invalid input. User ID, fromDate, and toDate are mandatory.");
                    return;
                }
                var reportOptional = reportService.generateReportByDate(userId, fromDate, toDate);
                if (reportOptional.isPresent()) {
                    ReportDto generatedReport = ReportMapper.INSTANCE.toDto(reportOptional.get());
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(generatedReport));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("No transactions found for the user in the specified date range.");
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid JSON format or input.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }

    /**
     * Handles GET requests to analyze expenses by category for a user within a date range.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/expenses-by-category".equals(pathInfo)) {
            try {
                Long userId = Long.parseLong(request.getParameter("userId"));
                String fromDate = request.getParameter("fromDate");
                String toDate = request.getParameter("toDate");
                if (fromDate == null || toDate == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Invalid input. fromDate and toDate are mandatory.");
                    return;
                }
                Map<String, BigDecimal> expensesByCategory = reportService
                        .analyzeExpensesByCategory(userId, fromDate, toDate);
                if (!expensesByCategory.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(expensesByCategory));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("No expenses found for the user in the specified date range.");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid user ID or date format.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }
}