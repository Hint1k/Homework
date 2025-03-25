package com.demo.finance.domain.utils;

/**
 * The {@code PaginationParams} record represents pagination parameters used for paginated requests.
 * It encapsulates the page number and page size, which are commonly used to control the subset of data
 * returned in a paginated response.
 *
 * @param page the page number (zero-based index) for pagination
 * @param size the maximum number of items to include in a single page
 */
public record PaginationParams(int page, int size) {
}