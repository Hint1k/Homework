package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.domain.utils.impl.PasswordUtilsImpl;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.UserService;
import com.demo.finance.domain.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The {@code UserServiceImpl} class implements the {@link UserService} interface
 * and provides concrete implementations for user-related operations.
 * It interacts with the database through the {@link UserRepository} and handles logic for retrieving,
 * updating, deleting, and paginating users.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordUtilsImpl passwordUtils;
    private final UserMapper userMapper;

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
     * Updates the account details of the user with the specified user ID.
     * This method maps the provided {@link UserDto} to a {@link User} entity,
     * preserves the existing role and increments the version, and updates the password
     * only if a new one is provided. Returns true if the update is successful.
     *
     * @param userDto the {@link UserDto} containing updated user details
     * @param userId  the unique identifier of the user whose account is being updated
     * @return true if the account is successfully updated, false otherwise
     */
    @Override
    public boolean updateOwnAccount(UserDto userDto, Long userId) {
        User user = userMapper.toEntity(userDto);
        User existingUser = userRepository.findById(userId);
        if (existingUser == null) {
            return false;
        }
        if (userDto.getPassword() != null && !userDto.getPassword().equals(existingUser.getPassword())) {
            user.setPassword(passwordUtils.hashPassword(userDto.getPassword()));
        } else {
            user.setPassword(existingUser.getPassword());
        }
        user.setUserId(userId);
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
                UserDto.removePassword(userMapper.toDto(user))).toList();

        return new PaginatedResponse<>(dtoList, totalUsers, (int) Math.ceil((double) totalUsers / size), page, size);
    }
}