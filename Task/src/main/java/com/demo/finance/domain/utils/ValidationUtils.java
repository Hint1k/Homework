package com.demo.finance.domain.utils;

public interface ValidationUtils {

    ValidatedUser validateUserJson(String json, Mode mode);

    ValidatedUser validateUserJson(String json, Mode mode, String userId);

    PaginationParams validatePaginationParams(String page, String size);
}