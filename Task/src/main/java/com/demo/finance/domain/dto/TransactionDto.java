package com.demo.finance.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * The {@code TransactionDto} class represents a data transfer object (DTO) for transaction-related information.
 * It encapsulates details such as transaction ID, user ID, amount, category, date, description, and type.
 * This class is used to transfer transaction data between layers of the application, such as between the API
 * layer and the persistence layer.
 */
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

    /**
     * Compares this {@code TransactionDto} object to another object for equality. Two {@code TransactionDto}
     * objects are considered equal if their transaction ID, user ID, amount, category, date, description,
     * and type are the same.
     *
     * @param o the object to compare to
     * @return {@code true} if this object is equal to the provided object, otherwise {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TransactionDto that = (TransactionDto) o;
        return Objects.equals(transactionId, that.transactionId) && Objects.equals(userId, that.userId)
                && Objects.equals(amount, that.amount) && Objects.equals(category, that.category)
                && Objects.equals(date, that.date) && Objects.equals(description, that.description)
                && Objects.equals(type, that.type);
    }

    /**
     * Generates a hash code for this {@code TransactionDto} object. The hash code is based on the transaction ID,
     * user ID, amount, category, date, description, and type.
     *
     * @return a hash code for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(transactionId, userId, amount, category, date, description, type);
    }

    /**
     * Returns a string representation of this {@code TransactionDto} object. The string includes all fields
     * of the transaction, such as transaction ID, user ID, amount, category, date, description, and type.
     *
     * @return a string representation of this object
     */
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