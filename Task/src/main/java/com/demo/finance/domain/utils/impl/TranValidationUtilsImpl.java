package com.demo.finance.domain.utils.impl;

import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.PaginationParams;
import com.demo.finance.domain.utils.TranValidationUtils;
import com.demo.finance.exception.ValidationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TranValidationUtilsImpl implements TranValidationUtils {

    private final ObjectMapper objectMapper;

    public TranValidationUtilsImpl() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public TransactionDto validateTransactionJson(String json, Mode mode) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            validateRequiredFields(jsonNode, mode);
            TransactionDto transactionDto = objectMapper.readValue(json, TransactionDto.class);
            validateFieldValues(jsonNode, mode);
            return transactionDto;
        } catch (Exception e) {
            throw new ValidationException("Invalid JSON format or validation error: " + e.getMessage());
        }
    }

    @Override
    public TransactionDto validateTransactionJson(String json, Mode mode, String transactionId) {
        Long parsedTransactionId = parseTransactionId(transactionId, mode);
        TransactionDto transactionDto = validateTransactionJson(json, mode);
        transactionDto.setTransactionId(parsedTransactionId);
        return transactionDto;
    }

    @Override
    public Long parseTransactionId(String transactionIdString, Mode mode) {
        try {
            return Long.parseLong(transactionIdString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format of transaction ID: " + transactionIdString);
        }
    }

    @Override
    public PaginationParams validatePaginationParams(String page, String size) {
        int parsedPage = parseInt(page, "Invalid page format: must be an integer.");
        int parsedSize = parseInt(size, "Invalid size format: must be an integer.");
        if (parsedSize > 100) {
            throw new IllegalArgumentException("Size cannot exceed 100.");
        }
        return new PaginationParams(parsedPage, parsedSize);
    }

    private void validateRequiredFields(JsonNode jsonNode, Mode mode) {
        switch (mode) {
            case CREATE:
                checkField(jsonNode, "userId");
                checkField(jsonNode, "amount");
                checkField(jsonNode, "category");
                checkField(jsonNode, "date");
                checkField(jsonNode, "description");
                checkField(jsonNode, "type");
                break;
            case UPDATE:
                checkField(jsonNode, "userId");
                checkField(jsonNode, "amount");
                checkField(jsonNode, "category");
                checkField(jsonNode, "description");
                break;
            case DELETE:
                checkField(jsonNode, "transactionId");
                checkField(jsonNode, "userId");
                break;
            default:
                break;
        }
    }

    private void validateFieldValues(JsonNode jsonNode, Mode mode) {
        if (jsonNode.has("amount")) {
            BigDecimal amount = new BigDecimal(jsonNode.get("amount").asText());
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Amount must be positive.");
            }
        }

        if (jsonNode.has("date")) {
            try {
                LocalDate.parse(jsonNode.get("date").asText());
            } catch (DateTimeParseException e) {
                throw new ValidationException("Invalid date format.");
            }
        }

        if (jsonNode.has("type")) {
            String type = jsonNode.get("type").asText();
            if (!type.equalsIgnoreCase("INCOME") && !type.equalsIgnoreCase("EXPENSE")) {
                throw new ValidationException("Type must be either INCOME or EXPENSE.");
            }
        }
    }

    private void checkField(JsonNode jsonNode, String fieldName) {
        if (!jsonNode.hasNonNull(fieldName)) {
            throw new ValidationException("Missing required field: " + fieldName);
        }
    }

    private int parseInt(String value, String errorMessage) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}