package com.demo.finance.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void shouldLogNormalExecution() throws Throwable {
        when(joinPoint.proceed()).thenReturn("result");
        when(joinPoint.getSignature()).thenReturn(mock(MethodSignature.class));

        loggingAspect.logExecutionTime(joinPoint);

        verify(logger).info(matches("\\[METHOD\\].*executed in \\d+ ms"));
    }

    @Test
    @DisplayName("Should log warning when method execution exceeds slow threshold")
    void shouldLogSlowMethodWhenExceedingThreshold() throws Throwable {
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.toShortString()).thenReturn("TestClass.testMethod()");

        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenAnswer(inv -> {
            Thread.sleep(600);
            return "result";
        });

        loggingAspect.logExecutionTime(joinPoint);

        verify(logger).warning(contains("[SLOW METHOD]"));
        verify(logger).warning(contains("TestClass.testMethod()"));
    }

    @Test
    @DisplayName("Should log info when method execution is under threshold")
    void shouldLogInfoForNormalExecution() throws Throwable {
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.toShortString()).thenReturn("TestClass.fastMethod()");

        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenReturn("result");

        loggingAspect.logExecutionTime(joinPoint);

        verify(logger).info(contains("TestClass.fastMethod()"));
        verify(logger, never()).warning(anyString());
    }

    @Test
    @DisplayName("Should log exceptions")
    void shouldLogExceptions() throws Throwable {
        RuntimeException exception = new RuntimeException("test error");
        when(joinPoint.proceed()).thenThrow(exception);
        when(joinPoint.getSignature()).thenReturn(mock(MethodSignature.class));

        assertThrows(RuntimeException.class, () ->
                loggingAspect.logExecutionTime(joinPoint));

        verify(logger).severe(contains("[ERROR] Exception in method"));
    }
}