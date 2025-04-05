package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.impl.PasswordUtilsImpl;
import com.demo.finance.exception.DuplicateEmailException;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.RegistrationService;
import com.demo.finance.domain.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The {@code RegistrationServiceImpl} class implements the {@link RegistrationService} interface
 * and provides concrete implementations for user registration and authentication operations.
 * It interacts with the database through the {@link UserRepository} and handles logic for
 * securely registering users and verifying their credentials.
 */
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final PasswordUtilsImpl passwordUtils;
    private final UserMapper userMapper;

    /**
     * Registers a new user in the system after performing necessary validations and transformations.
     * <p>
     * This method maps the provided {@link UserDto} to a {@link User} entity, checks for duplicate email addresses,
     * hashes the user's password, sets default values for blocked status, role, and version, and saves the user
     * to the repository.
     *
     * @param userDto the data transfer object containing user registration details.
     * @return true if the user is successfully registered and saved in the repository.
     * @throws DuplicateEmailException if the email address provided in the {@link UserDto} is already registered.
     * @throws RuntimeException        if an unexpected error occurs during the registration process.
     */
    @Override
    public boolean registerUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new DuplicateEmailException("Email is already registered: " + user.getEmail());
        }
        user.setPassword(passwordUtils.hashPassword(userDto.getPassword()));
        user.setBlocked(false);
        user.setRole(new Role("user"));
        user.setVersion(1L);
        userRepository.save(user);
        return true;
    }

    /**
     * Authenticates a user by verifying their credentials against the system's records.
     * Checks if the provided email exists and validates the password using secure hashing techniques.
     *
     * @param userDto the {@link UserDto} object containing the user's authentication details (e.g., email and password)
     * @return {@code true} if the authentication was successful, {@code false} otherwise
     */
    @Override
    public boolean authenticate(UserDto userDto) {
        String email = userDto.getEmail();
        String password = userDto.getPassword();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }
        return passwordUtils.checkPassword(password, user.getPassword());
    }
}