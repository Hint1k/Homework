package com.demo.finance.starter.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * The {@code LoggingAspect} class is an Aspect-Oriented Programming (AOP) aspect that provides logging
 * for method execution within the application. It logs method execution times, identifies slow methods,
 * and captures exceptions during method execution.
 */
@Aspect
@Component
@ConditionalOnBean(annotation = EnableLogging.class)
@Slf4j
public class LoggingAspect {

    private final long slowThreshold;

    public LoggingAspect(@Value("${logging-aspect.slow-method-threshold-ms:500}") long slowThreshold) {
        this.slowThreshold = slowThreshold;
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
        if (executionTime > slowThreshold) {
            log.warn("[SLOW METHOD] {} executed in {} ms with arguments: {}",
                    methodName, executionTime, Arrays.toString(args));
        } else {
            log.info("[METHOD] {} executed in {} ms", methodName, executionTime);
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
        log.error("[ERROR] Exception in method {}: {}", methodName, ex.getMessage(), ex);
    }
}