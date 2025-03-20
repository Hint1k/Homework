package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.BudgetDto;
import com.demo.finance.domain.mapper.BudgetMapper;
import com.demo.finance.domain.model.Budget;
import com.demo.finance.out.service.BudgetService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * The {@code BudgetServlet} class handles incoming HTTP requests related to budget management.
 * It provides endpoints for setting and retrieving a user's budget.
 */
@WebServlet("/api/budget/*")
public class BudgetServlet extends HttpServlet {

    private final BudgetService budgetService;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a {@code BudgetServlet} with the specified {@code BudgetService}.
     *
     * @param budgetService the {@code BudgetService} used for managing budgets
     */
    public BudgetServlet(BudgetService budgetService) {
        this.budgetService = budgetService;
        this.objectMapper = new ObjectMapper();
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
        if ("/".equals(pathInfo)) {
            try {
                StringBuilder jsonBody = new StringBuilder();
                String line;
                try (BufferedReader reader = request.getReader()) {
                    while ((line = reader.readLine()) != null) {
                        jsonBody.append(line);
                    }
                }
                BudgetDto budgetDto = objectMapper.readValue(jsonBody.toString(), BudgetDto.class);
                BigDecimal monthlyLimit = budgetDto.getMonthlyLimit();
                Long userId = budgetDto.getUserId();
                if (userId == null || monthlyLimit == null || monthlyLimit.compareTo(BigDecimal.ZERO) <= 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Invalid input. User ID and monthly limit must be provided.");
                    return;
                }
                budgetService.setMonthlyBudget(userId, monthlyLimit);
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(budgetDto));
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid JSON format or input.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
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
        if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                Long userId = Long.parseLong(pathInfo.substring(1));
                String formattedBudget = budgetService.getFormattedBudget(userId);
                if (formattedBudget != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    BudgetDto budgetDto = BudgetMapper.INSTANCE.toDto(
                            new Budget(null, userId, null, null)
                    );
                    budgetDto.setMonthlyLimit(new BigDecimal(formattedBudget.split("/")[1].trim()));
                    budgetDto.setCurrentExpenses(new BigDecimal(formattedBudget.split("/")[0].trim()));
                    response.getWriter().write(objectMapper.writeValueAsString(budgetDto));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("Budget not found for the user.");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid user ID.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }
}