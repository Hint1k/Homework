package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.impl.UserRepositoryImpl;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryImplTest extends AbstractContainerBaseTest {

    private UserRepositoryImpl repository;

    @BeforeAll
    void setupRepository() {
        repository = new UserRepositoryImpl();
    }

    @BeforeEach
    void cleanDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection(
                POSTGRESQL_CONTAINER.getJdbcUrl(),
                POSTGRESQL_CONTAINER.getUsername(),
                POSTGRESQL_CONTAINER.getPassword());
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM finance.users");
        }
    }

    @Test
    @DisplayName("Save and find user by user ID - Success scenario")
    void testSaveAndFindUserById() {
        User user = new User(null, "Alice", "alice@mail.com", "password123",
                false, new Role("user"));
        repository.save(user);

        Optional<User> found = repository.findByEmail("alice@mail.com");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("Update user - Success scenario")
    void testUpdateUser() {
        User user = new User(null, "Bob", "bob@mail.com", "securepass",
                false, new Role("user"));
        repository.save(user);

        Optional<User> existingUser = repository.findByEmail("bob@mail.com");
        assertThat(existingUser).isPresent();
        Long userId = existingUser.get().getUserId();

        User updatedUser = new User(userId, "Bob Updated", "bob@mail.com", "newpass",
                false, new Role("admin"));
        boolean updated = repository.update(updatedUser);

        assertThat(updated).isTrue();
        Optional<User> found = repository.findById(userId);
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Bob Updated");
    }

    @Test
    @DisplayName("Delete user - Success scenario")
    void testDeleteUser() {
        User user = new User(null, "Charlie", "charlie@mail.com", "mypassword",
                false, new Role("admin"));
        repository.save(user);

        Optional<User> existingUser = repository.findByEmail("charlie@mail.com");
        assertThat(existingUser).isPresent();
        Long userId = existingUser.get().getUserId();

        boolean deleted = repository.delete(userId);

        assertThat(deleted).isTrue();
        assertThat(repository.findById(userId)).isEmpty();
    }

    @Test
    @DisplayName("Find by user ID - Non-existent user returns empty Optional")
    void testFindByUserId_NonExistentUser_ReturnsEmptyOptional() {
        assertThat(repository.findById(999L)).isEmpty();
    }

    @Test
    @DisplayName("Find by email - User exists returns user")
    void testFindByEmail_UserExists_ReturnsUser() {
        User user = new User(null, "Dave", "dave@mail.com", "password456",
                false, new Role("user"));
        repository.save(user);

        Optional<User> found = repository.findByEmail("dave@mail.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Dave");
    }

    @Test
    @DisplayName("Find by email - Non-existent email returns empty Optional")
    void testFindByEmail_NonExistentEmail_ReturnsEmptyOptional() {
        assertThat(repository.findByEmail("nonexistent@mail.com")).isEmpty();
    }

    @Test
    @DisplayName("Find all users - Users exist returns all users")
    void testFindAll_UsersExist_ReturnsAllUsers() {
        repository.save(new User(null, "Emma", "emma@mail.com", "pass1",
                false, new Role("user")));
        repository.save(new User(null, "Frank", "frank@mail.com", "pass2",
                false, new Role("admin")));

        assertThat(repository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("Find all users - No users returns empty list")
    void testFindAll_NoUsers_ReturnsEmptyList() {
        assertThat(repository.findAll()).isEmpty();
    }

    @AfterAll
    static void stopContainer() {
        POSTGRESQL_CONTAINER.stop();
    }
}