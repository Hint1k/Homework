package com.demo.finance.domain.utils;

import com.demo.finance.domain.dto.TransactionDto;

public interface TranValidationUtils {

    TransactionDto validateTransactionJson(String json, Mode mode);

    Long parseTransactionId(String transactionIdString, Mode mode);

    PaginationParams validatePaginationParams(String page, String size);

    TransactionDto validateTransactionJson(String json, Mode mode, String transactionId);
}