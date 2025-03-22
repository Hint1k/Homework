package com.demo.finance.aop;

import com.demo.finance.domain.dto.UserDto;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.JoinPoint;

import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Logger;

@Aspect
public class AuditAspect {

    private static final Logger log = Logger.getLogger(AuditAspect.class.getName());

    @Pointcut("execution(* com.demo.finance.out.service.*.*(..))")
    public void serviceMethods() {
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAudit(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        String userId = extractUserIdFromArgs(args).orElse("unknown");

        log.info("[AUDIT] User " + userId + " performed action: " + methodName + " with arguments: " +
                Arrays.toString(args) + ". Result: " + result);
    }

    /**
     * Safely extracts the userId from the arguments.
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