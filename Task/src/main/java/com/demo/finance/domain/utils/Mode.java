package com.demo.finance.domain.utils;

/**
 * The {@code Mode} enum defines various operational modes used to specify the context or type of validation
 * and processing in the application. Each mode corresponds to a specific operation or action, such as user
 * registration, creation, updating, deletion, authentication, or other domain-specific tasks.
 */
public enum Mode {
    /**
     * Represents the mode for user registration.
     */
    REGISTER,

    /**
     * Represents the mode for creating a generic entity.
     */
    CREATE,

    /**
     * Represents the mode for creating a goal.
     */
    GOAL_CREATE,

    /**
     * Represents the mode for creating a transaction.
     */
    TRANSACTION_CREATE,

    /**
     * Represents the mode for updating a generic entity.
     */
    UPDATE,

    /**
     * Represents the mode for updating a goal.
     */
    GOAL_UPDATE,

    /**
     * Represents the mode for updating a transaction.
     */
    TRANSACTION_UPDATE,

    /**
     * Represents the mode for deleting a generic entity.
     */
    DELETE,

    /**
     * Represents the mode for deleting a goal.
     */
    GOAL_DELETE,

    /**
     * Represents the mode for deleting a transaction.
     */
    TRANSACTION_DELETE,

    /**
     * Represents the mode for retrieving data.
     */
    GET,

    /**
     * Represents the mode for user authentication.
     */
    AUTHENTICATE,

    /**
     * Represents the mode for updating a user's role.
     */
    UPDATE_ROLE,

    /**
     * Represents the mode for blocking or unblocking a user.
     */
    BLOCK_UNBLOCK,

    /**
     * Represents the mode for generating reports.
     */
    REPORT,

    /**
     * Represents the mode for managing budgets.
     */
    BUDGET,

    /**
     * Represents the mode for paginated parameters.
     */
    PAGE,
}