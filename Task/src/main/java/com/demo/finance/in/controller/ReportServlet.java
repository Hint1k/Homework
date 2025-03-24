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

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * The {@code ReportServlet} class is a servlet that handles HTTP requests related to report operations,
 * such as generating reports by date, analyzing expenses by category, and retrieving general user reports.
 * It extends the {@code BaseServlet} to reuse common functionality.
 */
@WebServlet("/api/reports/*")
public class ReportServlet extends BaseServlet {

    private final ReportService reportService;

    /**
     * Constructs a new instance of {@code ReportServlet} with the required dependencies.
     *
     * @param reportService   the service responsible for report-related operations
     * @param objectMapper    the object mapper for JSON serialization and deserialization
     * @param validationUtils the utility for validating incoming JSON data
     */
    public ReportServlet(ReportService reportService, ObjectMapper objectMapper, ValidationUtils validationUtils) {
        super(validationUtils, objectMapper);
        this.reportService = reportService;
    }

    /**
     * Handles POST requests for generating reports by a specified date range.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/by-date".equals(pathInfo)) {
            handleGenerateReportByDate(request, response);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    /**
     * Handles GET requests for retrieving expenses by category or generating a general user report.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/expenses-by-category".equals(pathInfo)) {
            handleAnalyzeExpensesByCategory(request, response);
        } else if ("/report".equals(pathInfo)) {
            handleGenerateGeneralReport(request, response);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    /**
     * Handles POST requests for generating reports by a specified date range.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleGenerateReportByDate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
            Long userId = userDto.getUserId();
            String json = readRequestBody(request);
            Map<String, LocalDate> reportDates = validationUtils.validateReport(json, Mode.REPORT, userId);
            Report report = reportService
                    .generateReportByDate(userId, reportDates.get("fromDate"), reportDates.get("toDate"));
            if (report != null) {
                ReportDto reportDto = ReportMapper.INSTANCE.toDto(report);
                sendSuccessResponse(response, HttpServletResponse.SC_OK,
                        "Report by dates generated successfully", reportDto);
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
    }

    /**
     * Handles GET requests for analyzing expenses by category within a specified date range.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleAnalyzeExpensesByCategory(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
            Long userId = userDto.getUserId();
            String json = readRequestBody(request);
            Map<String, LocalDate> reportDates = validationUtils.validateReport(json, Mode.REPORT, userId);
            Map<String, BigDecimal> expensesByCategory = reportService.analyzeExpensesByCategory(
                    userId, reportDates.get("fromDate"), reportDates.get("toDate")
            );
            if (!expensesByCategory.isEmpty()) {
                sendSuccessResponse(response, HttpServletResponse.SC_OK,
                        "Expenses generated successfully", expensesByCategory);
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
    }

    /**
     * Handles GET requests for generating a general user report.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleGenerateGeneralReport(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
            Long userId = userDto.getUserId();
            Report report = reportService.generateUserReport(userId);
            if (report != null) {
                ReportDto reportDto = ReportMapper.INSTANCE.toDto(report);
                sendSuccessResponse(response, HttpServletResponse.SC_OK,
                        "General report generated successfully", reportDto);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                        "No reports found for the user.");
            }
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid JSON format or input.");
        }
    }
}