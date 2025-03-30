package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.ReportDatesDto;
import com.demo.finance.domain.dto.ReportDto;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.ReportMapper;
import com.demo.finance.domain.model.Report;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.ValidationException;
import com.demo.finance.out.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Map;
import java.math.BigDecimal;

/**
 * The {@code ReportController} class is a REST controller that provides endpoints for generating various types
 * of financial reports for the currently logged-in user. It supports generating reports by date range, analyzing
 * expenses by category, and creating general financial reports.
 * <p>
 * This controller leverages validation utilities to ensure that incoming requests meet the required constraints
 * and formats. It also uses a service layer to perform business logic related to reports and a mapper to convert
 * between entities and DTOs.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController extends BaseController {

    private final ReportService reportService;
    private final ValidationUtils validationUtils;
    private final ReportMapper reportMapper;

    /**
     * Constructs a new {@code ReportController} instance with the required dependencies.
     *
     * @param reportService   the service responsible for report-related operations
     * @param validationUtils the utility for validating request parameters and DTOs
     * @param reportMapper    the mapper for converting between report entities and DTOs
     */
    @Autowired
    public ReportController(ReportService reportService, ValidationUtils validationUtils, ReportMapper reportMapper) {
        this.reportService = reportService;
        this.validationUtils = validationUtils;
        this.reportMapper = reportMapper;
    }

    /**
     * Generates a financial report for the currently logged-in user based on a specified date range.
     * <p>
     * This endpoint validates the provided date range and delegates the request to the report service
     * to generate the report. If the operation succeeds, a success response containing the report data
     * is returned; otherwise, an error response is returned.
     *
     * @param reportDatesDto the request body containing the date range for the report
     * @param currentUser    the currently logged-in user retrieved from the session
     * @return a success response containing the generated report or an error response if validation fails
     */
    @PostMapping("/by-date")
    public ResponseEntity<Map<String, Object>> generateReportByDate(
            @RequestBody ReportDatesDto reportDatesDto, @SessionAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            ReportDatesDto reportDates = validationUtils.validateRequest(reportDatesDto, Mode.REPORT);
            Report report =
                    reportService.generateReportByDate(userId, reportDates.getFromDate(), reportDates.getToDate());
            if (report != null) {
                ReportDto reportDto = reportMapper.toDto(report);
                return buildSuccessResponse(
                        HttpStatus.OK, "Report by dates generated successfully", reportDto);
            }
            return buildErrorResponse(HttpStatus.NOT_FOUND,
                    "No transactions found for the user in the specified date range.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Analyzes expenses by category for the currently logged-in user within a specified date range.
     * <p>
     * This endpoint validates the provided date range and delegates the request to the report service
     * to analyze expenses. If the operation succeeds, a success response containing the categorized expenses
     * is returned; otherwise, an error response is returned.
     *
     * @param reportDatesDto the request body containing the date range for the analysis
     * @param currentUser    the currently logged-in user retrieved from the session
     * @return a success response containing the categorized expenses or an error response if validation fails
     */
    @GetMapping("/expenses-by-category")
    public ResponseEntity<Map<String, Object>> analyzeExpensesByCategory(
            @RequestBody ReportDatesDto reportDatesDto, @SessionAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            ReportDatesDto reportDates =
                    validationUtils.validateRequest(reportDatesDto, Mode.REPORT);
            Map<String, BigDecimal> expensesByCategory = reportService
                    .analyzeExpensesByCategory(userId, reportDates.getFromDate(), reportDates.getToDate());
            if (!expensesByCategory.isEmpty()) {
                return buildSuccessResponse(
                        HttpStatus.OK, "Expenses generated successfully", expensesByCategory);
            }
            return buildErrorResponse(
                    HttpStatus.NOT_FOUND, "No expenses found for the user in the specified date range.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NumberFormatException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid user ID or date format.");
        }
    }

    /**
     * Generates a general financial report for the currently logged-in user.
     * <p>
     * This endpoint retrieves the user's financial data from the report service. If the data is found,
     * a success response is returned; otherwise, an error response is returned.
     *
     * @param currentUser the currently logged-in user retrieved from the session
     * @return a success response containing the general report or an error response if no data is found
     */
    @GetMapping("/report")
    public ResponseEntity<Map<String, Object>> generateGeneralReport(
            @SessionAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            Report report = reportService.generateUserReport(userId);
            if (report != null) {
                ReportDto reportDto = reportMapper.toDto(report);
                return buildSuccessResponse(HttpStatus.OK, "General report generated successfully", reportDto);
            }
            return buildErrorResponse(HttpStatus.NOT_FOUND, "No reports found for the user.");
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format or input.");
        }
    }
}