package com.demo.finance.out.service;

/**
 * The {@code NotificationService} interface defines the contract for operations related to fetching notifications.
 * It provides methods to retrieve budget-related and goal-related notifications for users.
 */
public interface NotificationService {

     /**
      * Fetches a budget-related notification for a specific user.
      *
      * @param userId the unique identifier of the user for whom the budget notification is fetched
      * @return a {@link String} containing the budget notification message for the user
      */
     String fetchBudgetNotification(Long userId);

     /**
      * Fetches a goal-related notification for a specific user.
      *
      * @param userId the unique identifier of the user for whom the goal notification is fetched
      * @return a {@link String} containing the goal notification message for the user
      */
     String fetchGoalNotification(Long userId);
}