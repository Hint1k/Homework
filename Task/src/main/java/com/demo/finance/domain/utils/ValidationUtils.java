package com.demo.finance.domain.utils;

import com.demo.finance.domain.dto.UserDto;

public interface ValidationUtils {

    UserDto validateUserJson(String json, Mode mode);

    UserDto validateUserJson(String json, Mode mode, String userId);

    PaginationParams validatePaginationParams(String page, String size);
}