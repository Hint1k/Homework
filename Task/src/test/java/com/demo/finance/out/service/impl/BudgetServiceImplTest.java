package com.demo.finance.out.service.impl;

import com.demo.finance.domain.model.Budget;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.BudgetRepository;
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
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetServiceImplTest {

    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private BudgetServiceImpl budgetService;
    private Budget budget;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        budget = Instancio.create(Budget.class);
        budget.setMonthlyLimit(BigDecimal.valueOf(1000));
        budget.setBudgetId(1L);
        budget.setUserId(1L);
        transaction = Instancio.create(Transaction.class);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setType(Type.EXPENSE);
    }

    @Test
    @DisplayName("Set monthly budget - new budget - returns budget")
    void testSetMonthlyBudget_newBudget_returnsBudget() {
        Long userId = budget.getUserId();
        BigDecimal limit = budget.getMonthlyLimit();

        when(budgetRepository.findByUserId(userId)).thenReturn(null).thenReturn(budget);
        when(budgetRepository.save(any(Budget.class))).thenReturn(true);
        Budget result = budgetService.setMonthlyBudget(userId, limit);

        verify(budgetRepository, times(2)).findByUserId(userId);
        verify(budgetRepository).save(any(Budget.class));

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getMonthlyLimit()).isEqualTo(limit);
    }

    @Test
    @DisplayName("Set monthly budget - update existing - returns updated budget")
    void testSetMonthlyBudget_updateExisting_returnsUpdatedBudget() {
        Long userId = budget.getUserId();
        BigDecimal newLimit = new BigDecimal(1500);

        when(budgetRepository.findByUserId(userId)).thenReturn(budget);

        budget.setMonthlyLimit(newLimit);

        when(budgetRepository.update(budget)).thenReturn(true);
        when(budgetRepository.findByUserId(userId)).thenReturn(budget);

        Budget result = budgetService.setMonthlyBudget(userId, newLimit);

        assertThat(result).isNotNull();
        assertThat(result.getMonthlyLimit()).isEqualTo(newLimit);
        verify(budgetRepository).update(budget);
    }

    @Test
    @DisplayName("Get budget - existing budget - returns budget")
    void testGetBudget_existingBudget_returnsBudget() {
        Long userId = budget.getUserId();

        when(budgetRepository.findByUserId(userId)).thenReturn(budget);

        Budget result = budgetService.getBudget(userId);

        assertThat(result).isEqualTo(budget);
    }

    @Test
    @DisplayName("Calculate expenses for month - has transactions - returns sum")
    void testCalculateExpensesForMonth_hasTransactions_returnsSum() {
        Long userId = 1L;
        YearMonth month = YearMonth.of(2025, 3);
        List<Transaction> transactions = List.of(transaction, transaction);

        when(transactionRepository
                .findFiltered(userId, month.atDay(1), month.atEndOfMonth(), null, Type.EXPENSE))
                .thenReturn(transactions);

        BigDecimal result = budgetService.calculateExpensesForMonth(userId, month);

        assertThat(result).isEqualTo(new BigDecimal(200));
    }

    @Test
    @DisplayName("Get budget data - no budget - returns message")
    void testGetBudgetData_noBudget_returnsMessage() {
        Long userId = 1L;
        YearMonth month = YearMonth.now();
        List<Transaction> transactions = List.of(transaction);

        when(budgetRepository.findByUserId(userId)).thenReturn(null);
        when(transactionRepository
                .findFiltered(userId, month.atDay(1), month.atEndOfMonth(), null, Type.EXPENSE))
                .thenReturn(transactions);

        Map<String, Object> result = budgetService.getBudgetData(userId);

        assertThat(result.get("message")).isEqualTo("Budget is not set");
        assertThat(((Map<?, ?>) result.get("data")).get("monthlyLimit")).isEqualTo(BigDecimal.ZERO);
        assertThat(((Map<?, ?>) result.get("data")).get("currentExpenses")).isEqualTo(new BigDecimal(100));
    }

    @Test
    @DisplayName("Get budget data - with budget - returns formatted data")
    void testGetBudgetData_withBudget_returnsFormattedData() {
        Long userId = 1L;
        YearMonth month = YearMonth.now();
        Budget budget = new Budget(userId, new BigDecimal(1000));
        List<Transaction> transactions = List.of(transaction);

        when(budgetRepository.findByUserId(userId)).thenReturn(budget);
        when(transactionRepository
                .findFiltered(userId, month.atDay(1), month.atEndOfMonth(), null, Type.EXPENSE))
                .thenReturn(transactions);

        Map<String, Object> result = budgetService.getBudgetData(userId);

        assertThat(result.get("formattedBudget")).isEqualTo("Budget: 100.00/1000.00");
        assertThat(((Map<?, ?>) result.get("budgetData")).get("monthlyLimit")).isEqualTo(new BigDecimal(1000));
        assertThat(((Map<?, ?>) result.get("budgetData")).get("currentExpenses")).isEqualTo(new BigDecimal(100));
    }
}