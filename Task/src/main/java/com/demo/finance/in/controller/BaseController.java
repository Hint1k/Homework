package com.demo.finance.in.controller;

import com.demo.finance.domain.utils.PaginatedResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseController {

    protected <T> ResponseEntity<Map<String, Object>> buildSuccessResponse(HttpStatus status, String message, T data) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", message);
        responseBody.put("timestamp", Instant.now().toString());
        if (data != null) {
            responseBody.put("data", data);
        }
        return new ResponseEntity<>(responseBody, status);
    }

    protected ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String errorMessage) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", errorMessage);
        errorResponse.put("timestamp", Instant.now().toString());
        return new ResponseEntity<>(errorResponse, status);
    }

    protected ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String errorMessage, Map<String, String> details) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", errorMessage);
        errorResponse.putAll(details);
        errorResponse.put("timestamp", Instant.now().toString());
        return new ResponseEntity<>(errorResponse, status);
    }

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