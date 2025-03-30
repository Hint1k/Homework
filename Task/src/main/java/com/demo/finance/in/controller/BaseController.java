package com.demo.finance.in.controller;

import com.demo.finance.domain.utils.PaginatedResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code BaseController} class is an abstract base class that provides utility methods for building
 * standardized HTTP responses. These methods are used to construct success, error, and paginated responses
 * in a consistent format across all controllers in the application.
 * <p>
 * This class ensures that all responses include a timestamp and adhere to a predefined structure,
 * improving the consistency and readability of API responses.
 */
public abstract class BaseController {

    /**
     * Builds a standardized success response with the given status, message, and data.
     * <p>
     * This method constructs a response body containing the provided message, a timestamp, and optional data.
     * It is typically used to return successful API responses with relevant information.
     *
     * @param <T>     the type of the data to include in the response
     * @param status  the HTTP status code for the response
     * @param message the success message to include in the response
     * @param data    the data to include in the response (can be null)
     * @return a {@link ResponseEntity} containing the success response
     */
    protected <T> ResponseEntity<Map<String, Object>> buildSuccessResponse(HttpStatus status, String message, T data) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", message);
        responseBody.put("timestamp", Instant.now().toString());
        if (data != null) {
            responseBody.put("data", data);
        }
        return new ResponseEntity<>(responseBody, status);
    }

    /**
     * Builds a standardized error response with the given status and error message.
     * <p>
     * This method constructs a response body containing the provided error message and a timestamp.
     * It is typically used to return error responses when an exception or validation failure occurs.
     *
     * @param status       the HTTP status code for the response
     * @param errorMessage the error message to include in the response
     * @return a {@link ResponseEntity} containing the error response
     */
    protected ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String errorMessage) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", errorMessage);
        errorResponse.put("timestamp", Instant.now().toString());
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Builds a detailed error response with additional error details.
     * <p>
     * This method constructs a response body containing the provided error message, a timestamp, and
     * additional key-value pairs representing error details. It is useful for returning more granular
     * error information to the client.
     *
     * @param status       the HTTP status code for the response
     * @param errorMessage the error message to include in the response
     * @param details      a map of additional error details to include in the response
     * @return a {@link ResponseEntity} containing the detailed error response
     */
    protected ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String errorMessage, Map<String, String> details) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", errorMessage);
        errorResponse.putAll(details);
        errorResponse.put("timestamp", Instant.now().toString());
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Builds a paginated response containing metadata and data for paginated results.
     * <p>
     * This method constructs a response body containing the paginated data, metadata about the pagination
     * (e.g., total items, total pages, current page, page size), and an optional user ID. It is typically
     * used to return paginated API responses.
     *
     * @param <T>               the type of the paginated data
     * @param userId            the ID of the user associated with the paginated data (can be null)
     * @param paginatedResponse the paginated response object containing data and metadata
     * @return a {@link ResponseEntity} containing the paginated response
     */
    protected <T> ResponseEntity<Map<String, Object>> buildPaginatedResponse(
            Long userId, PaginatedResponse<T> paginatedResponse) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("totalItems", paginatedResponse.totalItems());
        metadata.put("totalPages", paginatedResponse.totalPages());
        metadata.put("currentPage", paginatedResponse.currentPage());
        metadata.put("pageSize", paginatedResponse.pageSize());
        if (userId != null) {
            metadata.put("user_id", userId);
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", paginatedResponse.data());
        responseBody.put("metadata", metadata);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}