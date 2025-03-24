package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.out.service.NotificationService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.demo.finance.domain.utils.ValidationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * The {@code NotificationServlet} class is a servlet that handles HTTP requests related to fetching
 * budget and goal notifications for users. It extends the {@code BaseServlet} to reuse common functionality.
 */
@WebServlet("/api/notifications/*")
public class NotificationServlet extends BaseServlet {

    private final NotificationService notificationService;

    /**
     * Constructs a new instance of {@code NotificationServlet} with the required dependencies.
     *
     * @param notificationService the service responsible for fetching notifications
     * @param objectMapper        the object mapper for JSON serialization and deserialization
     * @param validationUtils     the utility for validating incoming JSON data
     */
    public NotificationServlet(NotificationService notificationService, ObjectMapper objectMapper,
                               ValidationUtils validationUtils) {
        super(validationUtils, objectMapper);
        this.notificationService = notificationService;
    }

    /**
     * Handles GET requests for retrieving budget or goal notifications for the authenticated user.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/budget".equals(pathInfo)) {
            handleBudgetNotification(request, response);
        } else if ("/goal".equals(pathInfo)) {
            handleGoalNotification(request, response);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    /**
     * Handles GET requests for retrieving budget notifications for the authenticated user.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleBudgetNotification(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
            Long userId = userDto.getUserId();
            String notification = notificationService.fetchBudgetNotification(userId);
            if (notification != null && !notification.isEmpty()) {
                Map<String, Object> responseBody = Map.of(
                        "message", notification,
                        "timestamp", java.time.Instant.now().toString()
                );
                sendSuccessResponse(response, HttpServletResponse.SC_OK, responseBody);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                        "No budget notification found for the user.");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID.");
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An error occurred while fetching the budget notification.");
        }
    }

    /**
     * Handles GET requests for retrieving goal notifications for the authenticated user.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleGoalNotification(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
            Long userId = userDto.getUserId();
            String notification = notificationService.fetchGoalNotification(userId);
            if (notification != null && !notification.isEmpty()) {
                Map<String, Object> responseBody = Map.of(
                        "message", notification,
                        "timestamp", java.time.Instant.now().toString()
                );
                sendSuccessResponse(response, HttpServletResponse.SC_OK, responseBody);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                        "No goal notification found for the user.");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID.");
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An error occurred while fetching the goal notification.");
        }
    }
}