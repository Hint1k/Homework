package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @InjectMocks
    private UserRepositoryImpl repository;

    @Test
    void testSaveAndFindUserById() {
        User user = new User(1L, "Alice", "alice@mail.com", "password123",
                false, new Role("user"));
        repository.save(user);

        Optional<User> found = repository.findByUserId(1L);

        assertThat(found).isPresent().contains(user);
    }

    @Test
    void testUpdateUser() {
        User user = new User(2L, "Bob", "bob@mail.com", "securepass",
                false, new Role("user"));
        repository.save(user);

        User updatedUser = new User(2L, "Bob Updated", "bob@mail.com", "newpass",
                false, new Role("user"));
        boolean updated = repository.update(updatedUser);

        assertThat(updated).isTrue();
        assertThat(repository.findByUserId(2L)).contains(updatedUser);
    }

    @Test
    void testDeleteUser() {
        User user = new User(3L, "Charlie", "charlie@mail.com", "mypassword",
                false, new Role("admin"));
        repository.save(user);

        boolean deleted = repository.delete(3L);

        assertThat(deleted).isTrue();
        assertThat(repository.findByUserId(3L)).isEmpty();
    }
}