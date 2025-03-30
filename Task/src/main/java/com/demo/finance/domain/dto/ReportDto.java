package com.demo.finance.domain.dto;

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

    private Long reportId;
    private Long userId;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
}