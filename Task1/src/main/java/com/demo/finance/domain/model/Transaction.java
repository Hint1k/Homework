package com.demo.finance.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import com.demo.finance.domain.utils.Type;

/**
 * Represents a financial transaction, including details such as the transaction ID, user ID,
 * amount, category, date, description, and type (income or expense).
 */
public class Transaction {

    private final Long transactionId;
    private final Long userId;
    private BigDecimal amount;
    private String category;
    private LocalDate date;
    private String description;
    private Type type;

    /**
     * Constructs a new Transaction with the specified details.
     *
     * @param transactionId The unique ID of the transaction.
     * @param userId The ID of the user who made the transaction.
     * @param amount The amount of the transaction.
     * @param category The category of the transaction (e.g., food, entertainment).
     * @param date The date the transaction occurred.
     * @param description A brief description of the transaction.
     * @param type The type of the transaction (income or expense).
     */
    public Transaction(Long transactionId, Long userId, BigDecimal amount, String category,
                       LocalDate date, String description, Type type) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
        this.type = type;
    }

    /**
     * Gets the unique ID of the transaction.
     *
     * @return The transaction ID.
     */
    public Long getTransactionId() {
        return transactionId;
    }

    /**
     * Gets the ID of the user who made the transaction.
     *
     * @return The user ID.
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Gets the amount of the transaction.
     *
     * @return The transaction amount.
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets a new amount for the transaction.
     *
     * @param amount The new transaction amount.
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Gets the category of the transaction (e.g., food, entertainment).
     *
     * @return The transaction category.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets a new category for the transaction.
     *
     * @param category The new category.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the date the transaction occurred.
     *
     * @return The transaction date.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets a new date for the transaction.
     *
     * @param date The new transaction date.
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Gets the description of the transaction.
     *
     * @return The transaction description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets a new description for the transaction.
     *
     * @param description The new transaction description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the type of the transaction (income or expense).
     *
     * @return The transaction type.
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets a new type for the transaction.
     *
     * @param type The new transaction type.
     */
    public void setType(Type type) {
        this.type = type;
    }

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