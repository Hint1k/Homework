package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.out.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    private MockMvc mockMvc;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private NotificationController notificationController;
    private UserDto currentUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
        currentUser = new UserDto();
        currentUser.setUserId(1L);
    }

    @Test
    @DisplayName("Get budget notification - Success scenario")
    void testGetBudgetNotification_Success() throws Exception {
        String notificationMessage = "Your monthly budget is exceeded.";
        when(notificationService.fetchBudgetNotification(1L)).thenReturn(notificationMessage);

        mockMvc.perform(get("/api/notifications/budget")
                        .sessionAttr("currentUser", currentUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(notificationMessage));

        verify(notificationService, times(1)).fetchBudgetNotification(1L);
    }

    @Test
    @DisplayName("Get budget notification - No notifications found")
    void testGetBudgetNotification_NoNotificationsFound() throws Exception {
        when(notificationService.fetchBudgetNotification(1L)).thenReturn(null);

        mockMvc.perform(get("/api/notifications/budget")
                        .sessionAttr("currentUser", currentUser))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("No budget notification found for the user."));

        verify(notificationService, times(1)).fetchBudgetNotification(1L);
    }

    @Test
    @DisplayName("Get goal notification - Success scenario")
    void testGetGoalNotification_Success() throws Exception {
        String notificationMessage = "You have reached 80% of your goal.";
        when(notificationService.fetchGoalNotification(1L)).thenReturn(notificationMessage);

        mockMvc.perform(get("/api/notifications/goal")
                        .sessionAttr("currentUser", currentUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(notificationMessage));

        verify(notificationService, times(1)).fetchGoalNotification(1L);
    }

    @Test
    @DisplayName("Get goal notification - No notifications found")
    void testGetGoalNotification_NoNotificationsFound() throws Exception {
        when(notificationService.fetchGoalNotification(1L)).thenReturn("");

        mockMvc.perform(get("/api/notifications/goal")
                        .sessionAttr("currentUser", currentUser))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("No goal notification found for the user."));

        verify(notificationService, times(1)).fetchGoalNotification(1L);
    }

    @Test
    @DisplayName("Get budget notification - Service throws exception")
    void testGetBudgetNotification_ServiceException() throws Exception {
        when(notificationService.fetchBudgetNotification(1L))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/api/notifications/budget")
                        .sessionAttr("currentUser", currentUser))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error")
                        .value("Failed to fetch budget notification."));

        verify(notificationService, times(1)).fetchBudgetNotification(1L);
    }

    @Test
    @DisplayName("Get goal notification - Service throws exception")
    void testGetGoalNotification_ServiceException() throws Exception {
        when(notificationService.fetchGoalNotification(1L))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/api/notifications/goal")
                        .sessionAttr("currentUser", currentUser))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to fetch goal notification."));

        verify(notificationService, times(1)).fetchGoalNotification(1L);
    }
}