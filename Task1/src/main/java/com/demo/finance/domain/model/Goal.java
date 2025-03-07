package com.demo.finance.domain.model;

import java.time.LocalDate;
import java.util.Objects;

public class Goal {

    private final Long userId;
    private String goalName;
    private double targetAmount;
    private double savedAmount;
    private int duration;
    private LocalDate startTime;

    public Goal(Long userId, String goalName, double targetAmount, int duration) {
        this.userId = userId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.savedAmount = 0.0;
        this.duration = duration;
        this.startTime = LocalDate.now();
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

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getSavedAmount() {
        return savedAmount;
    }

    public void setSavedAmount(double savedAmount) {
        this.savedAmount = savedAmount;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDate getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDate startTime) {
        this.startTime = startTime;
    }

    public boolean isExpired() {
        LocalDate endDate = startTime.plusMonths(duration);
        return LocalDate.now().isAfter(endDate);
    }

    public double calculateProgress(double totalBalance) {
        return Math.min(totalBalance / targetAmount * 100, 100);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return Double.compare(targetAmount, goal.targetAmount) == 0
                && Double.compare(savedAmount, goal.savedAmount) == 0 && duration == goal.duration
                && Objects.equals(userId, goal.userId) && Objects.equals(goalName, goal.goalName)
                && Objects.equals(startTime, goal.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, goalName, targetAmount, savedAmount, duration, startTime);
    }

    @Override
    public String toString() {
        return "Goal: " + goalName + " [" + savedAmount + "/" + targetAmount + "]";
    }
}