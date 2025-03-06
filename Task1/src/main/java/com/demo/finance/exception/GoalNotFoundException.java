package com.demo.finance.exception;

public class GoalNotFoundException extends RuntimeException {
    public GoalNotFoundException(String goalName) {
        super("Goal '" + goalName + "' not found.");
    }
}