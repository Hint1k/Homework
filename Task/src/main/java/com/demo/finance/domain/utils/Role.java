package com.demo.finance.domain.utils;

/**
 * The {@code Role} enum represents the different roles that a user can have within the application.
 * This enum is used to define and manage user access levels and permissions, ensuring proper
 * authorization and access control in the system.
 *
 * <p>The roles defined in this enum are:
 * <ul>
 *   <li>{@link #USER}: Represents a standard user with basic access privileges.</li>
 *   <li>{@link #ADMIN}: Represents an administrator with elevated privileges for managing the application.</li>
 * </ul>
 *
 * <p>This enum is designed to be extensible, allowing additional roles to be added in the future if needed.
 */
public enum Role {

    /**
     * Represents a standard user role.
     * <p>Users with this role have basic access to the application's features. They can perform actions
     * such as viewing content, submitting requests, or interacting with the system within the limits
     * defined by the application's business logic.
     */
    USER,

    /**
     * Represents an administrator role.
     * <p>Users with this role have elevated privileges and are responsible for managing the application.
     * Administrators can perform tasks such as creating, updating, or deleting resources, managing other
     * users, and configuring system settings.
     */
    ADMIN
}