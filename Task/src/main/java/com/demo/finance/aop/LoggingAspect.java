package com.demo.finance.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * The {@code LoggingAspect} class is an Aspect-Oriented Programming (AOP) aspect that provides logging
 * for method execution within the application. It logs method execution times, identifies slow methods,
 * and captures exceptions during method execution.
 */
@Aspect
public class LoggingAspect {

    private static final Logger log = Logger.getLogger(LoggingAspect.class.getName());
    private static final long SLOW_METHOD_THRESHOLD_MS = 500;

    /**
     * Defines a pointcut that matches all methods in the {@code com.demo.finance} package and its sub-packages.
     */
    @Pointcut("execution(* com.demo.finance..*(..))")
    public void allMethods() {
    }

    /**
     * Logs the execution time of methods matched by the {@code allMethods()} pointcut. It also identifies
     * slow methods based on a predefined threshold and logs any exceptions that occur during execution.
     *
     * @param joinPoint the {@link ProceedingJoinPoint} representing the intercepted method
     * @return the result of the method execution
     * @throws Throwable if an exception occurs during method execution
     */
    @Around("allMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            if (executionTime > SLOW_METHOD_THRESHOLD_MS) {
                log.warning("[SLOW METHOD] " + methodName + " executed in " + executionTime
                        + " ms with arguments: " + Arrays.toString(args));
            } else {
                log.info("[METHOD] " + methodName + " executed in " + executionTime + " ms");
            }
            return result;
        } catch (Throwable t) {
            log.severe("[ERROR] Exception in method " + methodName + ": " + t.getMessage());
            throw t;
        }
    }
}