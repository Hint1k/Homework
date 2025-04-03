package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.app.config.DatabaseConfig;
import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryImplTest extends AbstractContainerBaseSetup {

    private UserRepositoryImpl repository;

    @BeforeAll
    void setupRepository() {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.init();
        DataSourceManager dataSourceManager = new DataSourceManager(databaseConfig);
        repository = new UserRepositoryImpl(dataSourceManager);
    }

    @Test
    @DisplayName("Save and find user by user ID - Success scenario")
    void testSaveAndFindUserById() {
        User user = new User(null, "Alice", "alice@mail.com", "password123",
                false, new Role("user"), 1L);
        repository.save(user);

        User found = repository.findByEmail("alice@mail.com");
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("Update user - Success scenario")
    void testUpdateUser() {
        User user = new User(null, "Bob", "bob@mail.com", "securepass",
                false, new Role("user"), 1L);
        repository.save(user);

        User existingUser = repository.findByEmail("bob@mail.com");
        assertThat(existingUser).isNotNull();
        Long userId = existingUser.getUserId();

        User updatedUser = new User(userId, "Bob Updated", "bob@mail.com", "newpass",
                false, new Role("admin"), 2L);
        boolean updated = repository.update(updatedUser);

        assertThat(updated).isTrue();
        User found = repository.findById(userId);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Bob Updated");
    }

    @Test
    @DisplayName("Delete user - Success scenario")
    void testDeleteUser() {
        User user = new User(null, "Charlie", "charlie@mail.com", "mypassword",
                false, new Role("admin"), 1L);
        repository.save(user);

        User existingUser = repository.findByEmail("charlie@mail.com");
        assertThat(existingUser).isNotNull();
        Long userId = existingUser.getUserId();

        boolean deleted = repository.delete(userId);

        assertThat(deleted).isTrue();
        assertThat(repository.findById(userId)).isNull();
    }

    @Test
    @DisplayName("Find by user ID - Non-existent user returns empty Optional")
    void testFindByUserId_NonExistentUser_ReturnsEmptyOptional() {
        assertThat(repository.findById(999L)).isNull();
    }

    @Test
    @DisplayName("Find by email - User exists returns user")
    void testFindByEmail_UserExists_ReturnsUser() {
        User user = new User(null, "Dave", "dave@mail.com", "password456",
                false, new Role("user"), 1L);
        repository.save(user);

        User found = repository.findByEmail("dave@mail.com");

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Dave");
    }

    @Test
    @DisplayName("Find by email - Non-existent email returns empty Optional")
    void testFindByEmail_NonExistentEmail_ReturnsEmptyOptional() {
        assertThat(repository.findByEmail("nonexistent@mail.com")).isNull();
    }

    @Test
    @DisplayName("Find all users - Users exist returns all users")
    void testFindAll_UsersExist_ReturnsAllUsers() {
        repository.findAll(0, 10).forEach(u -> repository.delete(u.getUserId()));
        repository.save(new User(null, "Emma", "emma@mail.com", "pass1",
                false, new Role("user"), 1L));
        repository.save(new User(null, "Frank", "frank@mail.com", "pass2",
                false, new Role("admin"), 1L));

        List<User> users = repository.findAll(0, 10);
        assertThat(users).hasSize(2);
    }

    @Test
    @DisplayName("Find all users - No users returns empty list")
    void testFindAll_NoUsers_ReturnsEmptyList() {
        repository.findAll(0, 10).forEach(u -> repository.delete(u.getUserId()));
        List<User> users = repository.findAll(0, 10);
        assertThat(users).isEmpty();
    }
}