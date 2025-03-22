package com.demo.finance.out.service;

/**
 * {@code NotificationService} defines the contract for services that handle notifications
 * related to budgets and goals for users.
 */
public interface NotificationService {

     /**
      * Fetches the notification related to the user's budget.
      *
      * @param userId the ID of the user for whom the budget notification is being fetched
      * @return a string message containing the budget notification for the user
      */
     String fetchBudgetNotification(Long userId);

     /**
      * Fetches the notification related to the user's goal.
      *
      * @param userId the ID of the user for whom the goal notification is being fetched
      * @return a string message containing the goal notification for the user
      */
     String fetchGoalNotification(Long userId);
}