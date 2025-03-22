package com.demo.finance.domain.utils.impl;

import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.utils.PaginationParams;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.ValidationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.regex.Pattern;

public class ValidationUtilsImpl implements ValidationUtils {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$");
    private final ObjectMapper objectMapper;

    public ValidationUtilsImpl() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public UserDto validateUserJson(String json, Mode mode) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            validateRequiredFields(jsonNode, mode);
            UserDto userDto = objectMapper.readValue(json, UserDto.class);
            validateFieldValues(jsonNode, mode);
            return userDto;
        } catch (Exception e) {
            throw new ValidationException("Invalid JSON format or validation error: " + e.getMessage());
        }
    }

    @Override
    public UserDto validateUserJson(String json, Mode mode, String userId) {
        Long parsedUserId = parseUserId(userId, mode);
        UserDto userDto = validateUserJson(json, mode);
        userDto.setUserId(parsedUserId);
        return userDto;
    }

    @Override
    public UserDto validateUserJson(String json, Mode mode, Long userId) {
        UserDto userDto = validateUserJson(json, mode);
        Long userIdJson = userDto.getUserId();
        if (userIdJson.equals(userId)) {
            return userDto;
        } else {
            throw new ValidationException("A user can't update other users");
        }
    }

    @Override
    public Long parseUserId(String userIdString, Mode mode) {
        Long userId = parseLong(userIdString, "Invalid userId format");
        if (userId == 1 && mode == Mode.DELETE) {
            throw new ValidationException("Default Admin cannot be deleted");
        }
        if (userId == 1 && mode == Mode.UPDATE_ROLE) {
            throw new ValidationException("Default Admin role cannot be changed");
        }
        if (userId == 1 && mode == Mode.BLOCK_UNBLOCK) {
            throw new ValidationException("Default Admin cannot be blocked or unblocked");
        }
        return userId;
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
            case UPDATE:
                checkField(jsonNode, "userId");
                checkField(jsonNode, "name");
                checkField(jsonNode, "email");
                checkField(jsonNode, "password");
            case REGISTER:
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
            case GET, DELETE:
                break;
        }
    }

    private void validateFieldValues(JsonNode jsonNode, Mode mode) {
        if (jsonNode.has("userId") && !jsonNode.get("userId").isIntegralNumber()) {
            throw new ValidationException("Invalid userId: must be a non-null Long.");
        }

        if (jsonNode.has("email") && !isValidEmail(jsonNode.get("email").asText())) {
            throw new ValidationException("Invalid email format.");
        }

        if (jsonNode.has("password")) {
            String password = jsonNode.get("password").asText();
            if (password.isBlank()) {
                throw new ValidationException("Password cannot be empty.");
            }
        }

        if (jsonNode.has("blocked") && !jsonNode.get("blocked").isBoolean()) {
            throw new ValidationException("Blocked field must be a boolean.");
        }
    }

    private void checkField(JsonNode jsonNode, String fieldName) {
        if (!jsonNode.hasNonNull(fieldName)) {
            throw new ValidationException("Missing required field: " + fieldName);
        }
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private Long parseLong(String value, String errorMessage) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage);
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