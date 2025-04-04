package com.demo.finance.starter.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * The {@code LoggingAspect} class is an Aspect-Oriented Programming (AOP) aspect that provides logging
 * for method execution within the application. It logs method execution times, identifies slow methods,
 * and captures exceptions during method execution.
 */
@Aspect
@Component
@ConditionalOnBean(annotation = EnableLogging.class)
public class LoggingAspect {

    private final Logger log;
    private static final long SLOW_METHOD_THRESHOLD_MS = 500;

    /**
     * Constructs a new LoggingAspect with the default logger instance.
     * The logger will be initialized for the LoggingAspect class.
     */
    public LoggingAspect() {
        this.log = Logger.getLogger(LoggingAspect.class.getName());
    }

    /**
     * Constructs a new LoggingAspect with the specified logger instance.
     * This constructor is primarily useful for testing purposes.
     *
     * @param log the logger instance to be used by this aspect
     */
    public LoggingAspect(Logger log) {
        this.log = log;
    }

    /**
     * Defines a pointcut that matches all method executions within the com.demo.finance package
     * and its sub-packages. This pointcut is used by the advice methods in this aspect.
     */
    @Pointcut("execution(* com.demo.finance..*(..)) && !within(com.demo.finance.starter.logging..*)")
    public void allMethods() {
    }

    /**
     * Advice that logs method execution time around all methods matched by the allMethods pointcut.
     * Logs a warning if the execution time exceeds the slow method threshold, otherwise logs an info message.
     * The log includes the method name, execution time, and method arguments (for slow methods).
     *
     * @param joinPoint the proceeding join point representing the method execution
     * @return the result of the method execution
     * @throws Throwable if the intercepted method throws an exception
     */
    @Around("allMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;
        if (executionTime > SLOW_METHOD_THRESHOLD_MS) {
            log.warning("[SLOW METHOD] " + methodName + " executed in " + executionTime
                    + " ms with arguments: " + Arrays.toString(args));
        } else {
            log.info("[METHOD] " + methodName + " executed in " + executionTime + " ms");
        }
        return result;
    }

    /**
     * Advice that logs exceptions thrown by methods matched by the allMethods pointcut.
     * Logs a severe error message containing the method name and exception message.
     *
     * @param joinPoint the join point where the exception was thrown
     * @param ex        the exception that was thrown
     */
    @AfterThrowing(pointcut = "allMethods()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().toShortString();
        log.severe("[ERROR] Exception in method " + methodName + ": " + ex.getMessage());
    }
}