package com.demo.finance.domain.utils;

/**
 * Contains all Swagger/OpenAPI example JSON strings for request/response bodies.
 * Organized by API endpoint categories.
 */
public final class SwaggerExamples {

    /**
     * Example JSON strings for user-related endpoints.
     */
    public static final class User {

        /**
         * Example JSON for user registration request
         */
        public static final String REGISTRATION_REQUEST = """
                {
                  "name": "jay",
                  "email": "jay@demo.com",
                  "password": "123"
                }
                """;

        /**
         * Example JSON for successful user registration response
         */
        public static final String REGISTRATION_SUCCESS = """
                {
                  "data": {
                    "userId": 2,
                    "name": "jay",
                    "email": "jay@demo.com",
                    "password": null,
                    "blocked": false,
                    "role": { "name": "USER" },
                    "version": 1
                  },
                  "message": "User registered successfully",
                  "timestamp": "2025-04-01T12:00:00Z"
                }
                """;

        /**
         * Example JSON for invalid user registration response
         */
        public static final String INVALID_REGISTRATION_RESPONSE = """
                {
                    "error": "Validation error: Missing required field: name",
                    "timestamp": "2025-04-02T05:24:25.495714235Z"
                }
                """;

        /**
         * Example JSON for user authentication request
         */
        public static final String AUTHENTICATION_REQUEST = """
                {
                  "email": "jay@demo.com",
                  "password": "123"
                }
                """;

        /**
         * Example JSON for successful user authentication response
         */
        public static final String AUTHENTICATION_SUCCESS = """
                {
                  "data": {
                    "userId": 2,
                    "name": "jay",
                    "email": "jay@demo.com",
                    "password": null,
                    "blocked": false,
                    "role": { "name": "USER" },
                    "version": 1
                  },
                  "message": "User authenticated successfully",
                  "timestamp": "2025-04-01T12:00:00Z"
                }
                """;

        /**
         * Example JSON for invalid credentials response
         */
        public static final String INVALID_CREDENTIALS_RESPONSE = """
                {
                    "error": "Invalid credentials.",
                    "timestamp": "2025-04-02T05:19:29.720588708Z"
                }
                """;

        /**
         * Example JSON for user account update request
         */
        public static final String UPDATE_ACCOUNT_REQUEST = """
                {
                  "name": "jay2",
                  "email": "jay2@demo.com",
                  "password": "123",
                  "version": 1
                }
                """;

        /**
         * Example JSON for successful user account update response
         */
        public static final String UPDATE_ACCOUNT_SUCCESS = """
                        {
                            "data": {
                                "userId": 2,
                                "name": "jay2",
                                "email": "jay2@demo.com",
                                "password": null,
                                "blocked": false,
                                "role": {
                                    "name": "USER"
                                },
                                "version": 2
                            },
                            "message": "User updated successfully",
                            "timestamp": "2025-04-01T13:15:17.387825014Z"
                        }
                """;

        /**
         * Example JSON for missing account field response
         */
        public static final String MISSING_ACCOUNT_FIELD_RESPONSE = """
                {
                    "error": "Validation error: Missing required field: email",
                    "timestamp": "2025-04-02T05:33:18.928604221Z"
                }
                """;

        /**
         * Example JSON for successful user details retrieval response
         */
        public static final String GET_DETAILS_SUCCESS = """
                {
                    "data": {
                        "userId": 2,
                        "name": "jay",
                        "email": "jay@demo.com",
                        "password": null,
                        "blocked": false,
                        "role": {
                            "name": "USER"
                        },
                        "version": 1
                    },
                    "message": "Authenticated user details",
                    "timestamp": "2025-04-01T13:09:56.074426539Z"
                }
                """;

        /**
         * Example JSON for successful user account deletion response
         */
        public static final String DELETE_ACCOUNT_SUCCESS = """
                {
                    "data": {
                        "email": "jay@demo.com"
                    },
                    "message": "Account deleted successfully",
                    "timestamp": "2025-04-01T13:23:27.437716025Z"
                }
                """;
    }

    /**
     * Example JSON strings for admin-related endpoints.
     */
    public static final class Admin {

