package com.demo.finance.exception;

public class BudgetExceededException extends RuntimeException {
    public BudgetExceededException(double limit, double attemptedAmount) {
        super("Budget limit of " + limit + " exceeded. Attempted transaction: " + attemptedAmount);
    }
}