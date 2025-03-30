package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.BudgetDto;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.Budget;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.ValidationException;
import com.demo.finance.out.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Map;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController extends BaseController {

    private final BudgetService budgetService;
    private final ValidationUtils validationUtils;

    @Autowired
    public BudgetController(BudgetService budgetService, ValidationUtils validationUtils) {
        this.budgetService = budgetService;
        this.validationUtils = validationUtils;
    }

    @PostMapping
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

    @GetMapping("/budget")
    public ResponseEntity<Map<String, Object>> getBudgetData(@SessionAttribute("currentUser") UserDto currentUser) {
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