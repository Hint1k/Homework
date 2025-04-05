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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Map;

import static com.demo.finance.domain.utils.SwaggerExamples.Budget.CREATE_BUDGET_REQUEST;
import static com.demo.finance.domain.utils.SwaggerExamples.Budget.CREATE_BUDGET_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Budget.GET_BUDGET_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Budget.MISSING_BUDGET_FIELD_RESPONSE;

/**
 * The {@code BudgetController} class is a REST controller that provides endpoints for managing user budgets.
 * It supports setting a monthly budget and retrieving budget-related data for the currently logged-in user.
 * <p>
 * This controller leverages validation utilities to ensure that incoming requests meet the required constraints
 * and formats. It also uses a service layer to perform business logic related to budgets.
 */
@RestController
@RequestMapping("/api/budgets")
public class BudgetController extends BaseController {

    private final BudgetService budgetService;
    private final ValidationUtils validationUtils;

    /**
     * Constructs a new {@code BudgetController} instance with the required dependencies.
     *
     * @param budgetService   the service responsible for budget-related operations
     * @param validationUtils the utility for validating request parameters and DTOs
     */
    @Autowired
    public BudgetController(BudgetService budgetService, ValidationUtils validationUtils) {
        this.budgetService = budgetService;
        this.validationUtils = validationUtils;
    }

    /**
     * Sets a monthly budget for the currently logged-in user.
     * <p>
     * This endpoint validates the provided budget data and delegates the request to the budget service
     * to set the monthly budget. If the operation succeeds, a success response is returned; otherwise,
     * an error response is returned.
     *
     * @param budgetDtoNew the request body containing the new budget data
     * @param currentUser  the currently logged-in user retrieved from the session
     * @return a success response if the operation succeeds or an error response if validation fails
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
            @RequestBody BudgetDto budgetDtoNew, @SessionAttribute("currentUser") UserDto currentUser) {
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
     * Retrieves budget-related data for the currently logged-in user.
     * <p>
     * This endpoint retrieves the user's budget data from the budget service. If the data is found,
     * a success response is returned; otherwise, an error response is returned.
     *
     * @param currentUser the currently logged-in user retrieved from the session
     * @return a success response containing the budget data or an error response if the data is not found
     */
    @GetMapping("/budget")
    @Operation(summary = "Get budget data", description = "Returns user's budget information")
    @ApiResponse(responseCode = "200", description = "Budget data retrieved", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Map.class),
            examples = @ExampleObject(name = "SuccessResponse", value = GET_BUDGET_SUCCESS)))
    public ResponseEntity<Map<String, Object>> getBudgetData(
            @Parameter(hidden = true) @SessionAttribute("currentUser") UserDto currentUser) {
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