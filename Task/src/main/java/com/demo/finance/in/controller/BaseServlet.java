package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.domain.utils.PaginationParams;
import com.demo.finance.domain.utils.ValidationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code BaseServlet} class is a base class for all servlets in the application.
 * It provides common functionality such as reading the request body, sending responses,
 * and handling errors.
 */
public abstract class BaseServlet extends HttpServlet {

    protected final ObjectMapper objectMapper;
    protected final ValidationUtils validationUtils;

    /**
     * Constructs a new instance of {@code BaseServlet} with the required dependencies.
     *
     * @param validationUtils the utility for validating incoming JSON data
     * @param objectMapper    the object mapper for JSON serialization and deserialization
     */
    public BaseServlet(ValidationUtils validationUtils, ObjectMapper objectMapper) {
        this.validationUtils = validationUtils;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Reads and returns the body of the HTTP request as a JSON string.
     *
     * @param request the HTTP servlet request
     * @return the JSON string from the request body
     * @throws IOException if an I/O error occurs while reading the request body
     */
    protected String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder json = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
        }
        return json.toString();
    }

    /**
     * Sends a successful response with the specified status code and response body.
     *
     * @param response     the HTTP servlet response
     * @param statusCode   the HTTP status code to set in the response
     * @param responseBody the response body to include in the response
     * @throws IOException if an I/O error occurs while writing the response
     */
    protected void sendSuccessResponse(HttpServletResponse response, int statusCode, Map<String, Object> responseBody)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }

    /**
     * Sends a successful response with a status code, message, and optional data.
     * <p>
     * This method constructs a response body containing a success message, an optional data field, and a timestamp.
     * The response is sent as JSON with the specified HTTP status code. If no data is provided, the response will omit
     * the "data" field.
     *
     * @param response   the HTTP servlet response to which the success response will be written
     * @param statusCode the HTTP status code to set in the response (e.g., 200 for OK, 201 for Created)
     * @param message    the success message to include in the response body
     * @param data       the data to include in the response body, or {@code null} if no data is needed
     * @param <T>        the type of data to include in the response
     * @throws IOException if an I/O error occurs while writing the response
     */
    protected <T> void sendSuccessResponse(HttpServletResponse response, int statusCode, String message, T data)
            throws IOException {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", message);
        responseBody.put("timestamp", java.time.Instant.now().toString());
        if (data != null) {
            responseBody.put("data", data);
        }
        sendSuccessResponse(response, statusCode, responseBody);
    }

    /**
     * Sends an error response with the specified status code and error message.
     *
     * @param response     the HTTP servlet response
     * @param statusCode   the HTTP status code to set in the response
     * @param errorMessage the error message to include in the response body
     * @throws IOException if an I/O error occurs while writing the response
     */
    protected void sendErrorResponse(HttpServletResponse response, int statusCode, String errorMessage)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        Map<String, String> errorResponse = Map.of("error", errorMessage);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * Sends an error response with the specified status code, error message, and additional details.
     * <p>
     * This method constructs a response body containing an error key-value pair and additional details
     * (e.g., a "message" field). The response is sent as JSON with the specified HTTP status code.
     *
     * @param response     the HTTP servlet response
     * @param statusCode   the HTTP status code to set in the response
     * @param errorMessage the primary error message to include in the response body
     * @param details      a map of additional key-value pairs to include in the response body
     * @throws IOException if an I/O error occurs while writing the response
     */
    protected void sendErrorResponse(HttpServletResponse response, int statusCode, String errorMessage,
                                     Map<String, String> details) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", errorMessage);
        errorResponse.putAll(details);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * Sends a paginated response with the specified data and metadata.
     *
     * @param response the HTTP servlet response
     * @param data     the data to include in the response
     * @param metadata the metadata to include in the response
     * @throws IOException if an I/O error occurs while writing the response
     */
    protected void sendPaginatedResponse(HttpServletResponse response, Object data, Map<String, Object> metadata)
            throws IOException {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", data);
        responseBody.put("metadata", metadata);
        sendSuccessResponse(response, HttpServletResponse.SC_OK, responseBody);
    }

    /**
     * Reads and validates pagination parameters from the request body.
     * <p>
     * This method retrieves the current user from the session, reads the JSON payload from the request body,
     * parses it into a {@link PaginationParams} object, and validates the pagination parameters (page and size).
     * If the user is not logged in or the request body is invalid, appropriate exceptions are thrown.
     *
     * @param request the HTTP servlet request containing the pagination parameters in the request body
     * @return a validated {@link PaginationParams} object representing the page number and page size
     * @throws IOException              if an I/O error occurs while reading the request body
     * @throws IllegalStateException    if no user is currently logged in (i.e., "currentUser" is not found in the session)
     * @throws IllegalArgumentException if the pagination parameters are invalid or missing
     */
    protected PaginationParams getValidatedPaginationParams(HttpServletRequest request) throws IOException {
        UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
        if (userDto == null) {
            throw new IllegalStateException("No user is currently logged in.");
        }
        String json = readRequestBody(request);
        PaginationParams paginationRequest = objectMapper.readValue(json, PaginationParams.class);
        return validationUtils.validatePaginationParams(String.valueOf(paginationRequest.page()),
                String.valueOf(paginationRequest.size()));
    }

    /**
     * Sends a paginated response with metadata included in the response body.
     * <p>
     * This method constructs a metadata map containing details about the pagination (e.g., total items, total pages,
     * current page, page size) and optionally includes the user ID if provided. The response is formatted as JSON and
     * includes both the data and the metadata.
     *
     * @param response          the HTTP servlet response to which the paginated data and metadata will be written
     * @param userId            the ID of the user associated with the paginated data, or {@code null} if not applicable
     * @param paginatedResponse a {@link PaginatedResponse} object containing the paginated data and pagination details
     * @param <T>               the type of data contained in the paginated response
     * @throws IOException if an I/O error occurs while writing the response
     */
    protected <T> void sendPaginatedResponseWithMetadata(HttpServletResponse response, Long userId,
                                                         PaginatedResponse<T> paginatedResponse) throws IOException {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("totalItems", paginatedResponse.totalItems());
        metadata.put("totalPages", paginatedResponse.totalPages());
        metadata.put("currentPage", paginatedResponse.currentPage());
        metadata.put("pageSize", paginatedResponse.pageSize());
        if (userId != null) {
            metadata.put("user_id", userId);
        }
        sendPaginatedResponse(response, paginatedResponse.data(), metadata);
    }
}