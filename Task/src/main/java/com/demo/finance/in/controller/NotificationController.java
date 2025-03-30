package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.out.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController extends BaseController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/budget")
    public ResponseEntity<Map<String, Object>> getBudgetNotification(
            @SessionAttribute("currentUser") UserDto currentUser) {
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

    @GetMapping("/goal")
    public ResponseEntity<Map<String, Object>> getGoalNotification(
            @SessionAttribute("currentUser") UserDto currentUser) {
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