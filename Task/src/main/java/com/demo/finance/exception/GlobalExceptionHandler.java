package com.demo.finance.exception;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;

/**
 * GlobalExceptionHandler is a centralized exception handler for the application.
 * It provides a consistent way to handle exceptions globally and return meaningful
 * error responses to clients. This class specifically handles JSON parsing errors,
 * invalid date formats, and other malformed request issues, returning appropriate
 * HTTP status codes and error details.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles HttpMessageNotReadableException, which occurs when the request body
     * contains invalid or malformed JSON. This method sets the HTTP status to
     * 400 Bad Request and returns a JSON response with detailed error information.
     *
     * @param ex       The HttpMessageNotReadableException containing parsing error details
     * @param response The HttpServletResponse used to set the status and write the error response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void handleInvalidJson(HttpMessageNotReadableException ex, HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String errorDetails = extractDetailedError(ex);
        String jsonResponse = String.format("{\"error\":\"Invalid JSON format\",\"details\":\"%s\"}", errorDetails);
        try {
            response.getWriter().write(jsonResponse);
            log.warn("Handled HttpMessageNotReadableException: {}", errorDetails);
        } catch (IOException e) {
            log.error("Failed to write error response", e);
            sendInternalServerError(response);
        }
    }

    /**
     * Extracts a detailed error message from the HttpMessageNotReadableException by analyzing
     * its root cause and exception message. This method distinguishes between different types
     * of JSON parsing errors (e.g., invalid date formats, missing commas) and returns a
     * human-readable error description.
     *
     * @param ex The HttpMessageNotReadableException to analyze
     * @return A detailed error message describing the specific parsing issue
     */
    private String extractDetailedError(HttpMessageNotReadableException ex) {
        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof DateTimeParseException) {
            return "Invalid date format. Expected: yyyy-MM-dd (e.g., 2025-01-01)";
        } else if (rootCause instanceof DateTimeException) {
            return "Invalid date value (e.g., month 13 or day 32)";
        } else if (ex.getMessage().contains("Unexpected character") || ex.getMessage().contains("missing comma")) {
            return "Malformed JSON: Check for missing commas or syntax errors";
        }
        return "Malformed request body";
    }

    /**
     * Sends a generic 500 Internal Server Error response when an unexpected error occurs
     * while writing the error response. This is a fallback mechanism for critical failures.
     *
     * @param response The HttpServletResponse used to write the error response
     */
    private void sendInternalServerError(HttpServletResponse response) {
        try {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write("{\"error\":\"Internal server error\"}");
        } catch (IOException e) {
            log.error("Completely failed to write error response", e);
        }
    }
}