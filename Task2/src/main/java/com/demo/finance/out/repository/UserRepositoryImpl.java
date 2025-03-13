package com.demo.finance.out.repository;

import com.demo.finance.domain.model.User;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@code UserRepositoryImpl} class provides an in-memory implementation of the {@code UserRepository} interface.
 * It manages a collection of {@code User} objects using a concurrent hash map.
 */
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new ConcurrentHashMap<>();

    /**
     * Saves the specified {@code User} object in the repository.
     *
     * @param user the {@code User} object to be saved
     */
    @Override
    public void save(User user) {
        users.put(user.getUserId(), user);
    }

    /**
     * Updates the specified {@code User} in the repository.
     * If the user exists, their information will be updated.
     *
     * @param user the {@code User} object containing updated information
     * @return {@code true} if the user was updated successfully, {@code false} if the user does not exist
     */
    @Override
    public boolean update(User user) {
        if (users.containsKey(user.getUserId())) {
            users.put(user.getUserId(), user);
            return true;
        }
        return false;
    }

    /**
     * Deletes the {@code User} with the specified user ID from the repository.
     *
     * @param userId the ID of the user to be deleted
     * @return {@code true} if the user was successfully deleted, {@code false} if the user does not exist
     */
    @Override
    public boolean delete(Long userId) {
        if (users.containsKey(userId)) {
            users.remove(userId);
            return true;
        }
        return false;
    }

    /**
     * Retrieves all {@code User} objects from the repository.
     *
     * @return a {@code List<User>} containing all users in the repository
     */
    @Override
    public List<User> findAll(){
        return new ArrayList<>(users.values());
    }

    /**
     * Retrieves the {@code User} associated with the specified user ID.
     *
     * @param userId the ID of the user to be retrieved
     * @return an {@code Optional<User>} containing the user if found, or an empty {@code Optional} if not found
     */
    @Override
    public Optional<User> findByUserId(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    /**
     * Retrieves the {@code User} associated with the specified email.
     *
     * @param email the email of the user to be retrieved
     * @return an {@code Optional<User>} containing the user if found, or an empty {@code Optional} if not found
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    /**
     * Generates the next available user ID by finding the maximum existing ID and incrementing it by 1.
     *
     * @return the next available user ID
     */
    @Override
    public Long generateNextId() {
        return users.keySet().stream().max(Long::compare).orElse(0L) + 1;
    }
}