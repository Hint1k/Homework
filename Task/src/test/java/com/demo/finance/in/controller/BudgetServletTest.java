package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.Budget;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.out.service.BudgetService;
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

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetServletTest {

    @Mock private BudgetService budgetService;
    @Mock private ValidationUtils validationUtils;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private PrintWriter printWriter;
    private BudgetServlet budgetServlet;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        budgetServlet = new BudgetServlet(budgetService, objectMapper, validationUtils);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    @DisplayName("Set monthly budget - Success scenario")
    void testSetMonthlyBudget_Success() throws Exception {
        String requestBody = "{\"monthlyLimit\": 5000.0}";
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        when(request.getPathInfo()).thenReturn("/");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(validationUtils.validateBudgetJson(any(), eq(Mode.BUDGET), eq(1L)))
                .thenReturn(BigDecimal.valueOf(5000.0));
        when(budgetService.setMonthlyBudget(eq(1L), any())).thenReturn(new Budget());

        budgetServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("Budget generated successfully"));
    }

    @Test
    @DisplayName("Set monthly budget - Invalid JSON format")
    void testSetMonthlyBudget_InvalidJsonFormat() throws Exception {
        when(request.getPathInfo()).thenReturn("/");

        budgetServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(printWriter).write(contains("Invalid JSON format or input."));
    }

    @Test
    @DisplayName("Get budget data - Success scenario")
    void testGetBudgetData_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        Map<String, Object> budgetData = new HashMap<>();
        budgetData.put("monthlyLimit", BigDecimal.valueOf(5000.0));
        budgetData.put("totalExpenses", BigDecimal.valueOf(3000.0));

        when(request.getPathInfo()).thenReturn("/budget");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(budgetService.getBudgetData(eq(1L))).thenReturn(budgetData);

        budgetServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("Budget retrieved successfully"));
    }

    @Test
    @DisplayName("Get budget data - Budget not found")
    void testGetBudgetData_BudgetNotFound() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        when(request.getPathInfo()).thenReturn("/budget");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(budgetService.getBudgetData(eq(1L))).thenReturn(null);

        budgetServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(printWriter).write(contains("Budget not found for the user."));
    }

    @Test
    @DisplayName("Endpoint not found - POST request")
    void testDoPost_EndpointNotFound() throws Exception {
        when(request.getPathInfo()).thenReturn("");

        budgetServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(printWriter).write(contains("Endpoint not found"));
    }

    @Test
    @DisplayName("Endpoint not found - GET request")
    void testDoGet_EndpointNotFound() throws Exception {
        when(request.getPathInfo()).thenReturn("");

        budgetServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(printWriter).write(contains("Endpoint not found"));
    }
}