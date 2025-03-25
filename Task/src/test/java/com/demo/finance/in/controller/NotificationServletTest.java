package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.out.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.PrintWriter;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServletTest {

    @Mock private NotificationService notificationService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private PrintWriter printWriter;
    @Mock private ValidationUtils validationUtils;
    private NotificationServlet notificationServlet;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        notificationServlet = new NotificationServlet(notificationService, objectMapper, validationUtils);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    @DisplayName("Get budget notification - Success scenario")
    void testGetBudgetNotification_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        when(request.getPathInfo()).thenReturn("/budget");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(notificationService.fetchBudgetNotification(1L)).thenReturn("Your monthly budget is exceeded.");

        notificationServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("Your monthly budget is exceeded."));
    }

    @Test
    @DisplayName("Get budget notification - No notifications found")
    void testGetBudgetNotification_NoNotificationsFound() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        when(request.getPathInfo()).thenReturn("/budget");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(notificationService.fetchBudgetNotification(1L)).thenReturn(null);

        notificationServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(printWriter).write(contains("No budget notification found for the user."));
    }

    @Test
    @DisplayName("Get goal notification - Success scenario")
    void testGetGoalNotification_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        when(request.getPathInfo()).thenReturn("/goal");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(notificationService.fetchGoalNotification(1L)).thenReturn("You have reached 80% of your goal.");

        notificationServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("You have reached 80% of your goal."));
    }

    @Test
    @DisplayName("Get goal notification - No notifications found")
    void testGetGoalNotification_NoNotificationsFound() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        when(request.getPathInfo()).thenReturn("/goal");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(notificationService.fetchGoalNotification(1L)).thenReturn("");

        notificationServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(printWriter).write(contains("No goal notification found for the user."));
    }

    @Test
    @DisplayName("Endpoint not found - GET request")
    void testDoGet_EndpointNotFound() throws Exception {
        when(request.getPathInfo()).thenReturn("/unknown");

        notificationServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(printWriter).write(contains("Endpoint not found"));
    }
}