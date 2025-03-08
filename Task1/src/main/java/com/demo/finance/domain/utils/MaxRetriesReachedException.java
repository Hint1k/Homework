package com.demo.finance.domain.utils;

public class MaxRetriesReachedException extends RuntimeException {

    public MaxRetriesReachedException(String message) {
        super(message);
    }
}