package com.demo.finance.out.service;

import com.demo.finance.domain.dto.UserDto;


public interface RegistrationService {

    boolean registerUser(UserDto userDto);

    boolean authenticate(UserDto userDto);
}