package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.mapper.TransactionMapper;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.out.repository.TransactionRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private TransactionDto transactionDto;
    private Transaction transaction;
    private Long transactionId;

    @BeforeEach
    void setUp() {
        transactionId = 2L;
        transactionDto = Instancio.create(TransactionDto.class);
        transactionDto.setTransactionId(transactionId);
        transactionDto.setUserId(1L);
        transaction = Instancio.create(Transaction.class);
        transaction.setTransactionId(transactionId);
    }

    @Test
    @DisplayName("Create transaction - valid transaction - returns transaction ID")
    void testCreateTransaction_validTransaction_returnsTransactionId() {
        when(transactionMapper.toEntity(transactionDto)).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transactionId);

        Long result = transactionService.createTransaction(transactionDto, 1L);

        assertThat(result).isEqualTo(transactionId);
        verify(transactionMapper).toEntity(transactionDto);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Get transaction - existing transaction - returns transaction")
    void testGetTransaction_existingTransaction_returnsTransaction() {
        when(transactionRepository.findById(transactionId)).thenReturn(transaction);

        Transaction result = transactionService.getTransaction(transactionId);

        assertThat(result).isEqualTo(transaction);
    }

    @Test
    @DisplayName("Delete transaction - transaction exists - deletes successfully")
    void testDeleteTransaction_transactionExists_deletesSuccessfully() {
        when(transactionRepository.findByUserIdAndTransactionId(1L, transactionId)).thenReturn(transaction);
        when(transactionRepository.delete(transactionId)).thenReturn(true);

        boolean result = transactionService.deleteTransaction(1L, transactionId);

        assertThat(result).isTrue();
        verify(transactionRepository).delete(transactionId);
    }

    @Test
    @DisplayName("Get transaction by user and ID - transaction exists - returns transaction")
    void testGetTransactionByUserIdAndTransactionId_exists_returnsTransaction() {
        when(transactionRepository.findByUserIdAndTransactionId(1L, transactionId)).thenReturn(transaction);

        Transaction result = transactionService.getTransactionByUserIdAndTransactionId(1L, transactionId);

        assertThat(result).isEqualTo(transaction);
    }

    @Test
    @DisplayName("Update transaction - transaction exists - updates successfully")
    void testUpdateTransaction_transactionExists_updatesSuccessfully() {
        when(transactionRepository.findByUserIdAndTransactionId(1L, transactionId)).thenReturn(transaction);
        when(transactionRepository.update(any(Transaction.class))).thenReturn(true);

        boolean result = transactionService.updateTransaction(transactionDto, 1L);

        assertThat(result).isTrue();
        assertThat(transaction.getAmount()).isEqualTo(transactionDto.getAmount());
        assertThat(transaction.getCategory()).isEqualTo(transactionDto.getCategory());
        assertThat(transaction.getDescription()).isEqualTo(transactionDto.getDescription());
        verify(transactionRepository).update(transaction);
    }

    @Test
    @DisplayName("Update transaction - transaction doesn't exist - returns false")
    void testUpdateTransaction_transactionDoesNotExist_returnsFalse() {
        when(transactionRepository.findByUserIdAndTransactionId(1L, transactionId)).thenReturn(null);

        boolean result = transactionService.updateTransaction(transactionDto, 1L);

        assertThat(result).isFalse();
        verify(transactionRepository, never()).update(any());
    }

    @Test
    @DisplayName("Delete transaction - transaction doesn't exist - returns false")
    void testDeleteTransaction_transactionDoesNotExist_returnsFalse() {
        Long transactionId = 99L;
        Long userId = 2L;

        when(transactionRepository.findByUserIdAndTransactionId(userId, transactionId)).thenReturn(null);

        boolean result = transactionService.deleteTransaction(userId, transactionId);

        assertThat(result).isFalse();
        verify(transactionRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Get paginated transactions - valid request - returns paginated response")
    void testGetPaginatedTransactionsForUser_validRequest_returnsPaginatedResponse() {
        List<Transaction> transactions = List.of(transaction, transaction);

        when(transactionRepository.findByUserId(1L, 0, 10)).thenReturn(transactions);
        when(transactionRepository.getTotalTransactionCountForUser(1L)).thenReturn(2);

        var result = transactionService.getPaginatedTransactionsForUser(1L, 1, 10);

        assertThat(result.data()).hasSize(2);
        assertThat(result.totalItems()).isEqualTo(2);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.currentPage()).isEqualTo(1);
        assertThat(result.pageSize()).isEqualTo(10);
    }
}