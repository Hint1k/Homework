package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.GoalDto;
import com.demo.finance.domain.mapper.GoalMapper;
import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.service.GoalService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The {@code GoalServlet} class handles incoming HTTP requests related to goal management.
 * It provides endpoints for creating, retrieving, updating, deleting, and filtering goals.
 */
@WebServlet("/api/goals/*")
public class GoalServlet extends HttpServlet {

    private final GoalService goalService;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a {@code GoalServlet} with the specified {@code GoalService}.
     *
     * @param goalService the {@code GoalService} used for managing goals
     */
    public GoalServlet(GoalService goalService) {
        this.goalService = goalService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Handles GET requests to retrieve a single goal or a paginated list of goals.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/".equals(pathInfo)) {
            try {
                int page = Integer.parseInt(request.getParameter("page") != null
                        ? request.getParameter("page") : "1");
                int size = Integer.parseInt(request.getParameter("size") != null
                        ? request.getParameter("size") : "10");
                if (page <= 0 || size <= 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter()
                            .write("Invalid pagination parameters. Page and size must be positive integers.");
                    return;
                }
                User currentUser = (User) request.getSession().getAttribute("currentUser");
                if (currentUser == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("No user is currently logged in.");
                    return;
                }
                Long userId = currentUser.getUserId();
                int offset = (page - 1) * size;
                List<Goal> goals = goalService.getPaginatedGoals(userId, offset, size);
                int totalGoals = goalService.getTotalGoalCount(userId);
                int totalPages = (int) Math.ceil((double) totalGoals / size);
                List<GoalDto> goalDtos = goals.stream().map(GoalMapper.INSTANCE::toDto).toList();
                Map<String, Object> responseMap = Map.of("data", goalDtos, "metadata", Map.of("totalItems",
                        totalGoals, "totalPages", totalPages, "currentPage", page, "pageSize", size));
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(responseMap));
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid pagination parameters.");
            }
        }
        else if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                Long goalId = Long.parseLong(pathInfo.substring(1));
                Goal goal = goalService.getGoal(goalId);
                if (goal != null) {
                    GoalDto goalDto = GoalMapper.INSTANCE.toDto(goal);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(goalDto));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("Goal not found.");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid goal ID.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }

    /**
     * Handles POST requests to create a new goal.
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
                GoalDto goalDto = objectMapper.readValue(jsonBody.toString(), GoalDto.class);
                Goal goal = GoalMapper.INSTANCE.toEntity(goalDto);
                goalService.createGoal(goal.getUserId(), goal.getGoalName(), goal.getTargetAmount(),
                        goal.getDuration());
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(goalDto));
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid JSON format.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }

    /**
     * Handles PUT requests to update an existing goal.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                Long goalId = Long.parseLong(pathInfo.substring(1));
                StringBuilder jsonBody = new StringBuilder();
                String line;
                try (BufferedReader reader = request.getReader()) {
                    while ((line = reader.readLine()) != null) {
                        jsonBody.append(line);
                    }
                }
                GoalDto goalDto = objectMapper.readValue(jsonBody.toString(), GoalDto.class);
                goalDto.setGoalId(goalId);
                Goal goal = GoalMapper.INSTANCE.toEntity(goalDto);
                boolean success = goalService.updateGoal(goal.getGoalId(), goal.getUserId(), goal.getGoalName(),
                        goal.getTargetAmount(), goal.getDuration());
                if (success) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(goalDto));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Failed to update goal.");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid goal ID.");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid JSON format.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }

    /**
     * Handles DELETE requests to delete a goal.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                Long goalId = Long.parseLong(pathInfo.substring(1));
                Long userId = Long.parseLong(request.getParameter("userId"));
                boolean success = goalService.deleteGoal(userId, goalId);
                if (success) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Goal deleted successfully.");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Failed to delete goal.");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid goal ID or user ID.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }
}