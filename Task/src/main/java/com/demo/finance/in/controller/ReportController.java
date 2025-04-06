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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.math.BigDecimal;

import static com.demo.finance.domain.utils.SwaggerExamples.Report.EXPENSES_BY_CATEGORY_REQUEST;
import static com.demo.finance.domain.utils.SwaggerExamples.Report.EXPENSES_BY_CATEGORY_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Report.GET_REPORT_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Report.MISSING_REPORT_FIELD_RESPONSE;
import static com.demo.finance.domain.utils.SwaggerExamples.Report.REPORT_BY_DATE_REQUEST;
import static com.demo.finance.domain.utils.SwaggerExamples.Report.REPORT_BY_DATE_SUCCESS;

/**
 * REST controller for managing financial reports.
 * <p>
 * Provides endpoints to generate various types of financial reports such as general report,
 * report by date range, and expenses categorized by type. Relies on {@code ReportService} and
 * {@code ReportMapper} for processing and formatting report data, and {@code ValidationUtils}
 * for validating request payloads.
 * </p>
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController extends BaseController {

    private final ReportService reportService;
    private final ValidationUtils validationUtils;
    private final ReportMapper reportMapper;

    /**
     * Generates a financial report based on a provided date range.
     * <p>
     * Validates the input date range and retrieves a report for the specified user.
     * Returns a {@code ReportDto} on success, or an error if input validation fails
     * or no transactions exist in the date range.
     * </p>
     *
     * @param reportDatesDto the date range for the report
     * @param currentUser    the currently authenticated user
     * @return a {@code ResponseEntity} containing the generated report or an error message
     */
    @PostMapping("/by-date")
    @Operation(summary = "Generate report by date", description = "Creates financial report for date range")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Date range", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReportDatesDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = REPORT_BY_DATE_REQUEST)))
    @ApiResponse(responseCode = "200", description = "Report generated successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReportDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = REPORT_BY_DATE_SUCCESS)))
    @ApiResponse(responseCode = "400", description = "Bad Request - Missing report field ", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "ValidationError",
            value = MISSING_REPORT_FIELD_RESPONSE)))
    public ResponseEntity<Map<String, Object>> generateReportByDate(
            @RequestBody ReportDatesDto reportDatesDto, @RequestAttribute("currentUser") UserDto currentUser) {
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
     * Analyzes the user's expenses grouped by category within a specified date range.
     * <p>
     * Validates the input and returns a map of categories and total expenses. If no data
     * is found, responds with a 404 error.
     * </p>
     *
     * @param reportDatesDto the date range for analysis
     * @param currentUser    the currently authenticated user
     * @return a {@code ResponseEntity} with the category-expense map or an error message
     */
    @PostMapping("/expenses-by-category")
    @Operation(summary = "Get expenses by category", description = "Analyzes expenses by category for date range")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Date range", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReportDatesDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = EXPENSES_BY_CATEGORY_REQUEST)))
    @ApiResponse(responseCode = "200", description = "Expenses analysis successful", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Map.class),
            examples = @ExampleObject(name = "SuccessResponse", value = EXPENSES_BY_CATEGORY_SUCCESS)))
    public ResponseEntity<Map<String, Object>> analyzeExpensesByCategory(
            @RequestBody ReportDatesDto reportDatesDto, @RequestAttribute("currentUser") UserDto currentUser) {
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
     * Generates a general financial report for the authenticated user.
     * <p>
     * Returns the user's complete financial summary, including total income,
     * expenses, and balance.
     * </p>
     *
     * @param currentUser the currently authenticated user
     * @return a {@code ResponseEntity} containing the general report or an error message
     */
    @GetMapping("/report")
    @Operation(summary = "Get general report", description = "Generates overall financial report")
    @ApiResponse(responseCode = "200", description = "General report generated", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReportDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = GET_REPORT_SUCCESS)))
    public ResponseEntity<Map<String, Object>> generateGeneralReport(
            @Parameter(hidden = true) @RequestAttribute("currentUser") UserDto currentUser) {
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