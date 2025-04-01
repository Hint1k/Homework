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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

/**
 * The {@code GoalController} class is a REST controller that provides endpoints for managing user goals.
 * It supports creating, retrieving, updating, and deleting goals for the currently logged-in user.
 * <p>
 * This controller leverages validation utilities to ensure that incoming requests meet the required constraints
 * and formats. It also uses a service layer to perform business logic related to goals and a mapper to convert
 * between entities and DTOs.
 */
@RestController
@RequestMapping("/api/goals")
public class GoalController extends BaseController {

    private final GoalService goalService;
    private final ValidationUtils validationUtils;
    private final GoalMapper goalMapper;

    /**
     * Constructs a new {@code GoalController} instance with the required dependencies.
     *
     * @param goalService     the service responsible for goal-related operations
     * @param validationUtils the utility for validating request parameters and DTOs
     * @param goalMapper      the mapper for converting between goal entities and DTOs
     */
    @Autowired
    public GoalController(GoalService goalService, ValidationUtils validationUtils, GoalMapper goalMapper) {
        this.goalService = goalService;
        this.validationUtils = validationUtils;
        this.goalMapper = goalMapper;
    }

    /**
     * Creates a new goal for the currently logged-in user.
     * <p>
     * This endpoint validates the provided goal data and delegates the request to the goal service
     * to create the goal. If the operation succeeds, a success response containing the created goal
     * is returned; otherwise, an error response is returned.
     *
     * @param goalDtoNew  the request body containing the new goal data
     * @param currentUser the currently logged-in user retrieved from the session
     * @return a success response if the operation succeeds or an error response if validation fails
     */
    @PostMapping
    @Operation(summary = "Create goal", description = "Creates a new financial goal")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Goal data", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GoalDto.class,
            requiredProperties = {"goalName", "targetAmount", "duration", "startTime"}, example = """
            {
              "goalName": "1",
              "targetAmount": "1500",
              "duration": "3",
              "startTime": "2025-01-01"
            }""")))
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
        }
    }

    /**
     * Retrieves a paginated list of goals for the currently logged-in user.
     * <p>
     * This endpoint validates the pagination parameters and delegates the request to the goal service
     * to fetch the paginated response. If the parameters are invalid, an error response is returned.
     *
     * @param paramsNew   the pagination parameters provided in the request
     * @param currentUser the currently logged-in user retrieved from the session
     * @return a paginated response containing goal data or an error response if validation fails
     */
    @GetMapping
    @Operation(summary = "Get goals", description = "Returns paginated list of goals")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = Object.class, example = "{}")))
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

    /**
     * Retrieves a specific goal by its ID for the currently logged-in user.
     * <p>
     * This endpoint validates the goal ID and ensures that the user owns the goal before retrieving it.
     * If the goal is found, a success response is returned; otherwise, an error response is returned.
     *
     * @param goalId      the ID of the goal to retrieve
     * @param currentUser the currently logged-in user retrieved from the session
     * @return a success response containing the goal data or an error response if validation fails
     */
    @GetMapping("/{goalId}")
    @Operation(summary = "Get goal", description = "Returns goal details by ID")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = Object.class, example = "{}")))
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

    /**
     * Updates a specific goal for the currently logged-in user.
     * <p>
     * This endpoint validates the goal ID and the updated goal data before delegating the request to the goal
     * service. If the operation succeeds, a success response containing the updated goal is returned; otherwise,
     * an error response is returned.
     *
     * @param goalId      the ID of the goal to update
     * @param goalDtoNew  the request body containing the updated goal data
     * @param currentUser the currently logged-in user retrieved from the session
     * @return a success response if the operation succeeds or an error response if validation fails
     */
    @PutMapping("/{goalId}")
    @Operation(summary = "Update goal", description = "Updates existing goal")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated goal data", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GoalDto.class,
            requiredProperties = {"goalName", "targetAmount", "duration"}, example = """
            {
              "goalName": "2",
              "targetAmount": "1500",
              "duration": "3"
            }""")))
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
        }
    }

    /**
     * Deletes a specific goal for the currently logged-in user.
     * <p>
     * This endpoint validates the goal ID and ensures that the user owns the goal before delegating the request
     * to the goal service. If the operation succeeds, a success response is returned; otherwise, an error
     * response is returned.
     *
     * @param goalId      the ID of the goal to delete
     * @param currentUser the currently logged-in user retrieved from the session
     * @return a success response if the operation succeeds or an error response if validation fails
     */
    @DeleteMapping("/{goalId}")
    @Operation(summary = "Delete goal", description = "Deletes goal by ID")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = Object.class, example = "{}")))
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