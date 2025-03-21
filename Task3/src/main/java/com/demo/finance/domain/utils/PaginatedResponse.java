package com.demo.finance.domain.utils;

import java.util.List;

public record PaginatedResponse<T>(List<T> data, int totalItems, int totalPages, int currentPage, int pageSize) {
}