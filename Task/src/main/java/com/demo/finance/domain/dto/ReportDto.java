package com.demo.finance.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * The {@code ReportDto} class represents a data transfer object (DTO) for report-related information.
 * It encapsulates details such as report ID, user ID, total income, total expenses, and balance.
 * This class is used to transfer report data between layers of the application, such as between the API
 * layer and the persistence layer.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {

    private Long reportId;
    private Long userId;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;

    /**
     * Compares this {@code ReportDto} object to another object for equality. Two {@code ReportDto} objects
     * are considered equal if their report ID, user ID, total income, total expenses, and balance are the same.
     *
     * @param o the object to compare to
     * @return {@code true} if this object is equal to the provided object, otherwise {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReportDto reportDto = (ReportDto) o;
        return Objects.equals(reportId, reportDto.reportId) && Objects.equals(userId, reportDto.userId)
                && Objects.equals(totalIncome, reportDto.totalIncome)
                && Objects.equals(totalExpense, reportDto.totalExpense) && Objects.equals(balance, reportDto.balance);
    }

    /**
     * Generates a hash code for this {@code ReportDto} object. The hash code is based on the report ID,
     * user ID, total income, total expenses, and balance.
     *
     * @return a hash code for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(reportId, userId, totalIncome, totalExpense, balance);
    }

    /**
     * Returns a string representation of this {@code ReportDto} object. The string includes all fields
     * of the report, such as report ID, user ID, total income, total expenses, and balance.
     *
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        return "ReportDto{" +
                "reportId=" + reportId +
                ", userId=" + userId +
                ", totalIncome=" + totalIncome +
                ", totalExpense=" + totalExpense +
                ", balance=" + balance +
                '}';
    }
}