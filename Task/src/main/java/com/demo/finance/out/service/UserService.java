package com.demo.finance.out.service;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.PaginatedResponse;

public interface UserService {

    User getUserByEmail(String email);

    boolean updateOwnAccount(UserDto userDto);

    boolean deleteOwnAccount(Long userId);

    PaginatedResponse<UserDto> getPaginatedUsers(int page, int size);
}