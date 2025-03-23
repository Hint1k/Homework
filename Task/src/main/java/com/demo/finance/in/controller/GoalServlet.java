package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.GoalDto;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.GoalMapper;
import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.domain.utils.PaginationParams;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.ValidationException;
import com.demo.finance.out.service.GoalService;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code GoalServlet} class is a servlet that handles HTTP requests related to goal operations,
 * such as creating, retrieving, updating, and deleting goals. It validates incoming JSON data,
 * interacts with services for business logic, and returns appropriate responses.
 */
@WebServlet("/api/goals/*")
public class GoalServlet extends HttpServlet {

    private final GoalService goalService;
    private final ObjectMapper objectMapper;
    private final ValidationUtils validationUtils;

    /**
     * Constructs a new instance of {@code GoalServlet} with the required dependencies.
     *
     * @param goalService       the service responsible for goal-related operations
     * @param objectMapper      the object mapper for JSON serialization and deserialization
     * @param validationUtils   the utility for validating incoming JSON data
     */
    public GoalServlet(GoalService goalService, ObjectMapper objectMapper,
                       ValidationUtils validationUtils) {
        this.goalService = goalService;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
        this.validationUtils = validationUtils;
    }

    /**
     * Handles POST requests for creating a new goal.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/".equals(pathInfo)) {
            try {
                String json = readRequestBody(request);
                GoalDto goalDto = validationUtils.validateGoalJson(json, Mode.GOAL_CREATE);
                Long goalId = goalService.createGoal(goalDto);
                if (goalId != null) {
                    Goal goal = goalService.getGoal(goalId);
                    if (goal != null) {
                        GoalDto goalDtoCreated = GoalMapper.INSTANCE.toDto(goal);
                        Map<String, Object> responseBody = Map.of(
                                "message", "Goal created successfully",
                                "data", goalDtoCreated,
                                "timestamp", java.time.Instant.now().toString()
                        );
                        response.setStatus(HttpServletResponse.SC_CREATED);
                        response.setContentType("application/json");
                        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                    } else {
                        sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                "Failed to retrieve goal details.");
                    }
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                            "Failed to create goal.");
                }
            } catch (ValidationException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            } catch (Exception e) {
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "An error occurred while creating the goal.");
            }
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    /**
     * Handles GET requests for retrieving paginated goals or a specific goal by ID.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo)) {
            try {
                UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
                Long userId = userDto.getUserId();
                String json = readRequestBody(request);
                PaginationParams paginationRequest = objectMapper.readValue(json, PaginationParams.class);
                PaginationParams params = validationUtils.validatePaginationParams(
                        String.valueOf(paginationRequest.page()),
                        String.valueOf(paginationRequest.size())
                );
                PaginatedResponse<GoalDto> paginatedResponse = goalService
                        .getPaginatedGoalsForUser(userId, params.page(), params.size());
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("data", paginatedResponse.data());
                responseMap.put("metadata", Map.of(
                        "user_id", userId,
                        "totalItems", paginatedResponse.totalItems(),
                        "totalPages", paginatedResponse.totalPages(),
                        "currentPage", paginatedResponse.currentPage(),
                        "pageSize", paginatedResponse.pageSize()
                ));
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(responseMap));
            } catch (IllegalArgumentException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, objectMapper.writeValueAsString(Map.of(
                        "error", "Invalid request parameters",
                        "message", e.getMessage()
                )));
            }
        } else if (pathInfo.startsWith("/")) {
            try {
                UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
                Long userId = userDto.getUserId();
                Long goalId = validationUtils.parseGoalId(pathInfo.substring(1), Mode.GET);
                Goal goal = goalService.getGoalByUserIdAndGoalId(userId, goalId);
                if (goal != null) {
                    GoalDto goalDto = GoalMapper.INSTANCE.toDto(goal);
                    Map<String, Object> responseBody = Map.of(
                            "message", "Goal found successfully",
                            "data", goalDto,
                            "timestamp", java.time.Instant.now().toString()
                    );
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                            "Goal not found or you are not the owner of the goal.");
                }
            } catch (NumberFormatException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid goal ID.");
            }
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    /**
     * Handles PUT requests for updating an existing goal.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
                Long userId = userDto.getUserId();
                String goalIdString = pathInfo.substring(1);
                String json = readRequestBody(request);
                GoalDto goalDto = validationUtils.validateGoalJson(json, Mode.GOAL_UPDATE, goalIdString);
                boolean success = goalService.updateGoal(goalDto, userId);
                if (success) {
                    Goal goal = goalService.getGoal(goalDto.getGoalId());
                    if (goal != null) {
                        GoalDto goalDtoUpdated = GoalMapper.INSTANCE.toDto(goal);
                        Map<String, Object> responseBody = Map.of(
                                "message", "Goal updated successfully",
                                "data", goalDtoUpdated,
                                "timestamp", java.time.Instant.now().toString()
                        );
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setContentType("application/json");
                        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                    } else {
                        sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                "Failed to retrieve goal details.");
                    }
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                            "Failed to update goal or you are not the owner of the goal.");
                }
            } catch (NumberFormatException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid goal ID.");
            } catch (ValidationException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            } catch (Exception e) {
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "An error occurred while updating the goal.");
            }
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    /**
     * Handles DELETE requests for deleting a goal by its ID.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
                Long userId = userDto.getUserId();
                String goalIdString = pathInfo.substring(1);
                Long goalId = validationUtils.parseGoalId(goalIdString, Mode.GOAL_DELETE);
                boolean success = goalService.deleteGoal(userId, goalId);
                if (success) {
                    Map<String, Object> responseBody = Map.of(
                            "message", "Goal deleted successfully",
                            "goal id", goalId,
                            "timestamp", java.time.Instant.now().toString()
                    );
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                            "Failed to delete goal or you are not the owner of the goal.");
                }
            } catch (NumberFormatException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid goal ID");
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