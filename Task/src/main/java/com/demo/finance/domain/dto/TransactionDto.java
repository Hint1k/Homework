package com.demo.finance.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private Long transactionId;
    private Long userId;
    private BigDecimal amount;
    private String category;
    private LocalDate date;
    private String description;
    private String type;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TransactionDto that = (TransactionDto) o;
        return Objects.equals(transactionId, that.transactionId) && Objects.equals(userId, that.userId)
                && Objects.equals(amount, that.amount) && Objects.equals(category, that.category)
                && Objects.equals(date, that.date) && Objects.equals(description, that.description)
                && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, userId, amount, category, date, description, type);
    }

    @Override
    public String toString() {
        return "TransactionDto{" +
                "transactionId=" + transactionId +
                ", userId=" + userId +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}