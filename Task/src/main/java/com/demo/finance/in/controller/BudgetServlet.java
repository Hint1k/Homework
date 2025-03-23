package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.Budget;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.out.service.BudgetService;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * The {@code BudgetServlet} class is a servlet that handles HTTP requests related to budget operations,
 * such as setting a monthly budget and retrieving budget data for users. It validates incoming JSON data,
 * interacts with services for business logic, and returns appropriate responses.
 */
@WebServlet("/api/budgets/*")
public class BudgetServlet extends HttpServlet {

    private final BudgetService budgetService;
    private final ObjectMapper objectMapper;
    private final ValidationUtils validationUtils;

    /**
     * Constructs a new instance of {@code BudgetServlet} with the required dependencies.
     *
     * @param budgetService    the service responsible for budget-related operations
     * @param objectMapper     the object mapper for JSON serialization and deserialization
     * @param validationUtils  the utility for validating incoming JSON data
     */
    public BudgetServlet(BudgetService budgetService, ObjectMapper objectMapper, ValidationUtils validationUtils) {
        this.budgetService = budgetService;
        this.objectMapper = objectMapper;
        this.validationUtils = validationUtils;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Handles POST requests for setting a monthly budget for the authenticated user.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo)) {
            try {
                UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
                Long userId = userDto.getUserId();
                String json = readRequestBody(request);
                BigDecimal limit = validationUtils.validateBudgetJson(json, Mode.BUDGET, userId);
                if (limit != null) {
                    Budget budget = budgetService.setMonthlyBudget(userId, limit);
                    if (budget != null) {
                        Map<String, Object> responseBody = Map.of(
                                "message", "Budget generated successfully",
                                "data", budget,
                                "timestamp", java.time.Instant.now().toString()
                        );
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setContentType("application/json");
                        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                    } else {
                        sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                "Failed to retrieve budget details.");
                    }
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                            "Monthly limit must be provided.");
                }
            } catch (Exception e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid JSON format or input.");
            }
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    /**
     * Handles GET requests for retrieving budget data for the authenticated user.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/budget".equals(pathInfo)) {
            try {
                UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
                Long userId = userDto.getUserId();
                Map<String, Object> budgetData = budgetService.getBudgetData(userId);
                if (budgetData != null) {
                    Map<String, Object> responseBody = Map.of(
                            "message", "Budget retrieved successfully",
                            "data", budgetData,
                            "timestamp", java.time.Instant.now().toString()
                    );
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                            "Budget not found for the user.");
                }
            } catch (NumberFormatException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID.");
            }
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    /**
     * Reads and returns the body of the HTTP request as a JSON string.
     *
     * @param request the HTTP servlet request
     * @return the JSON string from the request body
     * @throws IOException if an I/O error occurs while reading the request body
     */
    private String readRequestBody(HttpServletRequest request) throws IOException {
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
     * Sends an error response with the specified status code and error message.
     *
     * @param response     the HTTP servlet response
     * @param statusCode   the HTTP status code to set in the response
     * @param errorMessage the error message to include in the response body
     * @throws IOException if an I/O error occurs while writing the response
     */
    private void sendErrorResponse(HttpServletResponse response, int statusCode, String errorMessage)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        Map<String, String> errorResponse = Map.of("error", errorMessage);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}