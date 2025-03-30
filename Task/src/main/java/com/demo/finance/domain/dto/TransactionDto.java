package com.demo.finance.domain.dto;

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

    private Long transactionId;
    private Long userId;
    private BigDecimal amount;
    private String category;
    private LocalDate date;
    private String description;
    private String type;
}