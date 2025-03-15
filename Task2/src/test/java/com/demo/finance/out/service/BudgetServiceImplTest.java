package com.demo.finance.out.service;

import com.demo.finance.domain.model.Budget;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.BudgetRepository;
import com.demo.finance.out.repository.TransactionRepository;
import com.demo.finance.out.service.impl.BudgetServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceImplTest {

    @Mock private BudgetRepository budgetRepository;
    @Mock private TransactionRepository transactionRepository;
    @InjectMocks private BudgetServiceImpl budgetService;

    @Test
    void setMonthlyBudget_savesSuccessfully() {
        Long userId = 1L;
        BigDecimal limit = new BigDecimal(1000);
        Budget budget = new Budget(userId, limit);

        when(budgetRepository.save(budget)).thenReturn(true);

        boolean result = budgetService.setMonthlyBudget(userId, limit);

        assertThat(result).isTrue();
        verify(budgetRepository).save(budget);
    }

    @Test
    void getBudget_existingBudget_returnsBudget() {
        Long userId = 1L;
        Budget budget = new Budget(userId, new BigDecimal(1000));
        when(budgetRepository.findByUserId(userId)).thenReturn(Optional.of(budget));

        Optional<Budget> result = budgetService.getBudget(userId);

        assertThat(result).contains(budget);
    }

    @Test
    void calculateExpensesForMonth_sumsTransactionsCorrectly() {
        Long userId = 1L;
        YearMonth currentMonth = YearMonth.of(2025, 3);
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();
        List<Transaction> transactions = List.of(
                new Transaction(1L, userId, new BigDecimal(50), "Groceries",
                        LocalDate.of(2025, 3, 5), "Food shopping", Type.EXPENSE),
                new Transaction(2L, userId, new BigDecimal(100), "Transport",
                        LocalDate.of(2025, 3, 10), "Taxi fare", Type.EXPENSE)
        );

        when(transactionRepository.findFiltered(userId, startOfMonth, endOfMonth, null, Type.EXPENSE))
                .thenReturn(transactions);

        BigDecimal result = budgetService.calculateExpensesForMonth(userId, currentMonth);

        assertThat(result).isEqualTo(new BigDecimal(150));
    }

    @Test
    void setMonthlyBudget_savesUnsuccessfully() {
        Long userId = 1L;
        BigDecimal limit = new BigDecimal(1000);
        Budget budget = new Budget(userId, limit);

        when(budgetRepository.save(budget)).thenReturn(false);

        boolean result = budgetService.setMonthlyBudget(userId, limit);

        assertThat(result).isFalse();
        verify(budgetRepository).save(budget);
    }

    @Test
    void getBudget_whenNoBudgetExists_returnsEmpty() {
        Long userId = 1L;

        when(budgetRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Optional<Budget> result = budgetService.getBudget(userId);

        assertThat(result).isEmpty();
    }

    @Test
    void calculateExpensesForMonth_whenNoTransactions_returnsZero() {
        Long userId = 1L;
        YearMonth currentMonth = YearMonth.of(2025, 3);
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        when(transactionRepository.findFiltered(userId, startOfMonth, endOfMonth, null, Type.EXPENSE))
                .thenReturn(List.of()); // No transactions

        BigDecimal result = budgetService.calculateExpensesForMonth(userId, currentMonth);

        assertThat(result).isEqualTo(new BigDecimal(0));
    }

    @Test
    void getFormattedBudget_whenNoBudgetSet_returnsNoBudgetMessage() {
        Long userId = 1L;

        when(budgetRepository.findByUserId(userId)).thenReturn(Optional.empty());

        String result = budgetService.getFormattedBudget(userId);

        assertThat(result).isEqualTo("No budget set.");
    }

    @Test
    void getFormattedBudget_whenBudgetSet_returnsFormattedMessage() {
        Long userId = 1L;
        Budget budget = new Budget(userId, new BigDecimal(1000));
        YearMonth currentMonth = YearMonth.now();
        BigDecimal totalExpenses = new BigDecimal(200);

        when(budgetRepository.findByUserId(userId)).thenReturn(Optional.of(budget));
        when(transactionRepository.findFiltered(userId, currentMonth.atDay(1), currentMonth.atEndOfMonth(),
                null, Type.EXPENSE))
                .thenReturn(List.of(new Transaction(1L, userId, new BigDecimal(100), "Food",
                                LocalDate.of(2025, 3, 5), "Groceries", Type.EXPENSE),
                        new Transaction(2L, userId, new BigDecimal(100), "Transport",
                                LocalDate.of(2025, 3, 10), "Taxi", Type.EXPENSE)));

        String result = budgetService.getFormattedBudget(userId);

        assertThat(result).isEqualTo("Budget: 200.00/1000.00");
    }
}