package com.demo.finance.out.service;

public interface NotificationService {

     String fetchBudgetNotification(Long userId);

     String fetchGoalNotification(Long userId);
}