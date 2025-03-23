package com.demo.finance.aop;

import com.demo.finance.domain.dto.UserDto;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.JoinPoint;

import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * The {@code AuditAspect} class is an Aspect-Oriented Programming (AOP) aspect that provides audit logging
 * for service-layer methods. It logs user actions, including the method name, arguments, and results,
 * along with the user ID of the actor, if available.
 */
@Aspect
public class AuditAspect {

    private static final Logger log = Logger.getLogger(AuditAspect.class.getName());

    /**
     * Defines a pointcut that matches all methods in the {@code com.demo.finance.out.service} package.
     */
    @Pointcut("execution(* com.demo.finance.out.service.*.*(..))")
    public void serviceMethods() {
    }

    /**
     * Logs audit information after the execution of a service method. This includes the method name,
     * arguments, result, and the user ID of the actor, if available.
     *
     * @param joinPoint the {@link JoinPoint} representing the intercepted method
     * @param result    the return value of the intercepted method
     */
    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAudit(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String userId = extractUserIdFromArgs(args).orElse("unknown");
        log.info("[AUDIT] User " + userId + " performed action: " + methodName + " with arguments: " +
                Arrays.toString(args) + ". Result: " + result);
    }

    /**
     * Safely extracts the userId from the arguments passed to a service method.
     *
     * @param args the arguments passed to the method
     * @return an Optional containing the userId if found, or empty if not applicable
     */
    private Optional<String> extractUserIdFromArgs(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof UserDto userDto) {
                if (userDto.getUserId() != null) {
                    return Optional.of(userDto.getUserId().toString());
                }
            }
        }
        return Optional.empty();
    }
}