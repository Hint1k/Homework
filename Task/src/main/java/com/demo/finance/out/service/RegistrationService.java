package com.demo.finance.out.service;

import com.demo.finance.domain.dto.UserDto;

/**
 * The {@code RegistrationService} interface defines the methods required for user registration
 * and authentication services.
 */
public interface RegistrationService {

    boolean registerUser(UserDto userDtor);

    boolean authenticate(UserDto userDto);
}