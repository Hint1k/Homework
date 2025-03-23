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
     * Updates the details of the currently authenticated user's account.
     *
     * @param userDto the {@link UserDto} object containing updated user information
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    boolean updateOwnAccount(UserDto userDto);

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