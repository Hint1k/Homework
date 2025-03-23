package com.demo.finance.out.service;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.User;

public interface AdminService {

    User getUser(Long userId);

    boolean updateUserRole(UserDto userDto);

    boolean blockOrUnblockUser(Long userId, boolean blocked);

    boolean deleteUser(Long userId);
}