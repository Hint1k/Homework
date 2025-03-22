package com.demo.finance.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDto {

    private Long budgetId;

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    @NotNull(message = "Monthly limit is mandatory")
    @DecimalMin(value = "0.01", message = "Monthly limit must be greater than zero")
    private BigDecimal monthlyLimit;

    private BigDecimal currentExpenses;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BudgetDto budgetDto = (BudgetDto) o;
        return Objects.equals(budgetId, budgetDto.budgetId) && Objects.equals(userId, budgetDto.userId)
                && Objects.equals(monthlyLimit, budgetDto.monthlyLimit)
                && Objects.equals(currentExpenses, budgetDto.currentExpenses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(budgetId, userId, monthlyLimit, currentExpenses);
    }

    @Override
    public String toString() {
        return "BudgetDto{" +
                "budgetId=" + budgetId +
                ", userId=" + userId +
                ", monthlyLimit=" + monthlyLimit +
                ", currentExpenses=" + currentExpenses +
                '}';
    }
}