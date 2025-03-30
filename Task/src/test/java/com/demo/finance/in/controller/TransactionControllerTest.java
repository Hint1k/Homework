package com.demo.finance.in.controller;

import com.demo.finance.domain.mapper.TransactionMapper;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.out.service.TransactionService;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.ValidationException;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.domain.utils.PaginationParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.fail;
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

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    private MockMvc mockMvc;
    @Mock
    private TransactionService transactionService;
    @Mock
    private ValidationUtils validationUtils;
    @Mock
    private TransactionMapper transactionMapper;
    @InjectMocks
    private TransactionController transactionController;
    private UserDto currentUser;
    private TransactionDto transactionDto;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
        currentUser = new UserDto();
        currentUser.setUserId(1L);
        transactionDto = new TransactionDto();
        transactionDto.setTransactionId(1L);
        transactionDto.setAmount(BigDecimal.valueOf(100.0));
        transactionDto.setCategory("Food");
        transactionDto.setDescription("Lunch");
        transactionDto.setType("EXPENSE");
        transaction = new Transaction();
        transaction.setTransactionId(1L);
    }

    private PaginationParams createPaginationParams() {
        return new PaginationParams(1, 10);
    }

    @Test
    @DisplayName("Create transaction - Success scenario")
    void testCreateTransaction_Success() {
        try {
            String content = "{\"amount\":100.0,\"category\":\"Food\",\"description\":\"Lunch\",\"type\":\"EXPENSE\"}";
            when(validationUtils.validateRequest(any(TransactionDto.class), eq(Mode.TRANSACTION_CREATE)))
                    .thenReturn(transactionDto);
            when(transactionService.createTransaction(any(TransactionDto.class), anyLong()))
                    .thenReturn(1L);
            when(transactionService.getTransaction(1L))
                    .thenReturn(transaction);
            when(transactionMapper.toDto(any(Transaction.class)))
                    .thenReturn(transactionDto);

            mockMvc.perform(post("/api/transactions")
                            .sessionAttr("currentUser", currentUser)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("Transaction created successfully"))
                    .andExpect(jsonPath("$.data.transactionId").value(1));

            verify(validationUtils, times(1))
                    .validateRequest(any(TransactionDto.class), eq(Mode.TRANSACTION_CREATE));
            verify(transactionService, times(1))
                    .createTransaction(any(TransactionDto.class), eq(1L));
        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Get paginated transactions - Success scenario")
    void testGetPaginatedTransactions_Success() {
        try {
            PaginationParams params = createPaginationParams();
            PaginatedResponse<TransactionDto> response = new PaginatedResponse<>(
                    List.of(transactionDto), 1, 1, 1, 10);

            when(validationUtils.validateRequest(any(PaginationParams.class), eq(Mode.PAGE)))
                    .thenReturn(params);
            when(transactionService.getPaginatedTransactionsForUser(1L, 1, 10))
                    .thenReturn(response);

            mockMvc.perform(get("/api/transactions")
                            .sessionAttr("currentUser", currentUser)
                            .param("page", "1")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.metadata.user_id").value(1));

            verify(validationUtils, times(1))
                    .validateRequest(any(PaginationParams.class), eq(Mode.PAGE));
            verify(transactionService, times(1))
                    .getPaginatedTransactionsForUser(1L, 1, 10);
        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Get transaction by ID - Success scenario")
    void testGetTransactionById_Success() {
        try {
            when(validationUtils.parseLong("1")).thenReturn(1L);
            when(transactionService.getTransactionByUserIdAndTransactionId(1L, 1L))
                    .thenReturn(transaction);
            when(transactionMapper.toDto(any(Transaction.class)))
                    .thenReturn(transactionDto);

            mockMvc.perform(get("/api/transactions/1")
                            .sessionAttr("currentUser", currentUser))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Transaction found successfully"))
                    .andExpect(jsonPath("$.data.transactionId").value(1));

            verify(validationUtils, times(1)).parseLong("1");
            verify(transactionService, times(1))
                    .getTransactionByUserIdAndTransactionId(1L, 1L);
        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Update transaction - Success scenario")
    void testUpdateTransaction_Success() {
        try {
            when(validationUtils.parseLong("1")).thenReturn(1L);
            when(validationUtils.validateRequest(any(TransactionDto.class), eq(Mode.TRANSACTION_UPDATE)))
                    .thenReturn(transactionDto);
            when(transactionService.updateTransaction(any(TransactionDto.class), eq(1L)))
                    .thenReturn(true);
            when(transactionService.getTransaction(1L))
                    .thenReturn(transaction);
            when(transactionMapper.toDto(any(Transaction.class)))
                    .thenReturn(transactionDto);

            mockMvc.perform(put("/api/transactions/1")
                            .sessionAttr("currentUser", currentUser)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"amount\":150.0,\"category\":\"Food\",\"description\":\"Dinner\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Transaction updated successfully"))
                    .andExpect(jsonPath("$.data.transactionId").value(1));

            verify(validationUtils, times(1)).parseLong("1");
            verify(validationUtils, times(1))
                    .validateRequest(any(TransactionDto.class), eq(Mode.TRANSACTION_UPDATE));
            verify(transactionService, times(1))
                    .updateTransaction(any(TransactionDto.class), eq(1L));
        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Delete transaction - Success scenario")
    void testDeleteTransaction_Success() {
        try {
            when(validationUtils.parseLong("1")).thenReturn(1L);
            when(transactionService.deleteTransaction(1L, 1L))
                    .thenReturn(true);

            mockMvc.perform(delete("/api/transactions/1")
                            .sessionAttr("currentUser", currentUser))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Transaction deleted successfully"))
                    .andExpect(jsonPath("$.data").value(1));

            verify(validationUtils, times(1)).parseLong("1");
            verify(transactionService, times(1))
                    .deleteTransaction(1L, 1L);
        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Create transaction - ValidationException")
    void testCreateTransaction_ValidationException() {
        try {
            String content = "{\"amount\":-100.0,\"category\":\"Foo\",\"description\":\"Bar\",\"type\":\"EXPENSE\"}";
            when(validationUtils.validateRequest(any(TransactionDto.class), eq(Mode.TRANSACTION_CREATE)))
                    .thenThrow(new ValidationException("Amount must be positive"));

            mockMvc.perform(post("/api/transactions")
                            .sessionAttr("currentUser", currentUser)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Amount must be positive"));

            verify(validationUtils, times(1))
                    .validateRequest(any(TransactionDto.class), eq(Mode.TRANSACTION_CREATE));
        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Get transaction by ID - Not found")
    void testGetTransactionById_NotFound() {
        try {
            when(validationUtils.parseLong("1")).thenReturn(1L);
            when(transactionService.getTransactionByUserIdAndTransactionId(1L, 1L))
                    .thenReturn(null);

            mockMvc.perform(get("/api/transactions/1")
                            .sessionAttr("currentUser", currentUser))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error")
                            .value("Transaction not found or you are not the owner of the transaction."));

            verify(validationUtils, times(1)).parseLong("1");
            verify(transactionService, times(1))
                    .getTransactionByUserIdAndTransactionId(1L, 1L);
        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Invalid transaction ID - NumberFormatException")
    void testGetTransactionById_InvalidId() {
        try {
            when(validationUtils.parseLong("invalid"))
                    .thenThrow(new NumberFormatException("Invalid transaction ID"));

            mockMvc.perform(get("/api/transactions/invalid")
                            .sessionAttr("currentUser", currentUser))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Invalid transaction ID."));

            verify(validationUtils, times(1)).parseLong("invalid");
        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }
}