        /**
         * Example JSON for successful users list retrieval response
         */
        public static final String GET_USERS_SUCCESS = """
                {
                    "metadata": {
                        "totalItems": 2,
                        "totalPages": 1,
                        "pageSize": 100,
                        "currentPage": 1
                    },
                    "data": [
                        {
                            "userId": 1,
                            "name": "admin",
                            "email": "admin@demo.com",
                            "password": null,
                            "blocked": false,
                            "role": {
                                "name": "ADMIN"
                            },
                            "version": 1
                        },
                        {
                            "userId": 2,
                            "name": "jay",
                            "email": "jay@demo.com",
                            "password": null,
                            "blocked": false,
                            "role": {
                                "name": "USER"
                            },
                            "version": 1
                        }
                    ]
                }
                """;

        /**
         * Example JSON for successful user details retrieval response
         */
        public static final String GET_USER_SUCCESS = """
                {
                    "data": {
                        "userId": 2,
                        "name": "jay",
                        "email": "jay@demo.com",
                        "password": null,
                        "blocked": false,
                        "role": {
                            "name": "USER"
                        },
                        "version": 1
                    },
                    "message": "User details",
                    "timestamp": "2025-04-01T14:14:52.883529502Z"
                }
                """;

        /**
         * Example JSON for user not found response
         */
        public static final String USER_NOT_FOUND_RESPONSE = """
                {
                    "error": "User not found.",
                    "timestamp": "2025-04-02T05:36:57.875290445Z"
                }
                """;

        /**
         * Example JSON for successful user transactions retrieval response
         */
        public static final String GET_USER_TRANSACTIONS_SUCCESS = """
                {
                     "metadata": {
                         "totalItems": 1,
                         "user_id": 2,
                         "totalPages": 1,
                         "pageSize": 10,
                         "currentPage": 1
                     },
                     "data": [
                         {
                             "transactionId": 2,
                             "userId": 2,
                             "amount": 250.00,
                             "category": "Food",
                             "date": "2025-4-30",
                             "description": "Grocery shopping",
                             "type": "EXPENSE"
                         }
                     ]
                }
                """;

        /**
         * Example JSON for invalid size parameter response
         */
        public static final String INVALID_SIZE_RESPONSE = """
                {
                    "error": "Validation error: Size cannot exceed 100.",
                    "timestamp": "2025-04-02T05:38:30.701197062Z"
                }
                """;

        /**
         * Example JSON for invalid page parameter response
         */
        public static final String INVALID_PAGE_RESPONSE = """
                {
                    "error": "Invalid pagination parameters: Validation error: Page must be positive integer: -7",
                    "timestamp": "2025-04-02T06:40:59.543748926Z"
                }
                """;

        /**
         * Example JSON for user role update request
         */
        public static final String UPDATE_ROLE_REQUEST = """
                {
                  "role": "USER",
                  "version": 1
                }
                """;

        /**
         * Example JSON for successful user role update response
         */
        public static final String UPDATE_ROLE_SUCCESS = """
                {
                    "data": {
                        "userId": 2,
                        "name": null,
                        "email": null,
                        "password": null,
                        "blocked": false,
                        "role": {
                            "name": "USER"
                        },
                        "version": null
                    },
                    "message": "User role updated successfully",
                    "timestamp": "2025-04-01T14:15:37.652176849Z"
                }
                """;

        /**
         * Example JSON for default admin role change restriction response
         */
        public static final String UPDATE_DEFAULT_ADMIN_RESPONSE = """
                {
                    "error": "Default Admin role cannot be changed",
                    "timestamp": "2025-04-02T08:28:06.355623207Z"
                }
                """;

        /**
         * Example JSON for user block/unblock request
         */
        public static final String BLOCK_USER_REQUEST = """
                {
                  "blocked": false,
                  "version": 1
                }
                """;

        /**
         * Example JSON for successful user block/unblock response
         */
        public static final String BLOCK_USER_SUCCESS = """
                {
                    "data": {
                        "userId": 2,
                        "name": null,
                        "email": null,
                        "password": null,
                        "blocked": false,
                        "role": null,
                        "version": null
                    },
                    "message": "User blocked/unblocked status changed successfully",
                    "timestamp": "2025-04-01T14:16:16.009018082Z"
                }
                """;

        /**
         * Example JSON for default admin block restriction response
         */
        public static final String BLOCK_DEFAULT_ADMIN_RESPONSE = """
                {
                    "error": "Default Admin cannot be blocked or unblocked",
                    "timestamp": "2025-04-02T08:28:09.726678036Z"
                }
                """;

