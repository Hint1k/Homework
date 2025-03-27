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
import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportController extends BaseController {

    private final ReportService reportService;
    private final ValidationUtils validationUtils;
    private final ReportMapper reportMapper;

    @Autowired
    public ReportController(ReportService reportService, ValidationUtils validationUtils, ReportMapper reportMapper) {
        this.reportService = reportService;
        this.validationUtils = validationUtils;
        this.reportMapper = reportMapper;
    }

    @PostMapping("/by-date")
    public ResponseEntity<Map<String, Object>> generateReportByDate(
            @RequestBody ReportDatesDto reportDatesDto, @SessionAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            Map<String, LocalDate> reportDates =
                    validationUtils.validateReportJson(reportDatesDto, Mode.REPORT, userId);
            Report report =
                    reportService.generateReportByDate(userId, reportDates.get("fromDate"), reportDates.get("toDate"));
            if (report != null) {
                ReportDto reportDto = reportMapper.toDto(report);
                return buildSuccessResponse(
                        HttpStatus.OK, "Report by dates generated successfully", reportDto);
            }
            return buildErrorResponse(HttpStatus.NOT_FOUND,
                    "No transactions found for the user in the specified date range.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format or input.");
        }
    }

    @GetMapping("/expenses-by-category")
    public ResponseEntity<Map<String, Object>> analyzeExpensesByCategory(
            @RequestBody ReportDatesDto reportDatesDto, @SessionAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            Map<String, LocalDate> reportDates =
                    validationUtils.validateReportJson(reportDatesDto, Mode.REPORT, userId);
            Map<String, BigDecimal> expensesByCategory = reportService.analyzeExpensesByCategory(
                    userId, reportDates.get("fromDate"), reportDates.get("toDate"));
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
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while analyzing expenses by category.");
        }
    }

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