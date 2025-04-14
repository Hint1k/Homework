package com.demo.finance.domain.utils.impl;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.TransactionRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BalanceUtilsImplTest {

    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private BalanceUtilsImpl balanceUtils;

    private Goal goal;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = Instancio.create(Transaction.class);
        transaction.setUserId(1L);
        transaction.setAmount(BigDecimal.ZERO);
        goal = Instancio.create(Goal.class);
        goal.setUserId(1L);
    }

    @Test
    @DisplayName("Calculate balance - successful calculation with valid transactions")
    void testCalculateBalance_Success() {
        transaction.setAmount(new BigDecimal("500"));
        transaction.setType(Type.INCOME);
        transaction.setDate(LocalDate.of(2025, 5, 1));
        goal.setStartTime(LocalDate.of(2025, 4, 1));
        goal.setDuration(3);

        List<Transaction> mockTransactions = Arrays.asList(transaction, transaction, transaction);

        when(transactionRepository.findByUserId(1L)).thenReturn(mockTransactions);

        BigDecimal balance = balanceUtils.calculateBalance(1L, goal);

        assertThat(balance).isEqualTo(new BigDecimal("1500"));
        verify(transactionRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Calculate balance - returns zero when no transactions are found")
    void testCalculateBalance_NoTransactions() {
        when(transactionRepository.findByUserId(1L)).thenReturn(List.of());

        BigDecimal balance = balanceUtils.calculateBalance(1L, goal);

        assertThat(balance).isZero();
        verify(transactionRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Calculate balance - returns zero when transactions are outside the goal period")
    void testCalculateBalance_TransactionsOutsideGoalPeriod() {
        List<Transaction> mockTransactions = Arrays.asList(transaction, transaction);

        when(transactionRepository.findByUserId(1L)).thenReturn(mockTransactions);

        BigDecimal balance = balanceUtils.calculateBalance(1L, goal);

        assertThat(balance).isZero();
        verify(transactionRepository, times(1)).findByUserId(1L);
    }
}