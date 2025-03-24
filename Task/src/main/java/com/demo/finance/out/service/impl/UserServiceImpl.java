package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.domain.utils.impl.PasswordUtilsImpl;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.UserService;
import com.demo.finance.domain.mapper.UserMapper;

import java.util.List;

/**
 * The {@code UserServiceImpl} class implements the {@link UserService} interface
 * and provides concrete implementations for user-related operations.
 * It interacts with the database through the {@link UserRepository} and handles logic for retrieving,
 * updating, deleting, and paginating users.
 */
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordUtilsImpl passwordUtils;

    /**
     * Constructs a new instance of {@code UserServiceImpl} with the provided repository and password utility.
     *
     * @param userRepository the repository used to interact with user data in the database
     * @param passwordUtils  the utility class used for password hashing and validation
     */
    public UserServiceImpl(UserRepository userRepository, PasswordUtilsImpl passwordUtils) {
        this.userRepository = userRepository;
        this.passwordUtils = passwordUtils;
    }

    /**
     * Retrieves a user from the database by their email address.
     *
     * @param email the email address of the user to retrieve
     * @return the {@link User} object associated with the provided email, or {@code null} if not found
     */
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Updates the details of the currently authenticated user's account.
     * Handles password updates securely by hashing the new password if it has changed.
     *
     * @param userDto the {@link UserDto} object containing updated user information
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    @Override
    public boolean updateOwnAccount(UserDto userDto) {
        User user = UserMapper.INSTANCE.toEntity(userDto);
        User existingUser = userRepository.findById(user.getUserId());
        if (existingUser == null) {
            return false;
        }
        if (userDto.getPassword() != null && !userDto.getPassword().equals(existingUser.getPassword())) {
            user.setPassword(passwordUtils.hashPassword(userDto.getPassword()));
        } else {
            user.setPassword(existingUser.getPassword());
        }
        user.setRole(existingUser.getRole());
        user.setVersion(existingUser.getVersion() + 1);
        return userRepository.update(user);
    }

    /**
     * Deletes the account of the currently authenticated user from the database.
     *
     * @param userId the unique identifier of the user to delete
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    @Override
    public boolean deleteOwnAccount(Long userId) {
        return userRepository.delete(userId);
    }

    /**
     * Retrieves a paginated list of users from the database.
     * Calculates pagination metadata such as total pages and total users.
     *
     * @param page the page number to retrieve (one-based index)
     * @param size the number of users to include per page
     * @return a {@link PaginatedResponse} object containing a paginated list of {@link UserDto} objects
     */
    @Override
    public PaginatedResponse<UserDto> getPaginatedUsers(int page, int size) {
        int offset = (page - 1) * size;
        List<User> users = userRepository.findAll(offset, size);
        int totalUsers = userRepository.getTotalUserCount();
        List<UserDto> dtoList = users.stream().map(user ->
                UserDto.removePassword(UserMapper.INSTANCE.toDto(user))).toList();

        return new PaginatedResponse<>(dtoList, totalUsers, (int) Math.ceil((double) totalUsers / size), page, size);
    }
}