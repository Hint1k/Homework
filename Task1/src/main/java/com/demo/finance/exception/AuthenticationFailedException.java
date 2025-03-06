package com.demo.finance.exception;

public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException() {
        super("Authentication failed: Invalid email or password.");
    }
}