package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.app.config.DatabaseConfig;
import com.demo.finance.domain.model.User;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryImplTest extends AbstractContainerBaseSetup {

    private UserRepositoryImpl repository;
    private User user;

    @BeforeEach
    void setUp() {
        user = Instancio.create(User.class);
    }

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
        user.setName("Alice");
        user.setEmail("alice@mail.com");
        repository.save(user);

        User found = repository.findByEmail("alice@mail.com");
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("Update user - Success scenario")
    void testUpdateUser() {
        user.setEmail("bob@mail.com");
        repository.save(user);

        User existingUser = repository.findByEmail("bob@mail.com");
        assertThat(existingUser).isNotNull();
        Long userId = existingUser.getUserId();

        user.setUserId(userId);
        user.setName("Bob Updated");
        boolean updated = repository.update(user);

        assertThat(updated).isTrue();
        User found = repository.findById(userId);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Bob Updated");
    }

    @Test
    @DisplayName("Delete user - Success scenario")
    void testDeleteUser() {
        user.setEmail("charlie@mail.com");
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
        user.setName("Dave");
        user.setEmail("dave@mail.com");
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

        repository.save(user);

        user.setEmail("alice2@gmail.com");
        repository.save(user);

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