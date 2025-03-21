package com.demo.finance.out.service;

import com.demo.finance.domain.utils.ValidatedUser;

/**
 * The {@code RegistrationService} interface defines the methods required for user registration
 * and authentication services.
 */
public interface RegistrationService {

    boolean registerUser(ValidatedUser validatedUser);

    boolean authenticate(ValidatedUser validatedUser);
}