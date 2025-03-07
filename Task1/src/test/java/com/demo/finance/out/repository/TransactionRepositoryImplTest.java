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

    @InjectMocks
    private TransactionRepositoryImpl repository;

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
}