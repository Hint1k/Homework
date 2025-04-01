package com.demo.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * The {@code TransactionDto} class represents a data transfer object (DTO) for transaction-related information.
 * It encapsulates details such as transaction ID, user ID, amount, category, date, description, and type.
 * This class is used to transfer transaction data between layers of the application, such as between the API
 * layer and the persistence layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    @Schema(description = "Unique identifier of the transaction", example = "1")
    private Long transactionId;

    @Schema(description = "ID of the user who made the transaction", example = "2")
    private Long userId;

    @Schema(description = "Transaction amount", example = "100.00")
    private BigDecimal amount;

    @Schema(description = "Transaction category ID", example = "Food")
    private String category;

    @Schema(description = "Date of transaction (YYYY-MM-DD)", example = "2025-04-10")
    private LocalDate date;

    @Schema(description = "Additional transaction details", example = "Grocery shopping")
    private String description;

    @Schema(description = "Transaction type (INCOME/EXPENSE)", example = "EXPENSE")
    private String type;
}