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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Map;

import static com.demo.finance.domain.utils.SwaggerExamples.Transaction.CREATE_TRANSACTION_REQUEST;
import static com.demo.finance.domain.utils.SwaggerExamples.Transaction.CREATE_TRANSACTION_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Transaction.DELETE_TRANSACTION_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Transaction.GET_TRANSACTIONS_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Transaction.GET_TRANSACTION_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Transaction.UPDATE_TRANSACTION_REQUEST;
import static com.demo.finance.domain.utils.SwaggerExamples.Transaction.UPDATE_TRANSACTION_SUCCESS;

/**
 * The {@code TransactionController} class is a REST controller that provides endpoints for managing user transactions.
 * It supports creating, retrieving, updating, and deleting transactions for the currently logged-in user.
 * <p>
 * This controller leverages validation utilities to ensure that incoming requests meet the required constraints
 * and formats. It also uses a service layer to perform business logic related to transactions and a mapper to convert
 * between entities and DTOs.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController extends BaseController {

    private final TransactionService transactionService;
    private final ValidationUtils validationUtils;
    private final TransactionMapper transactionMapper;

    /**
     * Constructs a new {@code TransactionController} instance with the required dependencies.
     *
     * @param transactionService the service responsible for transaction-related operations
     * @param validationUtils    the utility for validating request parameters and DTOs
     * @param transactionMapper  the mapper for converting between transaction entities and DTOs
     */
    @Autowired
    public TransactionController(TransactionService transactionService, ValidationUtils validationUtils,
                                 TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.validationUtils = validationUtils;
        this.transactionMapper = transactionMapper;
    }

    /**
     * Creates a new transaction for the currently logged-in user.
     * <p>
     * This endpoint validates the provided transaction data and delegates the request to the transaction service
     * to create the transaction. If the operation succeeds, a success response containing the created transaction
     * is returned; otherwise, an error response is returned.
     *
     * @param transactionDtoNew the request body containing the new transaction data
     * @param currentUser       the currently logged-in user retrieved from the session
     * @return a success response if the operation succeeds or an error response if validation fails
     */
    @PostMapping
    @Operation(summary = "Create transaction", description = "Creates a new transaction")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Transaction data", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TransactionDto.class,
            requiredProperties = {"amount", "category", "date", "description", "type"},
            example = CREATE_TRANSACTION_REQUEST)))
    @ApiResponse(responseCode = "201", description = "Transaction created successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TransactionDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = CREATE_TRANSACTION_SUCCESS)))
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
        }
    }

    /**
     * Retrieves a paginated list of transactions for the currently logged-in user.
     * <p>
     * This endpoint validates the pagination parameters and delegates the request to the transaction service
     * to fetch the paginated response. If the parameters are invalid, an error response is returned.
     *
     * @param paramsNew   the pagination parameters provided in the request
     * @param currentUser the currently logged-in user retrieved from the session
     * @return a paginated response containing transaction data or an error response if validation fails
     */
    @GetMapping
    @Operation(summary = "Get transactions", description = "Returns paginated transactions")
    @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PaginatedResponse.class),
            examples = @ExampleObject(name = "SuccessResponse", value = GET_TRANSACTIONS_SUCCESS)))
    public ResponseEntity<Map<String, Object>> getPaginatedTransactions(
            @ParameterObject @ModelAttribute PaginationParams paramsNew,
            @SessionAttribute("currentUser") UserDto currentUser) {
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

    /**
     * Retrieves a specific transaction by its ID for the currently logged-in user.
     * <p>
     * This endpoint validates the transaction ID and ensures that the user owns the transaction before retrieving it.
     * If the transaction is found, a success response is returned; otherwise, an error response is returned.
     *
     * @param transactionId the ID of the transaction to retrieve
     * @param currentUser   the currently logged-in user retrieved from the session
     * @return a success response containing the transaction data or an error response if validation fails
     */
    @GetMapping("/{transactionId}")
    @Operation(summary = "Get transaction", description = "Returns transaction by ID")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE))
    @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TransactionDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = GET_TRANSACTION_SUCCESS)))
    public ResponseEntity<Map<String, Object>> getTransactionById(
            @PathVariable("transactionId") String transactionId, @SessionAttribute("currentUser") UserDto currentUser) {
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

    /**
     * Updates a specific transaction for the currently logged-in user.
     * <p>
     * This endpoint validates the transaction ID and the updated transaction data before delegating the request to the
     * transaction service. If the operation succeeds, a success response containing the updated transaction is
     * returned; otherwise, an error response is returned.
     *
     * @param transactionId     the ID of the transaction to update
     * @param transactionDtoNew the request body containing the updated transaction data
     * @param currentUser       the currently logged-in user retrieved from the session
     * @return a success response if the operation succeeds or an error response if validation fails
     */
    @PutMapping("/{transactionId}")
    @Operation(summary = "Update transaction", description = "Updates existing transaction")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated transaction data", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TransactionDto.class,
            requiredProperties = {"amount", "category", "description"}, example = UPDATE_TRANSACTION_REQUEST)))
    @ApiResponse(responseCode = "200", description = "Transaction updated successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TransactionDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = UPDATE_TRANSACTION_SUCCESS)))
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
        }
    }

    /**
     * Deletes a specific transaction for the currently logged-in user.
     * <p>
     * This endpoint validates the transaction ID and ensures that the user owns the transaction before delegating the
     * request to the transaction service. If the operation succeeds, a success response is returned; otherwise, an
     * error response is returned.
     *
     * @param transactionId the ID of the transaction to delete
     * @param currentUser   the currently logged-in user retrieved from the session
     * @return a success response if the operation succeeds or an error response if validation fails
     */
    @DeleteMapping("/{transactionId}")
    @Operation(summary = "Delete transaction", description = "Deletes transaction by ID")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE))
    @ApiResponse(responseCode = "200", description = "Transaction deleted successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Long.class),
            examples = @ExampleObject(name = "SuccessResponse", value = DELETE_TRANSACTION_SUCCESS)))
    public ResponseEntity<Map<String, Object>> deleteTransaction(
            @PathVariable("transactionId") String transactionId,
            @SessionAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            Long transactionIdLong = validationUtils.parseLong(transactionId);
            boolean success = transactionService.deleteTransaction(userId, transactionIdLong);
            if (success) {
                return buildSuccessResponse(HttpStatus.OK, "Transaction deleted successfully",
                        Map.of("transactionId", transactionIdLong));
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST,
                    "Failed to delete transaction or you are not the owner of the transaction.");
        } catch (NumberFormatException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid transaction ID");
        }
    }
}