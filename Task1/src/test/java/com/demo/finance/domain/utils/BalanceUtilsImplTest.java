package com.demo.finance.domain.utils;

import com.demo.finance.domain.model.Goal;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.out.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceUtilsImplTest {

    @Mock private TransactionRepository transactionRepository;
    @InjectMocks private BalanceUtilsImpl balanceUtils;

    private Goal mockGoal;

    @BeforeEach
    void setUp() {
        mockGoal = new Goal(1L, "Vacation", new BigDecimal(5000), 6);
        mockGoal.setStartTime(LocalDate.of(2023, 1, 1));
    }

    @Test
    void testCalculateBalance_Success() {
        List<Transaction> mockTransactions = Arrays.asList(
                new Transaction(1L, 1L, new BigDecimal(1000), "Salary",
                        LocalDate.of(2023, 2, 1), "Monthly salary", Type.INCOME),
                new Transaction(2L, 1L, new BigDecimal(200), "Groceries",
                        LocalDate.of(2023, 2, 5), "Weekly shopping", Type.EXPENSE),
                new Transaction(3L, 1L, new BigDecimal(500), "Bonus",
                        LocalDate.of(2023, 3, 1), "Year-end bonus", Type.INCOME)
        );

        when(transactionRepository.findByUserId(1L)).thenReturn(mockTransactions);

        BigDecimal balance = balanceUtils.calculateBalance(1L, mockGoal);

        assertThat(balance).isEqualTo(new BigDecimal(1300));
        verify(transactionRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testCalculateBalance_NoTransactions() {
        when(transactionRepository.findByUserId(1L)).thenReturn(List.of());

        BigDecimal balance = balanceUtils.calculateBalance(1L, mockGoal);

        assertThat(balance).isZero();
        verify(transactionRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testCalculateBalance_TransactionsOutsideGoalPeriod() {
        List<Transaction> mockTransactions = Arrays.asList(
                new Transaction(1L, 1L, new BigDecimal(1000), "Salary",
                        LocalDate.of(2022, 12, 1), "Monthly salary", Type.INCOME),
                new Transaction(2L, 1L, new BigDecimal(200), "Groceries",
                        LocalDate.of(2024, 1, 1), "Weekly shopping", Type.EXPENSE)
        );

        when(transactionRepository.findByUserId(1L)).thenReturn(mockTransactions);

        BigDecimal balance = balanceUtils.calculateBalance(1L, mockGoal);

        assertThat(balance).isZero();
        verify(transactionRepository, times(1)).findByUserId(1L);
    }
}