package com.demo.finance.out.service;

import com.demo.finance.domain.model.Report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface ReportService {

    Report generateUserReport(Long userId);

    Report generateReportByDate(Long userId, LocalDate from, LocalDate to);

    Map<String, BigDecimal> analyzeExpensesByCategory(Long userId, LocalDate from, LocalDate to);
}