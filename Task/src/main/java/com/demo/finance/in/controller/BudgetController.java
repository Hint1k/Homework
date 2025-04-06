package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.BudgetDto;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.Budget;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.ValidationException;
import com.demo.finance.out.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.demo.finance.domain.utils.SwaggerExamples.Budget.CREATE_BUDGET_REQUEST;
import static com.demo.finance.domain.utils.SwaggerExamples.Budget.CREATE_BUDGET_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Budget.GET_BUDGET_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Budget.MISSING_BUDGET_FIELD_RESPONSE;

/**
 * REST controller for managing budget operations.
 * <p>
 * This controller provides endpoints to set a user's monthly budget and retrieve budget data.
 * It utilizes the {@code BudgetService} for budget operations and {@code ValidationUtils} for request validation.
 * </p>
 */
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController extends BaseController {

    private final BudgetService budgetService;
    private final ValidationUtils validationUtils;

    /**
     * Sets the monthly budget for a user.
     * <p>
     * Validates the incoming {@code BudgetDto} request using {@code ValidationUtils}.
     * If the monthly limit is provided, it delegates to {@code BudgetService} to set the budget and returns
     * a successful response. Otherwise, it returns an error response indicating the missing monthly limit.
     * </p>
     *
     * @param budgetDtoNew the new budget data provided in the request body
     * @param currentUser  the current user's data injected via a request attribute
     * @return a {@code ResponseEntity} containing a map with the operation result and the budget details
     * or an error message
     * @throws ValidationException if the validation of the request fails
     */
    @PostMapping
    @Operation(summary = "Set monthly budget", description = "Sets user's monthly budget limit")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Budget data", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BudgetDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = CREATE_BUDGET_REQUEST)))
    @ApiResponse(responseCode = "200", description = "Budget set successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Budget.class),
            examples = @ExampleObject(name = "SuccessResponse", value = CREATE_BUDGET_SUCCESS)))
    @ApiResponse(responseCode = "400", description = "Bad Request - Missing budget field ", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "ValidationError",
            value = MISSING_BUDGET_FIELD_RESPONSE)))
    public ResponseEntity<Map<String, Object>> setMonthlyBudget(
            @RequestBody BudgetDto budgetDtoNew, @RequestAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            BudgetDto budgetDto = validationUtils.validateRequest(budgetDtoNew, Mode.BUDGET);
            if (budgetDto.getMonthlyLimit() != null) {
                Budget budget = budgetService.setMonthlyBudget(userId, budgetDto.getMonthlyLimit());
                if (budget != null) {
                    return buildSuccessResponse(HttpStatus.OK, "Budget generated successfully", budget);
                }
                return buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve budget details.");
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Monthly limit must be provided.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Retrieves the budget data for the current user.
     * <p>
     * Delegates to {@code BudgetService} to fetch the budget information for the user.
     * Returns a successful response with the budget data if found, otherwise an error response indicating
     * that the budget was not found.
     * </p>
     *
     * @param currentUser the current user's data injected via a request attribute
     * @return a {@code ResponseEntity} containing a map with the budget data or an error message
     */
    @GetMapping("/budget")
    @Operation(summary = "Get budget data", description = "Returns user's budget information")
    @ApiResponse(responseCode = "200", description = "Budget data retrieved", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Map.class),
            examples = @ExampleObject(name = "SuccessResponse", value = GET_BUDGET_SUCCESS)))
    public ResponseEntity<Map<String, Object>> getBudgetData(
            @Parameter(hidden = true) @RequestAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            Map<String, Object> budgetData = budgetService.getBudgetData(userId);
            if (budgetData != null) {
                return buildSuccessResponse(HttpStatus.OK, "Budget retrieved successfully", budgetData);
            }
            return buildErrorResponse(HttpStatus.NOT_FOUND, "Budget not found for the user.");
        } catch (NumberFormatException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid user ID.");
        }
    }
}