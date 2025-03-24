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
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * The {@code GoalServlet} class is a servlet that handles HTTP requests related to goal operations,
 * such as creating, retrieving, updating, and deleting goals. It extends the {@code BaseServlet} to reuse common functionality.
 */
@WebServlet("/api/goals/*")
public class GoalServlet extends BaseServlet {

    private final GoalService goalService;

    /**
     * Constructs a new instance of {@code GoalServlet} with the required dependencies.
     *
     * @param goalService     the service responsible for goal-related operations
     * @param objectMapper    the object mapper for JSON serialization and deserialization
     * @param validationUtils the utility for validating incoming JSON data
     */
    public GoalServlet(GoalService goalService, ObjectMapper objectMapper,
                       ValidationUtils validationUtils) {
        super(validationUtils, objectMapper);
        this.goalService = goalService;
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
            handleCreateGoal(request, response);
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
            handleGetPaginatedGoals(request, response);
        } else if (pathInfo.startsWith("/")) {
            handleGetGoalById(request, response, pathInfo);
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
            handleUpdateGoal(request, response, pathInfo);
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
            handleDeleteGoal(request, response, pathInfo);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    /**
     * Handles POST requests for creating a new goal.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleCreateGoal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String json = readRequestBody(request);
            GoalDto goalDto = validationUtils.validateJson(json, Mode.GOAL_CREATE, GoalDto.class);
            Long goalId = goalService.createGoal(goalDto);
            if (goalId != null) {
                Goal goal = goalService.getGoal(goalId);
                if (goal != null) {
                    GoalDto goalDtoCreated = GoalMapper.INSTANCE.toDto(goal);
                    sendSuccessResponse(response, HttpServletResponse.SC_CREATED,
                            "Goal created successfully", goalDtoCreated);
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
    }

    /**
     * Handles GET requests for retrieving paginated goals.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleGetPaginatedGoals(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Long userId = ((UserDto) request.getSession().getAttribute("currentUser")).getUserId();
            PaginationParams params = getValidatedPaginationParams(request);
            PaginatedResponse<GoalDto> paginatedResponse = goalService
                    .getPaginatedGoalsForUser(userId, params.page(), params.size());
            sendPaginatedResponseWithMetadata(response, userId, paginatedResponse);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request parameters",
                    Map.of("message", e.getMessage()));
        }
    }

    /**
     * Handles GET requests for retrieving a specific goal by ID.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @param pathInfo the path info from the request
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleGetGoalById(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws IOException {
        try {
            UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
            Long userId = userDto.getUserId();
            Long goalId = validationUtils.parseLong(pathInfo.substring(1));
            Goal goal = goalService.getGoalByUserIdAndGoalId(userId, goalId);
            if (goal != null) {
                GoalDto goalDto = GoalMapper.INSTANCE.toDto(goal);
                sendSuccessResponse(response, HttpServletResponse.SC_OK, "Goal found successfully", goalDto);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                        "Goal not found or you are not the owner of the goal.");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid goal ID.");
        }
    }

    /**
     * Handles PUT requests for updating an existing goal.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @param pathInfo the path info from the request
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleUpdateGoal(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws IOException {
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
                    sendSuccessResponse(response, HttpServletResponse.SC_OK,
                            "Goal updated successfully", goalDtoUpdated);
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
    }

    /**
     * Handles DELETE requests for deleting a goal by its ID.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @param pathInfo the path info from the request
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleDeleteGoal(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws IOException {
        try {
            UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
            Long userId = userDto.getUserId();
            String goalIdString = pathInfo.substring(1);
            Long goalId = validationUtils.parseLong(goalIdString);
            boolean success = goalService.deleteGoal(userId, goalId);
            if (success) {
                sendSuccessResponse(response, HttpServletResponse.SC_OK, "Goal deleted successfully", goalId);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Failed to delete goal or you are not the owner of the goal.");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid goal ID");
        }
    }
}