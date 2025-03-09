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

    @InjectMocks private UserRepositoryImpl repository;

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

    @Test
    void testFindByUserId_NonExistentUser_ReturnsEmptyOptional() {
        Optional<User> found = repository.findByUserId(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void testFindByEmail_UserExists_ReturnsUser() {
        User user = new User(4L, "Dave", "dave@mail.com", "password456",
                false, new Role("user"));
        repository.save(user);

        Optional<User> found = repository.findByEmail("dave@mail.com");

        assertThat(found).isPresent().contains(user);
    }

    @Test
    void testFindByEmail_NonExistentEmail_ReturnsEmptyOptional() {
        assertThat(repository.findByEmail("nonexistent@mail.com")).isEmpty();
    }

    @Test
    void testFindAll_UsersExist_ReturnsAllUsers() {
        repository.save(new User(5L, "Emma", "emma@mail.com", "pass1",
                false, new Role("user")));
        repository.save(new User(6L, "Frank", "frank@mail.com", "pass2",
                false, new Role("admin")));

        assertThat(repository.findAll()).hasSize(2);
    }

    @Test
    void testFindAll_NoUsers_ReturnsEmptyList() {
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void testGenerateNextId_EmptyRepository_ReturnsOne() {
        assertThat(repository.generateNextId()).isEqualTo(1L);
    }

    @Test
    void testGenerateNextId_NonEmptyRepository_ReturnsNextId() {
        repository.save(new User(7L, "George", "george@mail.com", "pass3",
                false, new Role("user")));

        assertThat(repository.generateNextId()).isEqualTo(8L);
    }
}