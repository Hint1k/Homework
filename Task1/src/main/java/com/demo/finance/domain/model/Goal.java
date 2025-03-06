package com.demo.finance.domain.model;

import java.util.Objects;

public class Goal {

    private final Long userId;
    private String goalName;
    private double targetAmount;
    private double savedAmount;

    public Goal(Long userId, String goalName, double targetAmount) {
        this.userId = userId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.savedAmount = 0.0;
    }

    public Long getUserId() {
        return userId;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public double getSavedAmount() {
        return savedAmount;
    }

    public void addSavings(double amount) {
        this.savedAmount += amount;
    }

    public boolean isAchieved() {
        return savedAmount >= targetAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return Double.compare(targetAmount, goal.targetAmount) == 0
                && Double.compare(savedAmount, goal.savedAmount) == 0 && Objects.equals(userId, goal.userId)
                && Objects.equals(goalName, goal.goalName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, goalName, targetAmount, savedAmount);
    }

    @Override
    public String toString() {
        return "Goal: " + goalName + " [" + savedAmount + "/" + targetAmount + "]";
    }
}