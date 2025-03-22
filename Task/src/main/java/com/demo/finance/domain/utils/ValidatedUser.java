package com.demo.finance.domain.utils;

import com.demo.finance.domain.dto.UserDto;

public record ValidatedUser(UserDto userDto, String password) {
}