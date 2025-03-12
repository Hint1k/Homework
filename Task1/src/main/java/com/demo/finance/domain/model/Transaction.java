package com.demo.finance.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import com.demo.finance.domain.utils.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a financial transaction, including details such as the transaction ID, user ID,
 * amount, category, date, description, and type (income or expense).
 */
@Setter
@Getter
@AllArgsConstructor
public class Transaction {

    private final Long transactionId;
    private final Long userId;
    private BigDecimal amount;
    private String category;
    private LocalDate date;
    private String description;
    private Type type;

    /**
     * Checks if the transaction date is within the specified date range.
     *
     * @param from The start date of the range.
     * @param to The end date of the range.
     * @return {@code true} if the transaction date is within the range, otherwise {@code false}.
     */
    public boolean isWithinDateRange(LocalDate from, LocalDate to) {
        return (date.isAfter(from) || date.isEqual(from)) && (date.isBefore(to) || date.isEqual(to));
    }

    /**
     * Compares this transaction to another object for equality. Two transactions are considered equal if
     * their transaction ID, user ID, amount, category, date, description, and type are the same.
     *
     * @param o The object to compare to.
     * @return {@code true} if this transaction is equal to the provided object, otherwise {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId) && Objects.equals(userId, that.userId)
                && Objects.equals(amount, that.amount) && Objects.equals(category, that.category)
                && Objects.equals(date, that.date) && Objects.equals(description, that.description)
                && type == that.type;
    }

    /**
     * Generates a hash code for this transaction. The hash code is based on the transaction ID, user ID,
     * amount, category, date, description, and type.
     *
     * @return A hash code for this transaction.
     */
    @Override
    public int hashCode() {
        return Objects.hash(transactionId, userId, amount, category, date, description, type);
    }

    /**
     * Returns a string representation of the transaction, including the transaction ID, amount, category,
     * date, and type.
     *
     * @return A string representation of the transaction.
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", date=" + date +
                ", type=" + type +
                '}';
    }
}