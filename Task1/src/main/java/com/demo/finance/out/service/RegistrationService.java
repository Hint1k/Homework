package com.demo.finance.out.service;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;

import java.util.Optional;

public interface RegistrationService {

    boolean registerUser(String name, String email, String password, Role role);

    Optional<User> authenticate(String email, String password);
}