package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionRepositoryImplTest {

    @InjectMocks private TransactionRepositoryImpl repository;

    @Test
    void testSaveAndFindTransactionById() {
        Transaction transaction = new Transaction(1L, 2L, 100.0, "Groceries",
                LocalDate.now(), "Weekly shopping", Type.EXPENSE);
        repository.save(transaction);

        Optional<Transaction> found = repository.findByUserIdAndTransactionId(1L, 2L);

        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(transaction);
    }

    @Test
    void testUpdateTransaction() {
        Transaction transaction = new Transaction(12L, 2L, 150.0, "Rent",
                LocalDate.now(), "Monthly rent", Type.EXPENSE);
        repository.save(transaction);

        Transaction updatedTransaction = new Transaction(12L, 2L, 200.0, "Rent",
                LocalDate.now(), "Updated rent", Type.EXPENSE);
        boolean updated = repository.update(updatedTransaction);

        assertThat(updated).isTrue();
        assertThat(repository.findByUserIdAndTransactionId(12L, 2L)).contains(updatedTransaction);
    }

    @Test
    void testDeleteTransaction() {
        Transaction transaction = new Transaction(13L, 2L, 50.0, "Transport",
                LocalDate.now(), "Bus fare", Type.EXPENSE);
        repository.save(transaction);

        boolean deleted = repository.delete(13L);

        assertThat(deleted).isTrue();
        assertThat(repository.findByUserIdAndTransactionId(13L, 2L)).isEmpty();
    }

    @Test
    void testFindTransactionsByUserId() {
        repository.save(new Transaction(14L, 3L, 30.0, "Food",
                LocalDate.now(), "Lunch", Type.EXPENSE));
        repository.save(new Transaction(15L, 3L, 70.0, "Entertainment",
                LocalDate.now(), "Concert ticket", Type.EXPENSE));

        List<Transaction> transactions = repository.findByUserId(3L);

        assertThat(transactions).hasSize(2);
    }

    @Test
    void testFindByTransactionId_NullId_ThrowsException() {
        assertThatThrownBy(() -> repository.findByTransactionId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transaction ID cannot be null.");
    }

    @Test
    void testFindByTransactionId_NonExistentTransaction_ReturnsNull() {
        assertThat(repository.findByTransactionId(999L)).isNull();
    }

    @Test
    void testFindFiltered_OnlyByUserId_ReturnsAllTransactionsForUser() {
        repository.save(new Transaction(20L, 4L, 100.0, "Shopping",
                LocalDate.of(2024, 3, 1), "Bought clothes", Type.EXPENSE));
        repository.save(new Transaction(21L, 4L, 50.0, "Transport",
                LocalDate.of(2024, 3, 2), "Bus fare", Type.EXPENSE));

        List<Transaction> transactions =
                repository.findFiltered(4L, null, null, null, null);

        assertThat(transactions).hasSize(2);
    }

    @Test
    void testFindFiltered_WithDateRange_ReturnsMatchingTransactions() {
        repository.save(new Transaction(22L, 5L, 200.0, "Utilities",
                LocalDate.of(2024, 2, 10), "Electricity bill", Type.EXPENSE));
        repository.save(new Transaction(23L, 5L, 100.0, "Utilities",
                LocalDate.of(2024, 3, 5), "Water bill", Type.EXPENSE));

        List<Transaction> transactions = repository.findFiltered(5L,
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2024, 3, 31), null, null);

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getTransactionId()).isEqualTo(23L);
    }

    @Test
    void testFindFiltered_WithCategoryAndType_ReturnsMatchingTransactions() {
        repository.save(new Transaction(24L, 6L, 500.0, "Salary",
                LocalDate.of(2024, 3, 1), "Monthly salary", Type.INCOME));
        repository.save(new Transaction(25L, 6L, 50.0, "Groceries",
                LocalDate.of(2024, 3, 2), "Supermarket shopping", Type.EXPENSE));

        List<Transaction> transactions =
                repository.findFiltered(6L, null, null, "Salary", Type.INCOME);

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getTransactionId()).isEqualTo(24L);
    }

    @Test
    void testGenerateNextId_EmptyRepository_ReturnsOne() {
        assertThat(repository.generateNextId()).isEqualTo(1L);
    }

    @Test
    void testGenerateNextId_NonEmptyRepository_ReturnsNextId() {
        repository.save(new Transaction(30L, 7L, 300.0, "Investment",
                LocalDate.of(2024, 3, 5), "Stocks", Type.INCOME));

        assertThat(repository.generateNextId()).isEqualTo(31L);
    }
}