        /**
         * Example JSON for successful user deletion response
         */
        public static final String DELETE_USER_SUCCESS = """
                {
                    "data": {
                        "userId": 2
                    },
                    "message": "Account deleted successfully",
                    "timestamp": "2025-04-01T14:52:29.386947135Z"
                }
                """;

        /**
         * Example JSON for invalid user ID format response
         */
        public static final String INVALID_USER_ID_RESPONSE = """
                {
                    "error": "Invalid user ID format. User ID must be a positive integer.",
                    "timestamp": "2025-04-02T06:34:05.728005962Z"
                }
                """;
    }

    /**
     * Example JSON strings for transaction-related endpoints.
     */
    public static final class Transaction {

        /**
         * Example JSON for successful transaction retrieval response
         */
        public static final String GET_TRANSACTION_SUCCESS = """
                {
                    "data": {
                        "transactionId": 1,
                        "userId": 2,
                        "amount": 250.00,
                        "category": "Food",
                        "date": "2025-4-30",
                        "description": "Grocery shopping",
                        "type": "EXPENSE"
                    },
                    "message": "Transaction found successfully",
                    "timestamp": "2025-04-01T14:20:51.419891845Z"
                }
                """;

        /**
         * Example JSON for transaction not found response
         */
        public static final String TRANSACTION_NOT_FOUND_RESPONSE = """
                {
                    "error": "Transaction not found or you are not the owner of the transaction.",
                    "timestamp": "2025-04-02T06:59:22.129736191Z"
                }
                """;

        /**
         * Example JSON for successful transactions list retrieval response
         */
        public static final String GET_TRANSACTIONS_SUCCESS = """
                {
                    "metadata": {
                        "totalItems": 1,
                        "user_id": 2,
                        "totalPages": 1,
                        "pageSize": 10,
                        "currentPage": 1
                    },
                    "data": [
                        {
                            "transactionId": 1,
                            "userId": 2,
                            "amount": 250.00,
                            "category": "Food",
                            "date": "2025-4-30",
                            "description": "Grocery shopping",
                            "type": "EXPENSE"
                        }
                    ]
                }
                """;

        /**
         * Example JSON for transaction creation request
         */
        public static final String CREATE_TRANSACTION_REQUEST = """
                {
                  "amount": "500",
                  "category": "Food",
                  "date": "2025-03-23",
                  "description": "Grocery shopping",
                  "type": "EXPENSE"
                }
                """;

        /**
         * Example JSON for invalid transaction type response
         */
        public static final String INVALID_TRANSACTION_TYPE_RESPONSE = """
                {
                    "error": "Validation error: Type must be either INCOME or EXPENSE.",
                    "timestamp": "2025-04-02T07:02:38.101826557Z"
                }
                """;

        /**
         * Example JSON for successful transaction creation response
         */
        public static final String CREATE_TRANSACTION_SUCCESS = """
                {
                      "data": {
                          "transactionId": 1,
                          "userId": 2,
                          "amount": 500.00,
                          "category": "Food",
                          "date": "2025-4-30",
                          "description": "Grocery shopping",
                          "type": "EXPENSE"
                      },
                      "message": "Transaction created successfully",
                      "timestamp": "2025-04-01T14:18:41.374683647Z"
                 }
                """;

        /**
         * Example JSON for transaction update request
         */
        public static final String UPDATE_TRANSACTION_REQUEST = """
                {
                  "amount": "750",
                  "category": "Luxury",
                  "description": "Gold shopping"
                }
                """;

        /**
         * Example JSON for missing transaction field response
         */
        public static final String MISSING_TRANSACTION_FIELD_RESPONSE = """
                {
                    "error": "Validation error: Missing required field: amount",
                    "timestamp": "2025-04-02T07:06:27.071108886Z"
                }
                """;

        /**
         * Example JSON for successful transaction update response
         */
        public static final String UPDATE_TRANSACTION_SUCCESS = """
                {
                     "data": {
                         "transactionId": 1,
                         "userId": 2,
                         "amount": 750.00,
                         "category": "Luxury",
                         "date": "2025-5-30",
                         "description": "Gold shopping",
                         "type": "EXPENSE"
                     },
                     "message": "Transaction updated successfully",
                     "timestamp": "2025-04-01T14:21:23.526079462Z"
                 }
                """;

        /**
         * Example JSON for successful transaction deletion response
         */
        public static final String DELETE_TRANSACTION_SUCCESS = """
                {
                    "data": {
                        "transactionId": 1
                    },
                    "message": "Transaction deleted successfully",
                    "timestamp": "2025-04-01T14:46:47.064719554Z"
                }
                """;

