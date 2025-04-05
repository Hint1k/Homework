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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Map;

import static com.demo.finance.domain.utils.SwaggerExamples.Notification.GET_BUDGET_NOTIFICATIONS_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Notification.GET_GOAL_NOTIFICATIONS_SUCCESS;

/**
 * The {@code NotificationController} class is a REST controller that provides endpoints for retrieving
 * notifications related to budgets and goals for the currently logged-in user.
 * <p>
 * This controller delegates notification-related operations to the {@link NotificationService} and ensures
 * that responses are standardized using utility methods from the base controller. It handles scenarios where
 * notifications are not found or errors occur during retrieval.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController extends BaseController {

    private final NotificationService notificationService;

    /**
     * Retrieves a budget-related notification for the currently logged-in user.
     * <p>
     * This endpoint fetches the budget notification for the user identified by their session attributes.
     * If a notification is found, it is returned in the response; otherwise, an appropriate error response
     * is returned.
     *
     * @param currentUser the currently logged-in user retrieved from the session
     * @return a success response containing the budget notification or an error response if no notification
     * is found or an exception occurs
     */
    @GetMapping("/budget")
    @Operation(summary = "Get budget notification", description = "Retrieves budget-related notifications")
    @ApiResponse(responseCode = "200", description = "Budget notification retrieved", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class),
            examples = @ExampleObject(name = "SuccessResponse", value = GET_BUDGET_NOTIFICATIONS_SUCCESS)))
    public ResponseEntity<Map<String, Object>> getBudgetNotification(
            @Parameter(hidden = true) @SessionAttribute("currentUser") UserDto currentUser) {
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
     * Retrieves a goal-related notification for the currently logged-in user.
     * <p>
     * This endpoint fetches the goal notification for the user identified by their session attributes.
     * If a notification is found, it is returned in the response; otherwise, an appropriate error response
     * is returned.
     *
     * @param currentUser the currently logged-in user retrieved from the session
     * @return a success response containing the goal notification or an error response if no notification
     * is found or an exception occurs
     */
    @GetMapping("/goal")
    @Operation(summary = "Get goal notification", description = "Retrieves goal-related notifications")
    @ApiResponse(responseCode = "200", description = "Goal notification retrieved", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class),
            examples = @ExampleObject(name = "SuccessResponse", value = GET_GOAL_NOTIFICATIONS_SUCCESS)))
    public ResponseEntity<Map<String, Object>> getGoalNotification(
            @Parameter(hidden = true) @SessionAttribute("currentUser") UserDto currentUser) {
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