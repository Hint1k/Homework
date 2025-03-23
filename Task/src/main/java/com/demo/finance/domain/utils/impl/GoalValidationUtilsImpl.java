package com.demo.finance.domain.utils.impl;

import com.demo.finance.domain.dto.GoalDto;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.PaginationParams;
import com.demo.finance.domain.utils.GoalValidationUtils;
import com.demo.finance.exception.ValidationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class GoalValidationUtilsImpl implements GoalValidationUtils {

    private final ObjectMapper objectMapper;

    public GoalValidationUtilsImpl() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public GoalDto validateGoalJson(String json, Mode mode) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            validateRequiredFields(jsonNode, mode);
            GoalDto goalDto = objectMapper.readValue(json, GoalDto.class);
            validateFieldValues(jsonNode, mode);
            return goalDto;
        } catch (Exception e) {
            throw new ValidationException("Invalid JSON format or validation error: " + e.getMessage());
        }
    }

    @Override
    public GoalDto validateGoalJson(String json, Mode mode, String goalId) {
        Long parsedGoalId = parseGoalId(goalId, mode);
        GoalDto goalDto = validateGoalJson(json, mode);
        goalDto.setGoalId(parsedGoalId);
        return goalDto;
    }

    @Override
    public Long parseGoalId(String goalIdString, Mode mode) {
        try {
            return Long.parseLong(goalIdString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format of goal ID: " + goalIdString);
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
                checkField(jsonNode, "goalName");
                checkField(jsonNode, "targetAmount");
                checkField(jsonNode, "duration");
                checkField(jsonNode, "startTime");
                break;
            case UPDATE:
                checkField(jsonNode, "userId");
                checkField(jsonNode, "goalName");
                checkField(jsonNode, "targetAmount");
                checkField(jsonNode, "duration");
                break;
            case DELETE:
                checkField(jsonNode, "goalId");
                checkField(jsonNode, "userId");
                break;
            default:
                break;
        }
    }

    private void validateFieldValues(JsonNode jsonNode, Mode mode) {
        if (jsonNode.has("targetAmount")) {
            BigDecimal targetAmount = new BigDecimal(jsonNode.get("targetAmount").asText());
            if (targetAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Target amount must be positive.");
            }
        }

        if (jsonNode.has("savedAmount")) {
            BigDecimal savedAmount = new BigDecimal(jsonNode.get("savedAmount").asText());
            if (savedAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Saved amount must be positive.");
            }
        }

        if (jsonNode.has("duration")) {
            int duration = jsonNode.get("duration").asInt();
            if (duration <= 0) {
                throw new ValidationException("Duration must be a positive integer.");
            }
        }

        if (jsonNode.has("startTime")) {
            try {
                LocalDate.parse(jsonNode.get("startTime").asText());
            } catch (DateTimeParseException e) {
                throw new ValidationException("Invalid start time format.");
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