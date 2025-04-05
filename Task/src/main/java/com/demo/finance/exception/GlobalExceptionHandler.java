package com.demo.finance.exception;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/**
 * GlobalExceptionHandler is a centralized exception handler for the application.
 * It provides a consistent way to handle exceptions globally and return meaningful error responses to clients.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles HttpMessageNotReadableException, which occurs when the request body contains invalid or malformed JSON.
     * This method sets the HTTP status to 400 Bad Request and returns a JSON response with details about the error.
     *
     * @param response The HttpServletResponse object used to set the status and write the error response.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void handleInvalidJson(HttpServletResponse response) {
        try {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            String message = "{\"error\":\"Invalid JSON format\",\"details\":\"Malformed request body\"}";
            response.getWriter().write(message);
            log.warn("Handled HttpMessageNotReadableException: Invalid JSON format");
        } catch (IOException e) {
            log.error("Failed to write error response for HttpMessageNotReadableException", e);
            try {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"error\":\"An unexpected error occurred\"}");
            } catch (IOException ioException) {
                log.error("Completely failed to write error response", ioException);
            }
        }
    }
}