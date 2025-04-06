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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {

    private MockMvc mockMvc;
    @Mock
    private GoalService goalService;
    @Mock
    private ValidationUtils validationUtils;
    @Mock
    private GoalMapper goalMapper;
    @InjectMocks
    private GoalController goalController;
    private UserDto currentUser;
    private GoalDto goalDto;
    private Goal goal;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(goalController).build();
        currentUser = new UserDto();
        currentUser.setUserId(1L);
        goalDto = new GoalDto();
        goalDto.setGoalId(1L);
        goalDto.setGoalName("Save for Vacation");
        goalDto.setTargetAmount(BigDecimal.valueOf(5000.0));
        goalDto.setDuration(12);
        goal = new Goal();
        goal.setGoalId(1L);
    }

    private PaginationParams createPaginationParams() {
        return new PaginationParams(1, 10);
    }

    @Test
    @DisplayName("Create goal - Success scenario")
    void testCreateGoal_Success() throws Exception {
        String content = "{\"goalName\":\"Save for Vacation\",\"targetAmount\":5000.0, "
                + "\"duration\":12,\"startTime\":\"2023-10-01\"}";
        when(validationUtils.validateRequest(any(GoalDto.class), eq(Mode.GOAL_CREATE)))
                .thenReturn(goalDto);
        when(goalService.createGoal(any(GoalDto.class), anyLong()))
                .thenReturn(1L);
        when(goalService.getGoal(1L))
                .thenReturn(goal);
        when(goalMapper.toDto(any(Goal.class)))
                .thenReturn(goalDto);

        mockMvc.perform(post("/api/goals")
                        .requestAttr("currentUser", currentUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Goal created successfully"))
                .andExpect(jsonPath("$.data.goalId").value(1));

        verify(validationUtils, times(1))
                .validateRequest(any(GoalDto.class), eq(Mode.GOAL_CREATE));
        verify(goalService, times(1))
                .createGoal(any(GoalDto.class), eq(1L));
        verify(goalService, times(1)).getGoal(1L);
        verify(goalMapper, times(1)).toDto(any(Goal.class));
    }

    @Test
    @DisplayName("Get paginated goals - Success scenario")
    void testGetPaginatedGoals_Success() throws Exception {
        PaginationParams params = createPaginationParams();
        PaginatedResponse<GoalDto> response = new PaginatedResponse<>(
                List.of(goalDto), 1, 1, 1, 10);

        when(validationUtils.validateRequest(any(PaginationParams.class), eq(Mode.PAGE)))
                .thenReturn(params);
        when(goalService.getPaginatedGoalsForUser(1L, 1, 10))
                .thenReturn(response);

        mockMvc.perform(get("/api/goals")
                        .requestAttr("currentUser", currentUser)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.metadata.user_id").value(1));

        verify(validationUtils, times(1))
                .validateRequest(any(PaginationParams.class), eq(Mode.PAGE));
        verify(goalService, times(1))
                .getPaginatedGoalsForUser(1L, 1, 10);
    }

    @Test
    @DisplayName("Get goal by ID - Success scenario")
    void testGetGoalById_Success() throws Exception {
        when(validationUtils.parseLong("1")).thenReturn(1L);
        when(goalService.getGoalByUserIdAndGoalId(1L, 1L))
                .thenReturn(goal);
        when(goalMapper.toDto(any(Goal.class)))
                .thenReturn(goalDto);

        mockMvc.perform(get("/api/goals/1")
                        .requestAttr("currentUser", currentUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Goal found successfully"))
                .andExpect(jsonPath("$.data.goalId").value(1));

        verify(validationUtils, times(1)).parseLong("1");
        verify(goalService, times(1))
                .getGoalByUserIdAndGoalId(1L, 1L);
        verify(goalMapper, times(1)).toDto(any(Goal.class));
    }

    @Test
    @DisplayName("Update goal - Success scenario")
    void testUpdateGoal_Success() throws Exception {
        when(validationUtils.parseLong("1")).thenReturn(1L);
        when(validationUtils.validateRequest(any(GoalDto.class), eq(Mode.GOAL_UPDATE)))
                .thenReturn(goalDto);
        when(goalService.updateGoal(any(GoalDto.class), eq(1L)))
                .thenReturn(true);
        when(goalService.getGoal(1L))
                .thenReturn(goal);
        when(goalMapper.toDto(any(Goal.class)))
                .thenReturn(goalDto);

        mockMvc.perform(put("/api/goals/1")
                        .requestAttr("currentUser", currentUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"goalName\":\"Updated Vacation\",\"targetAmount\":6000.0,\"duration\":12}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Goal updated successfully"))
                .andExpect(jsonPath("$.data.goalId").value(1));

        verify(validationUtils, times(1)).parseLong("1");
        verify(validationUtils, times(1))
                .validateRequest(any(GoalDto.class), eq(Mode.GOAL_UPDATE));
        verify(goalService, times(1))
                .updateGoal(any(GoalDto.class), eq(1L));
        verify(goalService, times(1)).getGoal(1L);
        verify(goalMapper, times(1)).toDto(any(Goal.class));
    }

    @Test
    @DisplayName("Delete goal - Success scenario")
    void testDeleteGoal_Success() throws Exception {
        when(validationUtils.parseLong("1")).thenReturn(1L);
        when(goalService.deleteGoal(1L, 1L))
                .thenReturn(true);

        mockMvc.perform(delete("/api/goals/1")
                        .requestAttr("currentUser", currentUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Goal deleted successfully"))
                .andExpect(jsonPath("$.data.goalId").value(1));

        verify(validationUtils, times(1)).parseLong("1");
        verify(goalService, times(1))
                .deleteGoal(1L, 1L);
    }

    @Test
    @DisplayName("Create goal - ValidationException")
    void testCreateGoal_ValidationException() throws Exception {
        when(validationUtils.validateRequest(any(GoalDto.class), eq(Mode.GOAL_CREATE)))
                .thenThrow(new ValidationException("Target amount must be positive"));

        mockMvc.perform(post("/api/goals")
                        .requestAttr("currentUser", currentUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"goalName\":\"Invalid Goal\",\"targetAmount\":-1000.0,\"duration\":6}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Target amount must be positive"));

        verify(validationUtils, times(1))
                .validateRequest(any(GoalDto.class), eq(Mode.GOAL_CREATE));
    }

    @Test
    @DisplayName("Get goal by ID - Not found")
    void testGetGoalById_NotFound() throws Exception {
        when(validationUtils.parseLong("1")).thenReturn(1L);
        when(goalService.getGoalByUserIdAndGoalId(1L, 1L))
                .thenReturn(null);

        mockMvc.perform(get("/api/goals/1")
                        .requestAttr("currentUser", currentUser))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Goal not found or you are not the owner of the goal."));

        verify(validationUtils, times(1)).parseLong("1");
        verify(goalService, times(1))
                .getGoalByUserIdAndGoalId(1L, 1L);
    }

    @Test
    @DisplayName("Invalid goal ID - ValidationException")
    void testGetGoalById_InvalidId() throws Exception {
        when(validationUtils.parseLong("invalid"))
                .thenThrow(new ValidationException("Invalid numeric format for id: invalid"));

        mockMvc.perform(get("/api/goals/invalid")
                        .requestAttr("currentUser", currentUser))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Invalid numeric format for id: invalid"));

        verify(validationUtils, times(1)).parseLong("invalid");
    }

    @Test
    @DisplayName("Negative goal ID - ValidationException")
    void testGetGoalById_NegativeId() throws Exception {
        when(validationUtils.parseLong("-1"))
                .thenThrow(new ValidationException("Id cannot be negative"));

        mockMvc.perform(get("/api/goals/-1")
                        .requestAttr("currentUser", currentUser))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Id cannot be negative"));

        verify(validationUtils, times(1)).parseLong("-1");
    }
}