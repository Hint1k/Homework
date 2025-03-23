package com.demo.finance.domain.utils;

import java.util.List;

/**
 * The {@code PaginatedResponse} record represents a paginated response containing a subset of data
 * along with metadata about the pagination. It is used to encapsulate the results of paginated queries,
 * providing details such as the total number of items, total pages, current page, and page size.
 *
 * @param <T>        the type of data contained in the paginated response
 * @param data       the list of items representing the current page of data
 * @param totalItems the total number of items across all pages
 * @param totalPages the total number of pages available
 * @param currentPage the current page number (zero-based index)
 * @param pageSize   the maximum number of items per page
 */
public record PaginatedResponse<T>(List<T> data, int totalItems, int totalPages, int currentPage, int pageSize) {
}