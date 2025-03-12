package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @InjectMocks private UserRepositoryImpl repository;

    @Test
    @DisplayName("Save and find user by user ID - Success scenario")
    void testSaveAndFindUserById() {
        User user = new User(1L, "Alice", "alice@mail.com", "password123",
                false, new Role("user"));
        repository.save(user);

        Optional<User> found = repository.findByUserId(1L);

        assertThat(found).isPresent().contains(user);
    }

    @Test
    @DisplayName("Update user - Success scenario")
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
    @DisplayName("Delete user - Success scenario")
    void testDeleteUser() {
        User user = new User(3L, "Charlie", "charlie@mail.com", "mypassword",
                false, new Role("admin"));
        repository.save(user);

        boolean deleted = repository.delete(3L);

        assertThat(deleted).isTrue();
        assertThat(repository.findByUserId(3L)).isEmpty();
    }

    @Test
    @DisplayName("Find by user ID - Non-existent user returns empty Optional")
    void testFindByUserId_NonExistentUser_ReturnsEmptyOptional() {
        Optional<User> found = repository.findByUserId(999L);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Find by email - User exists returns user")
    void testFindByEmail_UserExists_ReturnsUser() {
        User user = new User(4L, "Dave", "dave@mail.com", "password456",
                false, new Role("user"));
        repository.save(user);

        Optional<User> found = repository.findByEmail("dave@mail.com");

        assertThat(found).isPresent().contains(user);
    }

    @Test
    @DisplayName("Find by email - Non-existent email returns empty Optional")
    void testFindByEmail_NonExistentEmail_ReturnsEmptyOptional() {
        assertThat(repository.findByEmail("nonexistent@mail.com")).isEmpty();
    }

    @Test
    @DisplayName("Find all users - Users exist returns all users")
    void testFindAll_UsersExist_ReturnsAllUsers() {
        repository.save(new User(5L, "Emma", "emma@mail.com", "pass1",
                false, new Role("user")));
        repository.save(new User(6L, "Frank", "frank@mail.com", "pass2",
                false, new Role("admin")));

        assertThat(repository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("Find all users - No users returns empty list")
    void testFindAll_NoUsers_ReturnsEmptyList() {
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Generate next ID - Empty repository returns 1")
    void testGenerateNextId_EmptyRepository_ReturnsOne() {
        assertThat(repository.generateNextId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Generate next ID - Non-empty repository returns next ID")
    void testGenerateNextId_NonEmptyRepository_ReturnsNextId() {
        repository.save(new User(7L, "George", "george@mail.com", "pass3",
                false, new Role("user")));

        assertThat(repository.generateNextId()).isEqualTo(8L);
    }
}