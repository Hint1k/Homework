package com.demo.finance.domain.utils.impl;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.PaginationParams;
import com.demo.finance.domain.utils.ValidatedUser;
import com.demo.finance.domain.utils.ValidationUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

public class ValidationUtilsImpl implements ValidationUtils {
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public ValidationUtilsImpl() {
        this.objectMapper = new ObjectMapper();
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    @Override
    public ValidatedUser validateUserJson(String json, Mode mode) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            validateRequiredFields(jsonNode, mode);
            UserDto userDto = objectMapper.readValue(json, UserDto.class);
            validateFieldValues(jsonNode, mode);
            Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
            if (!violations.isEmpty()) {
                StringBuilder errors = new StringBuilder("Validation failed: ");
                for (ConstraintViolation<UserDto> violation : violations) {
                    errors.append(violation.getPropertyPath()).append(" ").append(violation.getMessage()).append("; ");
                }
                throw new ValidationException(errors.toString());
            }
            String password = (mode == Mode.CREATE || mode == Mode.UPDATE || mode == Mode.AUTHENTICATE)
                    ? jsonNode.get("password").asText() : null;
            return new ValidatedUser(userDto, password);
        } catch (Exception e) {
            throw new ValidationException("Invalid JSON format or validation error: " + e.getMessage());
        }
    }

    @Override
    public ValidatedUser validateUserJson(String json, Mode mode, String userId) {
        Long parsedUserId = null;
        try {
            parsedUserId = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid userId format.");
        }
        ValidatedUser validatedUser = validateUserJson(json, mode);
        validatedUser.userDto().setUserId(parsedUserId);
        return validatedUser;
    }

    @Override
    public PaginationParams validatePaginationParams(String page, String size) {
        int parsedPage;
        int parsedSize;
        try {
            parsedPage = Integer.parseInt(page);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid page format: must be an integer.");
        }
        try {
            parsedSize = Integer.parseInt(size);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid size format: must be an integer.");
        }
        if (parsedSize > 100) { // maximum page size to prevent abuse
            throw new IllegalArgumentException("Size cannot exceed 100.");
        }
        return new PaginationParams(parsedPage, parsedSize);
    }

    private void validateRequiredFields(JsonNode jsonNode, Mode mode) {
        switch (mode) {
            case CREATE:
                checkField(jsonNode, "name");
                checkField(jsonNode, "email");
                checkField(jsonNode, "password");
                break;
            case UPDATE:
                checkField(jsonNode, "userId");
                checkField(jsonNode, "name");
                checkField(jsonNode, "email");
                checkField(jsonNode, "password");
                break;
            case AUTHENTICATE:
                checkField(jsonNode, "email");
                checkField(jsonNode, "password");
                break;
            case UPDATE_ROLE:
                checkField(jsonNode, "role");
                break;
            case BLOCK_UNBLOCK:
                checkField(jsonNode, "blocked");
                break;
            case GET:
            case DELETE:
            case GET_USER_TRANSACTIONS:
                break;
        }
    }

    private void validateFieldValues(JsonNode jsonNode, Mode mode) {
        if (mode == Mode.UPDATE || mode == Mode.DELETE || mode == Mode.GET) {
            if (!jsonNode.get("userId").isIntegralNumber()) {
                throw new ValidationException("Invalid userId: must be a non-null Long.");
            }
        }

        if ((mode == Mode.CREATE || mode == Mode.UPDATE) && jsonNode.has("password")) {
            String password = jsonNode.get("password").asText();
            if (password.isBlank()) {
                throw new ValidationException("Password cannot be empty.");
            }
        }
    }

    private void checkField(JsonNode jsonNode, String fieldName) {
        if (!jsonNode.hasNonNull(fieldName)) {
            throw new ValidationException("Missing required field: " + fieldName);
        }
    }
}