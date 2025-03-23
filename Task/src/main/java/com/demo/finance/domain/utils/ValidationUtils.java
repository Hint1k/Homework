package com.demo.finance.domain.utils;

import com.demo.finance.domain.dto.ReportDto;
import com.demo.finance.domain.dto.UserDto;

import java.time.LocalDate;
import java.util.Map;

public interface ValidationUtils {

    UserDto validateUserJson(String json, Mode mode);

    UserDto validateUserJson(String json, Mode mode, String userId);

    UserDto validateUserJson(String json, Mode mode, Long userId);

    Long parseUserId(String userId, Mode mode);

    PaginationParams validatePaginationParams(String page, String size);

    Map<String, LocalDate> validateReport(String json, Mode mode, Long userId);
}