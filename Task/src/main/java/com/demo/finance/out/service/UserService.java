package com.demo.finance.out.service;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.PaginatedResponse;

/**
 * The {@code UserService} interface defines the contract for operations related to user management.
 * It provides methods for retrieving, updating, deleting, and paginating user data.
 */
public interface UserService {

    /**
     * Retrieves a {@link User} object by their email address.
     *
     * @param email the email address of the user to retrieve
     * @return the {@link User} object associated with the provided email
     */
    User getUserByEmail(String email);

    /**
     * Retrieves a {@link User} object by their unique identifier.
     *
     * @param userId the unique identifier of the user to retrieve
     * @return the {@link User} object associated with the provided user ID
     */
    User getUserById(Long userId);

    /**
     * Updates the account details of the user with the specified user ID.
     * This method maps the provided {@link UserDto} to a {@link User} entity,
     * preserves the existing role and increments the version, and updates the password
     * only if a new one is provided. Returns true if the update is successful.
     *
     * @param userDto the {@link UserDto} containing updated user details
     * @param userId  the unique identifier of the user whose account is being updated
     * @return true if the account is successfully updated, false otherwise
     */
    boolean updateOwnAccount(UserDto userDto, Long userId);

    /**
     * Deletes the account of the currently authenticated user.
     *
     * @param userId the unique identifier of the user to delete
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    boolean deleteOwnAccount(Long userId);

    /**
     * Retrieves a paginated list of users in the system.
     *
     * @param page the page number to retrieve (zero-based index)
     * @param size the number of users to include per page
     * @return a {@link PaginatedResponse} object containing a paginated list of {@link UserDto} objects
     */
    PaginatedResponse<UserDto> getPaginatedUsers(int page, int size);
}