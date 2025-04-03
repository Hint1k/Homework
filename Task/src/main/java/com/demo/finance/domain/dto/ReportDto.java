package com.demo.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * The {@code ReportDto} class represents a data transfer object (DTO) for report-related information.
 * It encapsulates details such as report ID, user ID, total income, total expenses, and balance.
 * This class is used to transfer report data between layers of the application, such as between the API
 * layer and the persistence layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {

    @Schema(description = "Unique identifier of the report", example = "1")
    private Long reportId;

    @Schema(description = "ID of the user who generated the report", example = "2")
    private Long userId;

    @Schema(description = "Total income for the period", example = "3000.00")
    private BigDecimal totalIncome;

    @Schema(description = "Total expenses for the period", example = "2000.00")
    private BigDecimal totalExpense;

    @Schema(description = "Current balance (income - expenses)", example = "1000.00")
    private BigDecimal balance;
}