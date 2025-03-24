package com.demo.finance.in.controller;

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
     * Sends a paginated response with the specified data and metadata.
     *
     * @param response         the HTTP servlet response
     * @param data             the data to include in the response
     * @param metadata         the metadata to include in the response
     * @throws IOException if an I/O error occurs while writing the response
     */
    protected void sendPaginatedResponse(HttpServletResponse response, Object data, Map<String, Object> metadata)
            throws IOException {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", data);
        responseBody.put("metadata", metadata);
        sendSuccessResponse(response, HttpServletResponse.SC_OK, responseBody);
    }
}