package com.demo.finance.starter.audit;

/**
 * AuditableUser is an interface that defines the contract for entities or objects representing users
 * in an auditable system. It provides a method to retrieve the unique identifier of a user,
 * enabling auditing functionality across modules. This interface ensures compatibility and
 * abstraction for user-related data used in audit logging, regardless of the specific user
 * implementation in different modules.
 */
public interface AuditableUser {

    Long getUserId();
}