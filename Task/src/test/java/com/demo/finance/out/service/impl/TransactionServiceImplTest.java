package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.mapper.TransactionMapper;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionMapper transactionMapper;
    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    @DisplayName("Create transaction - valid transaction - returns transaction ID")
    void testCreateTransaction_validTransaction_returnsTransactionId() {
        TransactionDto dto = new TransactionDto();
        dto.setUserId(1L);
        dto.setAmount(new BigDecimal(100));
        dto.setCategory("Food");
        dto.setDate(LocalDate.of(2025, 3, 5));
        dto.setDescription("Dinner");
        dto.setType(Type.EXPENSE.name());

        Transaction transaction = new Transaction();
        when(transactionMapper.toEntity(dto)).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(123L);

        Long result = transactionService.createTransaction(dto, 1L);

        assertThat(result).isEqualTo(123L);
        verify(transactionMapper).toEntity(dto);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Get transaction - existing transaction - returns transaction")
    void testGetTransaction_existingTransaction_returnsTransaction() {
        Long transactionId = 1L;
        Transaction transaction = new Transaction(transactionId, 2L, new BigDecimal(100),
                "Groceries", LocalDate.now(), "Shopping", Type.EXPENSE);
        when(transactionRepository.findById(transactionId)).thenReturn(transaction);

        Transaction result = transactionService.getTransaction(transactionId);

        assertThat(result).isEqualTo(transaction);
    }

    @Test
    @DisplayName("Delete transaction - transaction exists - deletes successfully")
    void testDeleteTransaction_transactionExists_deletesSuccessfully() {
        Long transactionId = 1L;
        Long userId = 2L;
        Transaction transaction = new Transaction(transactionId, userId, new BigDecimal(100),
                "Groceries", LocalDate.now(), "Shopping", Type.EXPENSE);

        when(transactionRepository.findByUserIdAndTransactionId(userId, transactionId))
                .thenReturn(transaction);
        when(transactionRepository.delete(transactionId)).thenReturn(true);

        boolean result = transactionService.deleteTransaction(userId, transactionId);

        assertThat(result).isTrue();
        verify(transactionRepository).delete(transactionId);
    }

    @Test
    @DisplayName("Get transaction by user and ID - transaction exists - returns transaction")
    void testGetTransactionByUserIdAndTransactionId_exists_returnsTransaction() {
        Long userId = 1L;
        Long transactionId = 2L;
        Transaction expected = new Transaction(transactionId, userId, new BigDecimal(50),
                "Food", LocalDate.now(), "Lunch", Type.EXPENSE);

        when(transactionRepository.findByUserIdAndTransactionId(userId, transactionId))
                .thenReturn(expected);

        Transaction result = transactionService.getTransactionByUserIdAndTransactionId(userId, transactionId);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Update transaction - transaction exists - updates successfully")
    void testUpdateTransaction_transactionExists_updatesSuccessfully() {
        Long transactionId = 1L;
        Long userId = 2L;

        TransactionDto dto = new TransactionDto();
        dto.setTransactionId(transactionId);
        dto.setUserId(userId);
        dto.setAmount(new BigDecimal(200));
        dto.setCategory("Bills");
        dto.setDescription("Electricity");

        Transaction existing = new Transaction(transactionId, userId, new BigDecimal(100),
                "Groceries", LocalDate.now(), "Shopping", Type.EXPENSE);

        when(transactionRepository.findByUserIdAndTransactionId(userId, transactionId))
                .thenReturn(existing);
        when(transactionRepository.update(any(Transaction.class))).thenReturn(true);

        boolean result = transactionService.updateTransaction(dto, userId);

        assertThat(result).isTrue();
        assertThat(existing.getAmount()).isEqualTo(dto.getAmount());
        assertThat(existing.getCategory()).isEqualTo(dto.getCategory());
        assertThat(existing.getDescription()).isEqualTo(dto.getDescription());
        verify(transactionRepository).update(existing);
    }

    @Test
    @DisplayName("Update transaction - transaction doesn't exist - returns false")
    void testUpdateTransaction_transactionDoesNotExist_returnsFalse() {
        Long transactionId = 1L;
        Long userId = 2L;

        TransactionDto dto = new TransactionDto();
        dto.setTransactionId(transactionId);
        dto.setUserId(userId);

        when(transactionRepository.findByUserIdAndTransactionId(userId, transactionId))
                .thenReturn(null);

        boolean result = transactionService.updateTransaction(dto, userId);

        assertThat(result).isFalse();
        verify(transactionRepository, never()).update(any());
    }

    @Test
    @DisplayName("Delete transaction - transaction doesn't exist - returns false")
    void testDeleteTransaction_transactionDoesNotExist_returnsFalse() {
        Long transactionId = 99L;
        Long userId = 2L;

        when(transactionRepository.findByUserIdAndTransactionId(userId, transactionId))
                .thenReturn(null);

        boolean result = transactionService.deleteTransaction(userId, transactionId);

        assertThat(result).isFalse();
        verify(transactionRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Get paginated transactions - valid request - returns paginated response")
    void testGetPaginatedTransactionsForUser_validRequest_returnsPaginatedResponse() {
        Long userId = 1L;
        int page = 1;
        int size = 10;
        int offset = 0;

        List<Transaction> transactions = List.of(
                new Transaction(1L, userId, new BigDecimal(50), "Food",
                        LocalDate.now(), "Lunch", Type.EXPENSE),
                new Transaction(2L, userId, new BigDecimal(80), "Food",
                        LocalDate.now(), "Dinner", Type.EXPENSE)
        );

        when(transactionRepository.findByUserId(userId, offset, size))
                .thenReturn(transactions);
        when(transactionRepository.getTotalTransactionCountForUser(userId))
                .thenReturn(2);

        var result = transactionService.getPaginatedTransactionsForUser(userId, page, size);

        assertThat(result.data()).hasSize(2);
        assertThat(result.totalItems()).isEqualTo(2);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.currentPage()).isEqualTo(page);
        assertThat(result.pageSize()).isEqualTo(size);
    }
}