package com.demo.finance.domain.dto;

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

    private Long budgetId;
    private Long userId;
    private BigDecimal monthlyLimit;
    private BigDecimal currentExpenses;
}