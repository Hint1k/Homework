package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.TransactionMapper;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.domain.utils.PaginationParams;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.ValidationException;
import com.demo.finance.out.service.TransactionService;
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
@RequestMapping("/api/transactions")
public class TransactionController extends BaseController {

    private final TransactionService transactionService;
    private final ValidationUtils validationUtils;
    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionController(TransactionService transactionService, ValidationUtils validationUtils,
                                 TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.validationUtils = validationUtils;
        this.transactionMapper = transactionMapper;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTransaction(
            @RequestBody TransactionDto transactionDtoNew, @SessionAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            TransactionDto transactionDto =
                    validationUtils.validateRequest(transactionDtoNew, Mode.TRANSACTION_CREATE);
            Long transactionId = transactionService.createTransaction(transactionDto, userId);
            if (transactionId != null) {
                Transaction transaction = transactionService.getTransaction(transactionId);
                if (transaction != null) {
                    TransactionDto transactionDtoCreated = transactionMapper.toDto(transaction);
                    return buildSuccessResponse(
                            HttpStatus.CREATED, "Transaction created successfully", transactionDtoCreated);
                }
                return buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve transaction details.");
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to create transaction.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while creating the transaction.");
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPaginatedTransactions(
            @ModelAttribute PaginationParams paramsNew, @SessionAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            PaginationParams params = validationUtils.validateRequest(paramsNew, Mode.PAGE);
            PaginatedResponse<TransactionDto> paginatedResponse =
                    transactionService.getPaginatedTransactionsForUser(userId, params.page(), params.size());
            return buildPaginatedResponse(userId, paginatedResponse);
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request parameters",
                    Map.of("message", e.getMessage()));
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Map<String, Object>> getTransactionById(
            @PathVariable String transactionId, @SessionAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            Long transactionIdLong = validationUtils.parseLong(transactionId);
            Transaction transaction =
                    transactionService.getTransactionByUserIdAndTransactionId(userId, transactionIdLong);
            if (transaction != null) {
                TransactionDto transactionDto = transactionMapper.toDto(transaction);
                return buildSuccessResponse(HttpStatus.OK, "Transaction found successfully", transactionDto);
            }
            return buildErrorResponse(HttpStatus.NOT_FOUND,
                    "Transaction not found or you are not the owner of the transaction.");
        } catch (NumberFormatException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid transaction ID.");
        }
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<Map<String, Object>> updateTransaction(
            @PathVariable("transactionId") String transactionId, @RequestBody TransactionDto transactionDtoNew,
            @SessionAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            Long transactionIdLong = validationUtils.parseLong(transactionId);
            TransactionDto transactionDto =
                    validationUtils.validateRequest(transactionDtoNew, Mode.TRANSACTION_UPDATE);
            transactionDto.setTransactionId(transactionIdLong);
            boolean success = transactionService.updateTransaction(transactionDto, userId);
            if (success) {
                Transaction transaction = transactionService.getTransaction(transactionDto.getTransactionId());
                if (transaction != null) {
                    TransactionDto transactionDtoUpdated = transactionMapper.toDto(transaction);
                    return buildSuccessResponse(
                            HttpStatus.OK, "Transaction updated successfully", transactionDtoUpdated);
                }
                return buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve transaction details.");
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST,
                    "Failed to update transaction or you are not the owner of the transaction.");
        } catch (NumberFormatException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid transaction ID.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while updating the transaction.");
        }
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Map<String, Object>> deleteTransaction(
            @PathVariable("transactionId") String transactionId,
            @SessionAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            Long transactionIdLong = validationUtils.parseLong(transactionId);
            boolean success = transactionService.deleteTransaction(userId, transactionIdLong);
            if (success) {
                return buildSuccessResponse(
                        HttpStatus.OK, "Transaction deleted successfully", transactionIdLong);
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST,
                    "Failed to delete transaction or you are not the owner of the transaction.");
        } catch (NumberFormatException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid transaction ID");
        }
    }
}