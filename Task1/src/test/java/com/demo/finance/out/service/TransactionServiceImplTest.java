package com.demo.finance.out.service;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock private TransactionRepository transactionRepository;
    @InjectMocks private TransactionServiceImpl transactionService;

    @Test
    void testCreateTransaction_validTransaction_savesSuccessfully() {
        Long userId = 1L;
        double amount = 100.0;
        String category = "Food";
        String date = "2025-03-05";
        String description = "Dinner";
        Type type = Type.EXPENSE;
        Long transactionId = 10L;

        when(transactionRepository.generateNextId()).thenReturn(transactionId);
        doNothing().when(transactionRepository).save(any());

        assertThatCode(() -> transactionService.createTransaction(userId, amount, category, date, description, type))
                .doesNotThrowAnyException();

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testGetTransaction_existingTransaction_returnsTransaction() {
        Long transactionId = 1L;
        Transaction transaction = new Transaction(transactionId, 2L, 100.0, "Groceries",
                LocalDate.now(), "Shopping", Type.EXPENSE);
        when(transactionRepository.findByTransactionId(transactionId)).thenReturn(transaction);

        Transaction result = transactionService.getTransaction(transactionId);

        assertThat(result).isEqualTo(transaction);
    }

    @Test
    void testDeleteTransaction_transactionExists_deletesSuccessfully() {
        Long transactionId = 1L;
        Long userId = 2L;
        when(transactionRepository.findByUserIdAndTransactionId(userId, transactionId))
                .thenReturn(Optional.of(new Transaction(transactionId, userId, 100.0, "Groceries",
                        LocalDate.now(), "Shopping", Type.EXPENSE)));
        when(transactionRepository.delete(transactionId)).thenReturn(true);

        boolean result = transactionService.deleteTransaction(userId, transactionId);

        assertThat(result).isTrue();
        verify(transactionRepository).delete(transactionId);
    }
}