package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.domain.utils.PaginationParams;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.ValidationException;
import com.demo.finance.out.service.AdminService;
import com.demo.finance.out.service.TransactionService;
import com.demo.finance.out.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class AdminController extends BaseController {

    private final AdminService adminService;
    private final UserService userService;
    private final TransactionService transactionService;
    private final ValidationUtils validationUtils;

    @Autowired
    public AdminController(AdminService adminService, UserService userService, TransactionService transactionService,
                           ValidationUtils validationUtils) {
        this.adminService = adminService;
        this.userService = userService;
        this.transactionService = transactionService;
        this.validationUtils = validationUtils;
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getPaginatedUsers(@RequestBody String json) {
        try {
            PaginationParams params = validationUtils.validatePaginationParams(json, Mode.PAGE);
            PaginatedResponse<UserDto> paginatedResponse = userService.getPaginatedUsers(params.page(), params.size());
            return buildPaginatedResponse(null, paginatedResponse);
        } catch (ValidationException e) {
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST, "Invalid pagination parameters: " + e.getMessage());
        }
    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<Map<String, Object>> getPaginatedTransactionsForUser(
            @PathVariable String userId, @RequestBody String json) {
        try {
            Long userIdLong = validationUtils.parseUserId(userId, Mode.GET);
            PaginationParams params = validationUtils.validatePaginationParams(json, Mode.PAGE);
            PaginatedResponse<TransactionDto> paginatedResponse =
                    transactionService.getPaginatedTransactionsForUser(userIdLong, params.page(), params.size());
            return buildPaginatedResponse(userIdLong, paginatedResponse);
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable String userId) {
        try {
            Long userIdLong = validationUtils.parseUserId(userId, Mode.GET);
            User user = adminService.getUser(userIdLong);
            if (user != null) {
                UserDto userDto = UserDto.removePassword(UserMapper.INSTANCE.toDto(user));
                return buildSuccessResponse(HttpStatus.OK, "User details", userDto);
            }
            return buildErrorResponse(HttpStatus.NOT_FOUND, "User not found.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PatchMapping("/block/{userId}")
    public ResponseEntity<Map<String, Object>> blockUnblockUser(
            @PathVariable String userId, @RequestBody String json) {
        try {
            Long userIdLong = validationUtils.parseUserId(userId, Mode.BLOCK_UNBLOCK);
            UserDto userDto = validationUtils.validateUserJson(json, Mode.BLOCK_UNBLOCK);
            boolean success = adminService.blockOrUnblockUser(userIdLong, userDto);
            if (success) {
                return buildSuccessResponse(HttpStatus.OK,
                        "User blocked/unblocked status changed successfully", UserDto.removePassword(userDto));
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to block/unblock user.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PatchMapping("/role/{userId}")
    public ResponseEntity<Map<String, Object>> updateUserRole(@PathVariable String userId, @RequestBody String json) {
        try {
            Long userIdLong = validationUtils.parseUserId(userId, Mode.UPDATE_ROLE);
            UserDto userDto = validationUtils.validateUserJson(json, Mode.UPDATE_ROLE);
            boolean success = adminService.updateUserRole(userIdLong, userDto);
            if (success) {
                return buildSuccessResponse(
                        HttpStatus.OK, "User role updated successfully", UserDto.removePassword(userDto));
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to update role.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String userId) {
        try {
            Long userIdLong = validationUtils.parseUserId(userId, Mode.DELETE);
            boolean success = adminService.deleteUser(userIdLong);
            if (success) {
                return buildSuccessResponse(HttpStatus.OK, "Account deleted successfully", userIdLong);
            }
            return buildErrorResponse(HttpStatus.NOT_FOUND, "User not found.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}