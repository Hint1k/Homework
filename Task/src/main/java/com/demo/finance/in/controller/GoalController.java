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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Map;

@RestController
@RequestMapping("/api/goals")
public class GoalController extends BaseController {

    private final GoalService goalService;
    private final ValidationUtils validationUtils;
    private final GoalMapper goalMapper;

    @Autowired
    public GoalController(GoalService goalService, ValidationUtils validationUtils, GoalMapper goalMapper) {
        this.goalService = goalService;
        this.validationUtils = validationUtils;
        this.goalMapper = goalMapper;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createGoal(
            @RequestBody GoalDto goalDtoNew, @SessionAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            GoalDto goalDto = validationUtils.validateRequest(goalDtoNew, Mode.GOAL_CREATE);
            Long goalId = goalService.createGoal(goalDto, userId);
            if (goalId != null) {
                Goal goal = goalService.getGoal(goalId);
                if (goal != null) {
                    GoalDto goalDtoCreated = goalMapper.toDto(goal);
                    return buildSuccessResponse(
                            HttpStatus.CREATED, "Goal created successfully", goalDtoCreated);
                }
                return buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve goal details.");
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to create goal.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while creating the goal.");
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPaginatedGoals(
            @ModelAttribute PaginationParams paramsNew, @SessionAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            PaginationParams params = validationUtils.validateRequest(paramsNew, Mode.PAGE);
            PaginatedResponse<GoalDto> paginatedResponse =
                    goalService.getPaginatedGoalsForUser(userId, params.page(), params.size());
            return buildPaginatedResponse(userId, paginatedResponse);
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request parameters",
                    Map.of("message", e.getMessage()));
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<Map<String, Object>> getGoalById(
            @PathVariable("goalId") String goalId, @SessionAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            Long goalIdLong = validationUtils.parseLong(goalId);
            Goal goal = goalService.getGoalByUserIdAndGoalId(userId, goalIdLong);
            if (goal != null) {
                GoalDto goalDto = goalMapper.toDto(goal);
                return buildSuccessResponse(HttpStatus.OK, "Goal found successfully", goalDto);
            }
            return buildErrorResponse(
                    HttpStatus.NOT_FOUND, "Goal not found or you are not the owner of the goal.");
        } catch (NumberFormatException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid goal ID.");
        }
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<Map<String, Object>> updateGoal(
            @PathVariable("goalId") String goalId, @RequestBody GoalDto goalDtoNew,
            @SessionAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            Long goalIdLong = validationUtils.parseLong(goalId);
            GoalDto goalDto = validationUtils.validateRequest(goalDtoNew, Mode.GOAL_UPDATE);
            goalDto.setGoalId(goalIdLong);
            boolean success = goalService.updateGoal(goalDto, userId);
            if (success) {
                Goal goal = goalService.getGoal(goalDto.getGoalId());
                if (goal != null) {
                    GoalDto goalDtoUpdated = goalMapper.toDto(goal);
                    return buildSuccessResponse(HttpStatus.OK, "Goal updated successfully", goalDtoUpdated);
                }
                return buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve goal details.");
            }
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST, "Failed to update goal or you are not the owner of the goal.");
        } catch (NumberFormatException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid goal ID.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while updating the goal.");
        }
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Map<String, Object>> deleteGoal(
            @PathVariable("goalId") String goalId, @SessionAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            Long goalIdLong = validationUtils.parseLong(goalId);
            boolean success = goalService.deleteGoal(userId, goalIdLong);
            if (success) {
                return buildSuccessResponse(HttpStatus.OK, "Goal deleted successfully", goalIdLong);
            }
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST, "Failed to delete goal or you are not the owner of the goal.");
        } catch (NumberFormatException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid goal ID");
        }
    }
}