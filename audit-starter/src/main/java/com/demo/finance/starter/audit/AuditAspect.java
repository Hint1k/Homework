package com.demo.finance.starter.audit;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * The {@code AuditAspect} class is an Aspect-Oriented Programming (AOP) aspect that provides auditing
 * functionality for service method executions within the application. It automatically logs information
 * about method invocations, including the user who performed the action, method arguments, and results.
 *
 * <p>This aspect targets methods in service layers while excluding any classes within starter packages.
 */
@Aspect
@Component
public class AuditAspect {

    private static final Logger log = Logger.getLogger(AuditAspect.class.getName());

    /**
     * Defines a pointcut that matches method executions within service packages of the application.
     * The pointcut excludes any classes within the starter packages to avoid auditing infrastructure code.
     *
     * <p>The pointcut matches:
     * <ul>
     *   <li>All method executions within {@code com.demo.finance..service..} packages</li>
     *   <li>Excludes any classes within {@code com.demo.finance.starter..} packages</li>
     * </ul>
     */
    @Pointcut("execution(* com.demo.finance..service..*(..)) && !within(com.demo.finance.starter..*)")
    public void auditableMethods() {
    }

    /**
     * Advice that logs audit information after successful method execution.
     *
     * <p>This advice executes after methods matched by the {@link #auditableMethods()} pointcut
     * complete successfully. It logs:
     * <ul>
     *   <li>The user ID extracted from method arguments</li>
     *   <li>The method name that was executed</li>
     *   <li>The arguments passed to the method</li>
     *   <li>The result returned by the method</li>
     * </ul>
     *
     * @param joinPoint the join point representing the method execution
     * @param result    the value returned by the method execution
     */
    @AfterReturning(pointcut = "auditableMethods()", returning = "result")
    public void logAudit(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String userId = extractUserIdFromArgs(args);
        log.info(() -> String.format("[AUDIT] User %s performed action: %s with arguments: %s. Result: %s",
                userId, methodName, Arrays.toString(args), result));
    }

    /**
     * Extracts the user ID from method arguments by finding the first argument that implements
     * {@link AuditableUser} and has a non-null user ID.
     *
     * @param args the method arguments to inspect
     * @return the user ID as a string if found, "unknown" otherwise
     */
    private String extractUserIdFromArgs(Object[] args) {
        return Arrays.stream(args).filter(arg -> arg instanceof AuditableUser)
                .map(arg -> ((AuditableUser) arg).getUserId()).filter(Objects::nonNull).findFirst()
                .map(Object::toString).orElse("unknown");
    }
}