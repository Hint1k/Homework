package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.Budget;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.out.service.BudgetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * The {@code BudgetServlet} class is a servlet that handles HTTP requests related to budget operations,
 * such as setting a monthly budget and retrieving budget data for users. It extends the {@code BaseServlet} to reuse common functionality.
 */
@WebServlet("/api/budgets/*")
public class BudgetServlet extends BaseServlet {

    private final BudgetService budgetService;

    /**
     * Constructs a new instance of {@code BudgetServlet} with the required dependencies.
     *
     * @param budgetService    the service responsible for budget-related operations
     * @param objectMapper     the object mapper for JSON serialization and deserialization
     * @param validationUtils  the utility for validating incoming JSON data
     */
    public BudgetServlet(BudgetService budgetService, ObjectMapper objectMapper, ValidationUtils validationUtils) {
        super(validationUtils, objectMapper);
        this.budgetService = budgetService;
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
            handleSetMonthlyBudget(request, response);
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
            handleGetBudgetData(request, response);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    /**
     * Handles POST requests for setting a monthly budget for the authenticated user.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleSetMonthlyBudget(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                    sendSuccessResponse(response, HttpServletResponse.SC_OK, responseBody);
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
    }

    /**
     * Handles GET requests for retrieving budget data for the authenticated user.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleGetBudgetData(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                sendSuccessResponse(response, HttpServletResponse.SC_OK, responseBody);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                        "Budget not found for the user.");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID.");
        }
    }
}