        /**
         * Example JSON for invalid transaction ID format response
         */
        public static final String INVALID_TRANSACTION_ID_RESPONSE = """
                {
                    "error": "Invalid numeric format for id: a",
                    "timestamp": "2025-04-02T07:25:03.619473835Z"
                }
                """;
    }

    /**
     * Example JSON strings for goal-related endpoints.
     */
    public static final class Goal {

        /**
         * Example JSON for successful goal retrieval response
         */
        public static final String GET_GOAL_SUCCESS = """
                {
                    "data": {
                        "goalId": 1,
                        "userId": 2,
                        "goalName": "My Goal",
                        "targetAmount": 1500.00,
                        "savedAmount": 0.00,
                        "duration": 3,
                        "startTime": "2025-4-30"
                    },
                    "message": "Goal found successfully",
                    "timestamp": "2025-04-01T14:35:09.321050058Z"
                }
                """;

        /**
         * Example JSON for invalid JSON format response
         */
        public static final String INVALID_JSON_RESPONSE = """
                {
                    "error": "Invalid JSON format",
                    "details": "Malformed request body"
                }
                """;

        /**
         * Example JSON for successful goals list retrieval response
         */
        public static final String GET_GOALS_SUCCESS = """
                {
                    "metadata": {
                        "totalItems": 1,
                        "user_id": 2,
                        "totalPages": 1,
                        "pageSize": 10,
                        "currentPage": 1
                    },
                    "data": [
                        {
                            "goalId": 1,
                            "userId": 2,
                            "goalName": "My Goal",
                            "targetAmount": 1500.00,
                            "savedAmount": 0.00,
                            "duration": 3,
                            "startTime": "2025-4-30"
                        }
                    ]
                }
                """;

        /**
         * Example JSON for goal not found response
         */
        public static final String GOAL_NOT_FOUND_RESPONSE = """
                {
                    "error": "Goal not found or you are not the owner of the goal.",
                    "timestamp": "2025-04-02T07:54:23.103440768Z"
                }
                """;

        /**
         * Example JSON for create goal request
         */
        public static final String CREATE_GOAL_REQUEST = """
                {
                    "goalName": "My Goal",
                    "targetAmount": "1500",
                    "duration": "3",
                    "startTime": "2025-01-01"
                }
                """;

        /**
         * Example JSON for successful goal creation response
         */
        public static final String CREATE_GOAL_SUCCESS = """
                {
                    "data": {
                        "goalId": 1,
                        "userId": 2,
                        "goalName": "My Goal",
                        "targetAmount": 1500.00,
                        "savedAmount": 0.00,
                        "duration": 3,
                        "startTime":  "2025-1-1"
                    },
                    "message": "Goal created successfully",
                    "timestamp": "2025-04-01T14:29:30.345555430Z"
                }
                """;

        /**
         * Example JSON for update goal request
         */
        public static final String UPDATE_GOAL_REQUEST = """
                {
                    "goalName": "My Goal 2",
                    "targetAmount": "1500",
                    "duration": "3"
                }
                """;

        /**
         * Example JSON for successful goal update response
         */
        public static final String UPDATE_GOAL_SUCCESS = """
                {
                    "data": {
                        "goalId": 1,
                        "userId": 2,
                        "goalName": "My Goal 2",
                        "targetAmount": 1500.00,
                        "savedAmount": 0.00,
                        "duration": 3,
                        "startTime": "2025-4-30"
                    },
                    "message": "Goal updated successfully",
                    "timestamp": "2025-04-01T14:31:24.355103445Z"
                }
                """;

        /**
         * Example JSON for missing goal field response
         */
        public static final String MISSING_GOAL_FIELD_RESPONSE = """
                {
                    "error": "Validation error: Missing required field: targetAmount",
                    "timestamp": "2025-04-02T07:56:25.737010554Z"
                }
                """;

        /**
         * Example JSON for successful goal deletion response
         */
        public static final String DELETE_GOAL_SUCCESS = """
                {
                    "data": {
                        "goalId": 1
                    },
                    "message": "Goal deleted successfully",
                    "timestamp": "2025-04-01T14:47:34.849803678Z"
                }
                """;

