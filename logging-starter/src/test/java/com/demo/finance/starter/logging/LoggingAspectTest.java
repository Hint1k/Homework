package com.demo.finance.starter.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.logging.Logger;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @InjectMocks
    private LoggingAspect loggingAspect;
    @Mock
    private ProceedingJoinPoint joinPoint;
    @Mock
    private Logger logger;

    @Test
    @DisplayName("Should log normal execution time")
    void shouldLogNormalExecution() {
        try {
            Mockito.when(joinPoint.proceed()).thenReturn("result");
            Mockito.when(joinPoint.getSignature()).thenReturn(Mockito.mock(MethodSignature.class));

            loggingAspect.logExecutionTime(joinPoint);

            Mockito.verify(logger).info(ArgumentMatchers.matches("\\[METHOD\\].*executed in \\d+ ms"));
        } catch (Throwable t) {
            Assertions.fail("Unexpected exception thrown: " + t.getMessage());
        }
    }

    @Test
    @DisplayName("Should log warning when method execution exceeds slow threshold")
    void shouldLogSlowMethodWhenExceedingThreshold() {
        try {
            MethodSignature signature = Mockito.mock(MethodSignature.class);
            Mockito.when(signature.toShortString()).thenReturn("TestClass.testMethod()");

            Mockito.when(joinPoint.getSignature()).thenReturn(signature);
            Mockito.when(joinPoint.getArgs()).thenReturn(new Object[]{});
            Mockito.when(joinPoint.proceed()).thenAnswer(inv -> {
                Thread.sleep(600);
                return "result";
            });

            loggingAspect.logExecutionTime(joinPoint);

            Mockito.verify(logger).warning(ArgumentMatchers.contains("[SLOW METHOD]"));
            Mockito.verify(logger).warning(ArgumentMatchers.contains("TestClass.testMethod()"));
        } catch (Throwable t) {
            Assertions.fail("Unexpected exception thrown: " + t.getMessage());
        }
    }

    @Test
    @DisplayName("Should log info when method execution is under threshold")
    void shouldLogInfoForNormalExecution() {
        try {
            MethodSignature signature = Mockito.mock(MethodSignature.class);
            Mockito.when(signature.toShortString()).thenReturn("TestClass.fastMethod()");

            Mockito.when(joinPoint.getSignature()).thenReturn(signature);
            Mockito.when(joinPoint.getArgs()).thenReturn(new Object[]{});
            Mockito.when(joinPoint.proceed()).thenReturn("result");

            loggingAspect.logExecutionTime(joinPoint);

            Mockito.verify(logger).info(ArgumentMatchers.contains("TestClass.fastMethod()"));
            Mockito.verify(logger, Mockito.never()).warning(ArgumentMatchers.anyString());
        } catch (Throwable t) {
            Assertions.fail("Unexpected exception thrown: " + t.getMessage());
        }
    }

    @Test
    @DisplayName("Should log exceptions")
    void shouldLogExceptions() {
        try {
            JoinPoint joinPoint = Mockito.mock(JoinPoint.class);
            MethodSignature signature = Mockito.mock(MethodSignature.class);
            Mockito.when(joinPoint.getSignature()).thenReturn(signature);
            Mockito.when(signature.toShortString()).thenReturn("TestClass.method()");

            RuntimeException exception = new RuntimeException("test error");
            loggingAspect.logException(joinPoint, exception);

            Mockito.verify(logger).severe(ArgumentMatchers.contains(
                    "[ERROR] Exception in method TestClass.method(): test error"));
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }
}