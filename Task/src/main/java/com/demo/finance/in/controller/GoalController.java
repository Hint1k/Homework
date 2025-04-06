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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.demo.finance.domain.utils.SwaggerExamples.Admin.INVALID_PAGE_RESPONSE;
import static com.demo.finance.domain.utils.SwaggerExamples.Goal.CREATE_GOAL_REQUEST;
import static com.demo.finance.domain.utils.SwaggerExamples.Goal.CREATE_GOAL_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Goal.DELETE_GOAL_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Goal.GET_GOALS_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Goal.GET_GOAL_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Goal.GOAL_NOT_FOUND_RESPONSE;
import static com.demo.finance.domain.utils.SwaggerExamples.Goal.INVALID_GOAL_ID_RESPONSE;
import static com.demo.finance.domain.utils.SwaggerExamples.Goal.INVALID_JSON_RESPONSE;
import static com.demo.finance.domain.utils.SwaggerExamples.Goal.MISSING_GOAL_FIELD_RESPONSE;
import static com.demo.finance.domain.utils.SwaggerExamples.Goal.UPDATE_GOAL_REQUEST;
import static com.demo.finance.domain.utils.SwaggerExamples.Goal.UPDATE_GOAL_SUCCESS;

/**
 * REST controller for managing user financial goals.
 * <p>
 * Provides endpoints to create, retrieve, update, and delete goals. Supports pagination
 * for goal listings. Uses {@code GoalService} for business logic and {@code ValidationUtils}
 * for validating input requests. {@code GoalMapper} is used to map between domain models and DTOs.
 * </p>
 */
@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController extends BaseController {

    private final GoalService goalService;
    private final ValidationUtils validationUtils;
    private final GoalMapper goalMapper;

    /**
     * Creates a new financial goal for the current user.
     * <p>
     * Validates the request body, creates the goal, retrieves the created goal, maps it to a DTO,
     * and returns a success response with the goal data.
     * </p>
     *
     * @param goalDtoNew  the incoming goal data
     * @param currentUser the currently authenticated user
     * @return a {@code ResponseEntity} with the created goal or an error message
     */
    @PostMapping
    @Operation(summary = "Create goal", description = "Creates a new financial goal")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Goal data", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GoalDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = CREATE_GOAL_REQUEST)))
    @ApiResponse(responseCode = "201", description = "Goal created successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GoalDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = CREATE_GOAL_SUCCESS)))
    @ApiResponse(responseCode = "400", description = "Bad request - Invalid json format", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "InvalidJsonFormat",
            value = INVALID_JSON_RESPONSE)))
    public ResponseEntity<Map<String, Object>> createGoal(
            @RequestBody GoalDto goalDtoNew, @RequestAttribute("currentUser") UserDto currentUser) {
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
     * Retrieves a paginated list of goals for the current user.
     * <p>
     * Validates pagination parameters and returns the paginated goal list using {@code GoalService}.
     * </p>
     *
     * @param paramsNew   the pagination parameters (page and size)
     * @param currentUser the currently authenticated user
     * @return a {@code ResponseEntity} with paginated goal results or an error message
     */
    @GetMapping
    @Operation(summary = "Get goals", description = "Returns paginated list of goals")
    @ApiResponse(responseCode = "200", description = "Goals retrieved successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PaginatedResponse.class),
            examples = @ExampleObject(name = "SuccessResponse", value = GET_GOALS_SUCCESS)))
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid page parameter", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "InvalidPage",
            value = INVALID_PAGE_RESPONSE)))
    public ResponseEntity<Map<String, Object>> getPaginatedGoals(
            @ParameterObject @ModelAttribute PaginationParams paramsNew,
            @Parameter(hidden = true) @RequestAttribute("currentUser") UserDto currentUser) {
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
     * Retrieves details of a specific goal by ID for the current user.
     * <p>
     * Validates the goal ID and ensures the goal belongs to the user. If found, maps and returns the goal.
     * </p>
     *
     * @param goalId      the ID of the goal to retrieve
     * @param currentUser the currently authenticated user
     * @return a {@code ResponseEntity} with goal details or an error message
     */
    @GetMapping("/{goalId}")
    @Operation(summary = "Get goal", description = "Returns goal details by ID")
    @ApiResponse(responseCode = "200", description = "Goal retrieved successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GoalDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = GET_GOAL_SUCCESS)))
    @ApiResponse(responseCode = "404", description = "Not Found - Goal not found", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "GoalNotFound",
            value = GOAL_NOT_FOUND_RESPONSE)))
    public ResponseEntity<Map<String, Object>> getGoalById(
            @PathVariable("goalId") String goalId,
            @Parameter(hidden = true) @RequestAttribute("currentUser") UserDto currentUser) {
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
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Updates an existing goal for the current user.
     * <p>
     * Validates the goal ID and incoming goal data, updates the goal, retrieves updated details,
     * and returns a response with updated goal data.
     * </p>
     *
     * @param goalId      the ID of the goal to update
     * @param goalDtoNew  the updated goal data
     * @param currentUser the currently authenticated user
     * @return a {@code ResponseEntity} with the updated goal or an error message
     */
    @PutMapping("/{goalId}")
    @Operation(summary = "Update goal", description = "Updates existing goal")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated goal data", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GoalDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = UPDATE_GOAL_REQUEST)))
    @ApiResponse(responseCode = "200", description = "Goal updated successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GoalDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = UPDATE_GOAL_SUCCESS)))
    @ApiResponse(responseCode = "400", description = "Bad Request - Missing goal field ", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "ValidationError",
            value = MISSING_GOAL_FIELD_RESPONSE)))
    public ResponseEntity<Map<String, Object>> updateGoal(
            @PathVariable("goalId") String goalId, @RequestBody GoalDto goalDtoNew,
            @RequestAttribute("currentUser") UserDto currentUser) {
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
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Deletes a specific goal by ID for the current user.
     * <p>
     * Validates the goal ID and ensures the user owns the goal before deletion.
     * </p>
     *
     * @param goalId      the ID of the goal to delete
     * @param currentUser the currently authenticated user
     * @return a {@code ResponseEntity} confirming the deletion or an error message
     */
    @DeleteMapping("/{goalId}")
    @Operation(summary = "Delete goal", description = "Deletes goal by ID")
    @ApiResponse(responseCode = "200", description = "Goal deleted successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Long.class),
            examples = @ExampleObject(name = "SuccessResponse", value = DELETE_GOAL_SUCCESS)))
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid goal ID format", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "InvalidGoalId",
            value = INVALID_GOAL_ID_RESPONSE)))
    public ResponseEntity<Map<String, Object>> deleteGoal(
            @PathVariable("goalId") String goalId,
            @Parameter(hidden = true) @RequestAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            Long goalIdLong = validationUtils.parseLong(goalId);
            boolean success = goalService.deleteGoal(userId, goalIdLong);
            if (success) {
                return buildSuccessResponse(HttpStatus.OK, "Goal deleted successfully",
                        Map.of("goalId", goalIdLong));
            }
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST, "Failed to delete goal or you are not the owner of the goal.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}