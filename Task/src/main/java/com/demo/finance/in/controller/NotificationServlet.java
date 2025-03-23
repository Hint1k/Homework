package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.out.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * The {@code NotificationServlet} class is a servlet that handles HTTP requests related to fetching
 * budget and goal notifications for users. It interacts with the notification service to retrieve
 * relevant notifications and returns appropriate responses.
 */
@WebServlet("/api/notifications/*")
public class NotificationServlet extends HttpServlet {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new instance of {@code NotificationServlet} with the required dependencies.
     *
     * @param notificationService the service responsible for fetching notifications
     * @param objectMapper        the object mapper for JSON serialization and deserialization
     */
    public NotificationServlet(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
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
            try {
                UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
                Long userId = userDto.getUserId();
                String notification = notificationService.fetchBudgetNotification(userId);
                if (notification != null && !notification.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(Map.of("message", notification)));
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
        } else if ("/goal".equals(pathInfo)) {
            try {
                UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
                Long userId = userDto.getUserId();
                String notification = notificationService.fetchGoalNotification(userId);
                if (notification != null && !notification.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(Map.of("message", notification)));
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
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
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