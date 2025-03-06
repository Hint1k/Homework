package com.demo.finance.domain.model;

import java.time.LocalDate;
import java.util.Objects;

public class Transaction {

    public enum Type { INCOME, EXPENSE }
    private final String id;
    private final String userId; // Owner of the transaction
    private double amount;
    private String category;
    private LocalDate date;
    private String description;
    private Type type;

    public Transaction(String id, String userId, double amount, String category, LocalDate date, String description,
                       Type type) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Double.compare(amount, that.amount) == 0 && Objects.equals(id, that.id)
                && Objects.equals(userId, that.userId) && Objects.equals(category, that.category)
                && Objects.equals(date, that.date) && Objects.equals(description, that.description)
                && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, amount, category, date, description, type);
    }

    @Override
    public String toString() {
        return "[" + type + "] " + category + ": " + amount + " on " + date;
    }
}