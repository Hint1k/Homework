package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.out.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.demo.finance.domain.utils.SwaggerExamples.Notification.GET_BUDGET_NOTIFICATIONS_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Notification.GET_GOAL_NOTIFICATIONS_SUCCESS;

/**
 * REST controller for managing user notifications.
 * <p>
 * Provides endpoints to retrieve budget- and goal-related notifications for the authenticated user.
 * Relies on {@code NotificationService} to fetch notifications based on the user's activity.
 * </p>
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController extends BaseController {

    private final NotificationService notificationService;

    /**
     * Retrieves budget-related notifications for the authenticated user.
     * <p>
     * Calls the {@code NotificationService} to fetch budget notifications and returns a success response
     * if a notification is found. If no notification exists, a 404 response is returned.
     * </p>
     *
     * @param currentUser the currently authenticated user
     * @return a {@code ResponseEntity} containing the budget notification message or an error message
     */
    @GetMapping("/budget")
    @Operation(summary = "Get budget notification", description = "Retrieves budget-related notifications")
    @ApiResponse(responseCode = "200", description = "Budget notification retrieved", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class),
            examples = @ExampleObject(name = "SuccessResponse", value = GET_BUDGET_NOTIFICATIONS_SUCCESS)))
    public ResponseEntity<Map<String, Object>> getBudgetNotification(
            @Parameter(hidden = true) @RequestAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            String notification = notificationService.fetchBudgetNotification(userId);
            if (notification != null && !notification.isEmpty()) {
                return buildSuccessResponse(HttpStatus.OK, notification, null);
            }
            return buildErrorResponse(HttpStatus.NOT_FOUND, "No budget notification found for the user.");
        } catch (Exception e) {
            return buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch budget notification.");
        }
    }

    /**
     * Retrieves goal-related notifications for the authenticated user.
     * <p>
     * Calls the {@code NotificationService} to fetch goal notifications and returns a success response
     * if a notification is found. If no notification exists, a 404 response is returned.
     * </p>
     *
     * @param currentUser the currently authenticated user
     * @return a {@code ResponseEntity} containing the goal notification message or an error message
     */
    @GetMapping("/goal")
    @Operation(summary = "Get goal notification", description = "Retrieves goal-related notifications")
    @ApiResponse(responseCode = "200", description = "Goal notification retrieved", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class),
            examples = @ExampleObject(name = "SuccessResponse", value = GET_GOAL_NOTIFICATIONS_SUCCESS)))
    public ResponseEntity<Map<String, Object>> getGoalNotification(
            @Parameter(hidden = true) @RequestAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            String notification = notificationService.fetchGoalNotification(userId);
            if (notification != null && !notification.isEmpty()) {
                return buildSuccessResponse(HttpStatus.OK, notification, null);
            }
            return buildErrorResponse(HttpStatus.NOT_FOUND, "No goal notification found for the user.");
        } catch (Exception e) {
            return buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch goal notification.");
        }
    }
}