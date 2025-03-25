package com.demo.finance.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * The {@code BudgetDto} class represents a data transfer object (DTO) for budget-related information.
 * It encapsulates details such as budget ID, user ID, monthly limit, and current expenses.
 * This class is used to transfer budget data between layers of the application, such as between the API
 * layer and the persistence layer.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDto {

    private Long budgetId;
    private Long userId;
    private BigDecimal monthlyLimit;
    private BigDecimal currentExpenses;

    /**
     * Compares this {@code BudgetDto} object to another object for equality. Two {@code BudgetDto} objects
     * are considered equal if their budget ID, user ID, monthly limit, and current expenses are the same.
     *
     * @param o the object to compare to
     * @return {@code true} if this object is equal to the provided object, otherwise {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BudgetDto budgetDto = (BudgetDto) o;
        return Objects.equals(budgetId, budgetDto.budgetId) && Objects.equals(userId, budgetDto.userId)
                && Objects.equals(monthlyLimit, budgetDto.monthlyLimit)
                && Objects.equals(currentExpenses, budgetDto.currentExpenses);
    }

    /**
     * Generates a hash code for this {@code BudgetDto} object. The hash code is based on the budget ID,
     * user ID, monthly limit, and current expenses.
     *
     * @return a hash code for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(budgetId, userId, monthlyLimit, currentExpenses);
    }

    /**
     * Returns a string representation of this {@code BudgetDto} object. The string includes all fields
     * of the budget, such as budget ID, user ID, monthly limit, and current expenses.
     *
     * @return a string representation of this object
     */
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