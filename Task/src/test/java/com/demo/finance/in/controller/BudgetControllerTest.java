package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.BudgetDto;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.Budget;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.ValidationException;
import com.demo.finance.out.service.BudgetService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {

    private MockMvc mockMvc;
    @Mock
    private BudgetService budgetService;
    @Mock
    private ValidationUtils validationUtils;
    @InjectMocks
    private BudgetController budgetController;
    private UserDto currentUser;
    private BudgetDto budgetDto;
    private Budget budget;
    private Map<String, Object> budgetData;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(budgetController).build();
        currentUser = Instancio.create(UserDto.class);
        currentUser.setUserId(1L);
        budgetDto = Instancio.create(BudgetDto.class);
        budgetDto.setMonthlyLimit(BigDecimal.valueOf(5000.0));
        budget = Instancio.create(Budget.class);
        budget.setMonthlyLimit(BigDecimal.valueOf(5000.0));
        budgetData = new HashMap<>();
        budgetData.put("monthlyLimit", BigDecimal.valueOf(5000.0));
        budgetData.put("totalExpenses", BigDecimal.valueOf(3000.0));
    }

    @Test
    @DisplayName("Set monthly budget - Success scenario")
    void testSetMonthlyBudget_Success() throws Exception {
        when(validationUtils.validateRequest(any(BudgetDto.class), eq(Mode.BUDGET))).thenReturn(budgetDto);
        when(budgetService.setMonthlyBudget(eq(1L), any(BigDecimal.class))).thenReturn(budget);

        mockMvc.perform(post("/api/budgets")
                        .requestAttr("currentUser", currentUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"monthlyLimit\":5000.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Budget generated successfully"))
                .andExpect(jsonPath("$.data.monthlyLimit").value(5000.0));

        verify(validationUtils, times(1)).validateRequest(any(BudgetDto.class), eq(Mode.BUDGET));
        verify(budgetService, times(1)).setMonthlyBudget(eq(1L), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Get budget data - Success scenario")
    void testGetBudgetData_Success() throws Exception {
        when(budgetService.getBudgetData(1L)).thenReturn(budgetData);

        mockMvc.perform(get("/api/budgets/budget")
                        .requestAttr("currentUser", currentUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Budget retrieved successfully"))
                .andExpect(jsonPath("$.data.monthlyLimit").value(5000.0))
                .andExpect(jsonPath("$.data.totalExpenses").value(3000.0));

        verify(budgetService, times(1)).getBudgetData(1L);
    }

    @Test
    @DisplayName("Set monthly budget - Service returns null")
    void testSetMonthlyBudget_ServiceReturnsNull() throws Exception {
        when(validationUtils.validateRequest(any(BudgetDto.class), eq(Mode.BUDGET))).thenReturn(budgetDto);
        when(budgetService.setMonthlyBudget(eq(1L), any(BigDecimal.class))).thenReturn(null);

        mockMvc.perform(post("/api/budgets")
                        .requestAttr("currentUser", currentUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"monthlyLimit\":5000.0}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to retrieve budget details."));

        verify(validationUtils, times(1)).validateRequest(any(BudgetDto.class), eq(Mode.BUDGET));
        verify(budgetService, times(1)).setMonthlyBudget(eq(1L), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Set monthly budget - ValidationException")
    void testSetMonthlyBudget_ValidationException() throws Exception {
        when(validationUtils.validateRequest(any(BudgetDto.class), eq(Mode.BUDGET)))
                .thenThrow(new ValidationException("Monthly limit must be a positive number"));

        mockMvc.perform(post("/api/budgets")
                        .requestAttr("currentUser", currentUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"monthlyLimit\":\"-1000\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Monthly limit must be a positive number"));

        verify(validationUtils, times(1)).validateRequest(any(BudgetDto.class), eq(Mode.BUDGET));
    }

    @Test
    @DisplayName("Get budget data - Budget not found")
    void testGetBudgetData_BudgetNotFound() throws Exception {
        when(budgetService.getBudgetData(1L)).thenReturn(null);

        mockMvc.perform(get("/api/budgets/budget")
                        .requestAttr("currentUser", currentUser))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Budget not found for the user."));

        verify(budgetService, times(1)).getBudgetData(1L);
    }

    @Test
    @DisplayName("Set monthly budget - Missing monthly limit")
    void testSetMonthlyBudget_MissingMonthlyLimit() throws Exception {
        BudgetDto emptyBudgetDto = new BudgetDto();
        when(validationUtils.validateRequest(any(BudgetDto.class), eq(Mode.BUDGET))).thenReturn(emptyBudgetDto);

        mockMvc.perform(post("/api/budgets")
                        .requestAttr("currentUser", currentUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"monthlyLimit\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Monthly limit must be provided."));

        verify(validationUtils, times(1)).validateRequest(any(BudgetDto.class), eq(Mode.BUDGET));
    }
}