package com.demo.finance.starter.audit;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

@Aspect
@Component
public class AuditAspect {

    private static final Logger log = Logger.getLogger(AuditAspect.class.getName());

    @Pointcut("execution(* com.demo.finance..service..*(..)) && !within(com.demo.finance.starter..*)")
    public void auditableMethods() {
    }

    @AfterReturning(pointcut = "auditableMethods()", returning = "result")
    public void logAudit(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String userId = extractUserIdFromArgs(args);
        log.info(() -> String.format("[AUDIT] User %s performed action: %s with arguments: %s. Result: %s",
                userId, methodName, Arrays.toString(args), result));
    }

    private String extractUserIdFromArgs(Object[] args) {
        return Arrays.stream(args).filter(arg -> arg instanceof AuditableUser)
                .map(arg -> ((AuditableUser) arg).getUserId()).filter(Objects::nonNull).findFirst()
                .map(Object::toString).orElse("unknown");
    }
}