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
 * The {@code BudgetServlet} class handles incoming HTTP requests related to budget management.
 * It provides endpoints for setting and retrieving a user's budget.
 */
@WebServlet("/api/budgets/*")
public class BudgetServlet extends HttpServlet {

    private final BudgetService budgetService;
    private final ObjectMapper objectMapper;
    private final ValidationUtils validationUtils;

    public BudgetServlet(BudgetService budgetService, ObjectMapper objectMapper, ValidationUtils validationUtils) {
        this.budgetService = budgetService;
        this.objectMapper = objectMapper;
        this.validationUtils = validationUtils;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Handles POST requests to set a monthly budget for a user.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws IOException if an I/O error occurs
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
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        response.setContentType("application/json");
                        response.getWriter().write("Failed to retrieve budget details.");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json");
                    response.getWriter().write("Monthly limit must be provided.");
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("Invalid JSON format or input.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.getWriter().write("Endpoint not found.");
        }
    }

    /**
     * Handles GET requests to retrieve a user's budget.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws IOException if an I/O error occurs
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
                            "message", "Budget generated successfully",
                            "data", budgetData,
                            "timestamp", java.time.Instant.now().toString()
                    );
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setContentType("application/json");
                    response.getWriter().write("Budget not found for the user.");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("Invalid user ID.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.getWriter().write("Endpoint not found.");
        }
    }

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
}