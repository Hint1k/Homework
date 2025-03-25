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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServletTest {

    @Mock private GoalService goalService;
    @Mock private ValidationUtils validationUtils;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private PrintWriter printWriter;
    @Spy private GoalMapper goalMapper = GoalMapper.INSTANCE;
    private GoalServlet goalServlet;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        goalServlet = new GoalServlet(goalService, objectMapper, validationUtils);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    @DisplayName("Get paginated goals - Success scenario")
    void testGetPaginatedGoals_Success() throws Exception {
        String requestBody = "{\"page\": 1, \"size\": 10}";
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        when(request.getPathInfo()).thenReturn("/");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(validationUtils.validatePaginationParams(requestBody, Mode.PAGE))
                .thenReturn(new PaginationParams(1, 10));
        when(goalService.getPaginatedGoalsForUser(1L, 1, 10))
                .thenReturn(new PaginatedResponse<>(Collections.emptyList(),
                        10, 0, 1, 10));

        goalServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("data"));
    }

    @Test
    @DisplayName("Get goal by ID - Success scenario")
    void testGetGoalById_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        when(request.getPathInfo()).thenReturn("/1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(validationUtils.parseLong("1")).thenReturn(1L);
        when(goalService.getGoalByUserIdAndGoalId(1L, 1L)).thenReturn(new Goal());

        goalServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("Goal found successfully"));
    }

    @Test
    @DisplayName("Update goal - Success scenario")
    void testUpdateGoal_Success() throws Exception {
        String requestBody =
                "{\"userId\": 1, \"goalName\": \"Save More\", \"targetAmount\": 1500.0, \"duration\": 12}";
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        when(request.getPathInfo()).thenReturn("/1");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(validationUtils.validateGoalJson(any(), eq(Mode.GOAL_UPDATE), eq("1"))).thenReturn(new GoalDto());
        when(goalService.updateGoal(any(), eq(1L))).thenReturn(true);
        when(goalService.getGoal(any())).thenReturn(new Goal());

        goalServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("Goal updated successfully"));
    }

    @Test
    @DisplayName("Delete goal - Success scenario")
    void testDeleteGoal_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        when(request.getPathInfo()).thenReturn("/1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(validationUtils.parseLong("1")).thenReturn(1L);
        when(goalService.deleteGoal(1L, 1L)).thenReturn(true);

        goalServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("Goal deleted successfully"));
    }

    @Test
    @DisplayName("Get goal by ID - Not found")
    void testGetGoalById_NotFound() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        when(request.getPathInfo()).thenReturn("/1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(validationUtils.parseLong("1")).thenReturn(1L);
        when(goalService.getGoalByUserIdAndGoalId(1L, 1L)).thenReturn(null);

        goalServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(printWriter).write(contains("Goal not found or you are not the owner of the goal"));
    }

    @Test
    @DisplayName("Update goal - ValidationException")
    void testUpdateGoal_ValidationException() throws Exception {
        String requestBody = "{\"userId\": 1, \"goalName\": \"Save More\", \"targetAmount\": -1500.0}";

        when(request.getPathInfo()).thenReturn("/1");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(new UserDto());
        when(validationUtils.validateGoalJson(any(), eq(Mode.GOAL_UPDATE), eq("1")))
                .thenThrow(new ValidationException("Target amount must be positive"));

        goalServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(printWriter).write(contains("Target amount must be positive"));
    }

    @Test
    @DisplayName("Delete goal - Invalid goal ID")
    void testDeleteGoal_InvalidGoalId() throws Exception {
        when(request.getPathInfo()).thenReturn("/invalid");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(new UserDto());
        when(validationUtils.parseLong("invalid")).thenThrow(new NumberFormatException("Invalid goal ID"));

        goalServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(printWriter).write(contains("Invalid goal ID"));
    }

    @Test
    @DisplayName("Endpoint not found - GET request")
    void testDoGet_EndpointNotFound() throws Exception {
        when(request.getPathInfo()).thenReturn("");

        goalServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(printWriter).write(contains("Endpoint not found"));
    }

    @Test
    @DisplayName("Create goal - ValidationException")
    void testCreateGoal_ValidationException() throws Exception {
        String requestBody = "{\"userId\": 1, \"goalName\": \"Save Money\", \"targetAmount\": -1000.0}";

        UserDto currentUser = new UserDto();
        currentUser.setUserId(1L);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(currentUser);

        when(request.getPathInfo()).thenReturn("/");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(validationUtils.validateJson(any(), eq(Mode.GOAL_CREATE), any()))
                .thenThrow(new ValidationException("Target amount must be positive"));

        goalServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(printWriter).write(contains("Target amount must be positive"));
    }

    @Test
    @DisplayName("Create goal - Success scenario")
    void testCreateGoal_Success() throws Exception {
        String requestBody = "{\"userId\": 1, \"goalName\": \"Save Money\", \"targetAmount\": 1000.0, "
                + "\"duration\": 6, \"startTime\": \"2023-10-01\"}";
        GoalDto goalDto = new GoalDto();
        goalDto.setUserId(1L);
        goalDto.setGoalName("Save Money");
        goalDto.setTargetAmount(BigDecimal.valueOf(1000.0));
        goalDto.setDuration(6);

        UserDto currentUser = new UserDto();
        currentUser.setUserId(1L);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(currentUser);

        when(request.getPathInfo()).thenReturn("/");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(validationUtils.validateJson(any(), eq(Mode.GOAL_CREATE), any())).thenReturn(goalDto);
        when(goalService.createGoal(any(), anyLong())).thenReturn(1L);
        when(goalService.getGoal(1L)).thenReturn(new Goal());

        goalServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(printWriter).write(contains("Goal created successfully"));
    }
}