package com.demo.finance.starter.logging;

import nl.altindag.log.LogCaptor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @InjectMocks
    private LoggingAspect loggingAspect;
    @Mock
    private ProceedingJoinPoint joinPoint;
    @Mock
    private JoinPoint joinPointForException;

    @Test
    @DisplayName("Should log normal execution time")
    void shouldLogNormalExecution() throws Throwable {
        try (LogCaptor logCaptor = LogCaptor.forClass(LoggingAspect.class)) {
            when(joinPoint.proceed()).thenReturn("result");
            when(joinPoint.getSignature()).thenReturn(mock(MethodSignature.class));

            loggingAspect.logExecutionTime(joinPoint);

            assertThat(logCaptor.getInfoLogs())
                    .anyMatch(log -> log.matches("\\[METHOD].*executed in \\d+ ms"));
        }
    }

    @Test
    @DisplayName("Should log warning when method execution exceeds slow threshold")
    void shouldLogSlowMethodWhenExceedingThreshold() throws Throwable {
        try (LogCaptor logCaptor = LogCaptor.forClass(LoggingAspect.class)) {
            MethodSignature signature = mock(MethodSignature.class);
            when(signature.toShortString()).thenReturn("TestClass.testMethod()");

            when(joinPoint.getSignature()).thenReturn(signature);
            when(joinPoint.getArgs()).thenReturn(new Object[]{});
            when(joinPoint.proceed()).thenAnswer(inv -> {
                Thread.sleep(600);
                return "result";
            });

            loggingAspect.logExecutionTime(joinPoint);

            assertThat(logCaptor.getWarnLogs())
                    .anyMatch(log -> log.contains("[SLOW METHOD] TestClass.testMethod()"));
        }
    }

    @Test
    @DisplayName("Should log info when method execution is under threshold")
    void shouldLogInfoForNormalExecution() throws Throwable {
        try (LogCaptor logCaptor = LogCaptor.forClass(LoggingAspect.class)) {
            MethodSignature signature = mock(MethodSignature.class);
            when(signature.toShortString()).thenReturn("TestClass.fastMethod()");

            when(joinPoint.getSignature()).thenReturn(signature);
            when(joinPoint.getArgs()).thenReturn(new Object[]{});
            when(joinPoint.proceed()).thenReturn("result");

            loggingAspect.logExecutionTime(joinPoint);

            assertThat(logCaptor.getInfoLogs()).anyMatch(log -> log.contains("TestClass.fastMethod()"));
            assertThat(logCaptor.getWarnLogs()).isEmpty();
        }
    }

    @Test
    @DisplayName("Should log exceptions")
    void shouldLogExceptions() {
        try (LogCaptor logCaptor = LogCaptor.forClass(LoggingAspect.class)) {
            MethodSignature signature = mock(MethodSignature.class);
            when(joinPointForException.getSignature()).thenReturn(signature);
            when(signature.toShortString()).thenReturn("TestClass.method()");

            RuntimeException exception = new RuntimeException("test error");
            loggingAspect.logException(joinPointForException, exception);

            assertThat(logCaptor.getErrorLogs())
                    .anyMatch(log -> log.contains("[ERROR] Exception in method TestClass.method(): test error"));
        }
    }
}