        /**
         * Example JSON for invalid goal ID response
         */
        public static final String INVALID_GOAL_ID_RESPONSE = """
                {
                    "error": "Invalid numeric format for id: a",
                    "timestamp": "2025-04-02T08:02:27.821858249Z"
                }
                """;
    }

    /**
     * Example JSON strings for budget-related endpoints.
     */
    public static final class Budget {

        /**
         * Example JSON for successful budget retrieval response
         */
        public static final String GET_BUDGET_SUCCESS = """
                {
                    "data": {
                        "budgetData": {
                            "currentExpenses": 0,
                            "monthlyLimit": 1500.00
                        },
                        "formattedBudget": "Budget: 0.00/1500.00"
                    },
                    "message": "Budget retrieved successfully",
                    "timestamp": "2025-04-01T14:37:54.830615602Z"
                }
                """;

        /**
         * Example JSON for create budget request
         */
        public static final String CREATE_BUDGET_REQUEST = """
                {
                  "monthlyLimit": "1500"
                }
                """;

        /**
         * Example JSON for missing budget field response
         */
        public static final String MISSING_BUDGET_FIELD_RESPONSE = """
                {
                    "error": "Validation error: Missing required field: monthlyLimit",
                    "timestamp": "2025-04-02T08:06:42.557539762Z"
                }
                """;

        /**
         * Example JSON for successful budget creation response
         */
        public static final String CREATE_BUDGET_SUCCESS = """
                {
                    "data": {
                        "budgetId": 1,
                        "userId": 2,
                        "monthlyLimit": 1500.00,
                        "currentExpenses": 0.00
                    },
                    "message": "Budget generated successfully",
                    "timestamp": "2025-04-01T14:37:30.669422303Z"
                }
                """;
    }

    /**
     * Example JSON strings for report-related endpoints.
     */
    public static final class Report {

        /**
         * Example JSON for successful report retrieval response
         */
        public static final String GET_REPORT_SUCCESS = """
                {
                    "data": {
                        "reportId": 1,
                        "userId": 2,
                        "totalIncome": 0,
                        "totalExpense": 250.00,
                        "balance": -250.00
                    },
                    "message": "General report generated successfully",
                    "timestamp": "2025-04-01T14:43:37.618032815Z"
                }
                """;

        /**
         * Example JSON for expense by category request
         */
        public static final String EXPENSES_BY_CATEGORY_REQUEST = """
                {
                  "fromDate": "2025-01-01",
                  "toDate": "2025-06-30"
                }
                """;

        /**
         * Example JSON for successful expense by category response
         */
        public static final String EXPENSES_BY_CATEGORY_SUCCESS = """
                {
                    "data": {
                        "Food": 250.00
                    },
                    "message": "Expenses generated successfully",
                    "timestamp": "2025-04-01T14:48:08.888582184Z"
                }
                """;

        /**
         * Example JSON for report by date request
         */
        public static final String REPORT_BY_DATE_REQUEST = """
                {
                  "fromDate": "2025-01-01",
                  "toDate": "2025-06-30"
                }
                """;

        /**
         * Example JSON for successful report by date response
         */
        public static final String REPORT_BY_DATE_SUCCESS = """
                {
                    "data": {
                        "reportId": 1,
                        "userId": 2,
                        "totalIncome": 0,
                        "totalExpense": 250.00,
                        "balance": -250.00
                    },
                    "message": "Report by dates generated successfully",
                    "timestamp": "2025-04-01T14:42:23.221611821Z"
                }
                """;

        /**
         * Example JSON for missing report field response
         */
        public static final String MISSING_REPORT_FIELD_RESPONSE = """
                {
                    "error": "Validation error: Missing required field: fromDate",
                    "timestamp": "2025-04-02T08:10:54.773234500Z"
                }
                """;
    }

    /**
     * Example JSON strings for notification-related endpoints.
     */
    public static final class Notification {

        /**
         * Example JSON for successful budget notification
         */
        public static final String GET_BUDGET_NOTIFICATIONS_SUCCESS = """
                {
                    "message": "✅ Budget is under control. Remaining budget: 1500.00",
                    "timestamp": "2025-04-01T14:40:14.576916577Z"
                }
                """;

        /**
         * Example JSON for successful goal notification
         */
        public static final String GET_GOAL_NOTIFICATIONS_SUCCESS = """
                {
                    "message": "⏳ Goal '1' progress: 0.00%",
                    "timestamp": "2025-04-01T14:40:54.035535022Z"
                }
                """;
    }
}