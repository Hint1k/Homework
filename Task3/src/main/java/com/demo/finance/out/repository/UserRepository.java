package com.demo.finance.out.repository;

import com.demo.finance.domain.model.User;

import java.util.List;
import java.util.Optional;

/**
 * The {@code UserRepository} interface defines the contract for operations related to user data persistence.
 * It provides methods for saving, updating, deleting, and retrieving users, along with generating the next user ID.
 */
public interface UserRepository {

    /**
     * Saves the specified {@code User} object in the repository.
     *
     * @param user the {@code User} object to be saved
     */
    void save(User user);

    /**
     * Updates the details of the specified {@code User}.
     *
     * @param user the {@code User} object containing updated details
     * @return {@code true} if the user was successfully updated, {@code false} otherwise
     */
    boolean update(User user);

    /**
     * Deletes the {@code User} associated with the specified user ID from the repository.
     *
     * @param userId the ID of the user to be deleted
     * @return {@code true} if the user was successfully deleted, {@code false} otherwise
     */
    boolean delete(Long userId);

    /**
     * Retrieves all {@code User} objects from the repository.
     *
     * @return a {@code List<User>} containing all users in the repository
     */
    List<User> findAll();

    /**
     * Retrieves the {@code User} associated with the specified user ID.
     *
     * @param userId the ID of the user to be retrieved
     * @return an {@code Optional<User>} containing the user if found, or an empty {@code Optional} if not found
     */
    Optional<User> findById(Long userId);

    /**
     * Retrieves the {@code User} associated with the specified email.
     *
     * @param email the email of the user to be retrieved
     * @return an {@code Optional<User>} containing the user if found, or an empty {@code Optional} if not found
     */
    Optional<User> findByEmail(String email);
}