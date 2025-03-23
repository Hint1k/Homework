package com.demo.finance.out.repository;

import com.demo.finance.domain.model.User;

import java.util.List;

/**
 * The {@code UserRepository} interface defines the contract for operations related to user data persistence.
 * It provides methods for saving, updating, deleting, and retrieving users from the database.
 */
public interface UserRepository {

    /**
     * Saves a new user to the database.
     *
     * @param user the {@link User} object to be saved
     */
    void save(User user);

    /**
     * Updates an existing user in the database.
     *
     * @param user the {@link User} object containing updated information
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    boolean update(User user);

    /**
     * Deletes a user from the database based on their unique user ID.
     *
     * @param userId the unique identifier of the user to delete
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    boolean delete(Long userId);

    /**
     * Retrieves a paginated list of users from the database.
     *
     * @param offset the starting index for pagination (zero-based)
     * @param size   the maximum number of users to retrieve
     * @return a {@link List} of {@link User} objects representing the paginated results
     */
    List<User> findAll(int offset, int size);

    /**
     * Retrieves the total count of users in the database.
     *
     * @return the total number of users as an integer
     */
    int getTotalUserCount();

    /**
     * Retrieves a specific user by their unique user ID.
     *
     * @param userId the unique identifier of the user
     * @return the {@link User} object matching the provided user ID, or {@code null} if not found
     */
    User findById(Long userId);

    /**
     * Retrieves a specific user by their email address.
     *
     * @param email the email address of the user
     * @return the {@link User} object matching the provided email, or {@code null} if not found
     */
    User findByEmail(String email);
}