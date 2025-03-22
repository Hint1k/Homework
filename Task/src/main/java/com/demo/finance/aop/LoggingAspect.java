package com.demo.finance.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Arrays;
import java.util.logging.Logger;

@Aspect
public class LoggingAspect {

    private static final Logger log = Logger.getLogger(LoggingAspect.class.getName());
    private static final long SLOW_METHOD_THRESHOLD_MS = 500;

    @Pointcut("execution(* com.demo.finance..*(..))")
    public void allMethods() {
    }

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