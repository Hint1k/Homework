package com.demo.finance.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {

    private Long reportId;

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    @NotNull(message = "Total income is mandatory")
    @DecimalMin(value = "0.0", message = "Total income must be non-negative")
    private BigDecimal totalIncome;

    @NotNull(message = "Total expense is mandatory")
    @DecimalMin(value = "0.0", message = "Total expense must be non-negative")
    private BigDecimal totalExpense;

    private BigDecimal balance;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReportDto reportDto = (ReportDto) o;
        return Objects.equals(reportId, reportDto.reportId) && Objects.equals(userId, reportDto.userId)
                && Objects.equals(totalIncome, reportDto.totalIncome)
                && Objects.equals(totalExpense, reportDto.totalExpense) && Objects.equals(balance, reportDto.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportId, userId, totalIncome, totalExpense, balance);
    }

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