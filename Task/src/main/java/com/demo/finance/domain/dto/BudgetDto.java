package com.demo.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * The {@code BudgetDto} class represents a data transfer object (DTO) for budget-related information.
 * It encapsulates details such as budget ID, user ID, monthly limit, and current expenses.
 * This class is used to transfer budget data between layers of the application, such as between the API
 * layer and the persistence layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDto {

    @Schema(description = "Unique identifier of the budget", example = "1")
    private Long budgetId;

    @Schema(description = "ID of the user who owns the budget", example = "2")
    private Long userId;

    @Schema(description = "Monthly spending limit amount", example = "1500.00")
    private BigDecimal monthlyLimit;

    @Schema(description = "Current month's expenses so far", example = "750.00")
    private BigDecimal currentExpenses;
}