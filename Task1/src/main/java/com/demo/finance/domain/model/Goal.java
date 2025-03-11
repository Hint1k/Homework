package com.demo.finance.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a financial goal for a user, including the goal name, target amount, saved amount,
 * duration, and the start time of the goal. This class allows for tracking and calculating the
 * progress toward achieving the goal.
 */
public class Goal {

    private final Long userId;
    private String goalName;
    private BigDecimal targetAmount;
    private BigDecimal savedAmount;
    private int duration;
    private LocalDate startTime;

    /**
     * Constructs a new Goal for a user with a specified goal name, target amount, and duration.
     * The saved amount is initialized to 0.0 and the start time is set to the current date.
     *
     * @param userId       The ID of the user this goal is associated with.
     * @param goalName     The name of the goal.
     * @param targetAmount The target amount to be saved for the goal.
     * @param duration     The duration (in months) to achieve the goal.
     */
    public Goal(Long userId, String goalName, BigDecimal targetAmount, int duration) {
        this.userId = userId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.savedAmount = new BigDecimal(0);
        this.duration = duration;
        this.startTime = LocalDate.now();
    }

    /**
     * Gets the ID of the user associated with this goal.
     *
     * @return The user ID.
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Gets the name of the goal.
     *
     * @return The goal name.
     */
    public String getGoalName() {
        return goalName;
    }

    /**
     * Sets a new name for the goal.
     *
     * @param goalName The new goal name.
     */
    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    /**
     * Gets the target amount for the goal.
     *
     * @return The target amount.
     */
    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    /**
     * Sets a new target amount for the goal.
     *
     * @param targetAmount The new target amount.
     */
    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    /**
     * Gets the amount already saved towards the goal.
     *
     * @return The saved amount.
     */
    public BigDecimal getSavedAmount() {
        return savedAmount;
    }

    /**
     * Sets the amount saved towards the goal.
     *
     * @param savedAmount The new saved amount.
     */
    public void setSavedAmount(BigDecimal savedAmount) {
        this.savedAmount = savedAmount;
    }

    /**
     * Gets the duration (in months) for achieving the goal.
     *
     * @return The duration in months.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Sets a new duration (in months) for achieving the goal.
     *
     * @param duration The new duration in months.
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Gets the start time of the goal.
     *
     * @return The start time of the goal.
     */
    public LocalDate getStartTime() {
        return startTime;
    }

    /**
     * Sets a new start time for the goal.
     *
     * @param startTime The new start time.
     */
    public void setStartTime(LocalDate startTime) {
        this.startTime = startTime;
    }

    /**
     * Checks if the goal has expired based on its duration and the current date.
     *
     * @return {@code true} if the goal has expired, otherwise {@code false}.
     */
    public boolean isExpired() {
        LocalDate endDate = startTime.plusMonths(duration);
        return LocalDate.now().isAfter(endDate);
    }

    /**
     * Calculates the progress of the goal as a percentage of the target amount.
     *
     * @param totalBalance The total balance of the savings account.
     * @return The progress of the goal as a percentage (between 0 and 100).
     */
    public BigDecimal calculateProgress(BigDecimal totalBalance) {
        if (totalBalance == null || targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal progress = totalBalance.divide(targetAmount, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP));

        return progress.min(BigDecimal.valueOf(100));
    }

    /**
     * Compares this goal to another object for equality. Two goals are considered equal if
     * their user ID, goal name, target amount, saved amount, duration, and start time are the same.
     *
     * @param o The object to compare to.
     * @return {@code true} if this goal is equal to the provided object, otherwise {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return duration == goal.duration && Objects.equals(userId, goal.userId)
                && Objects.equals(goalName, goal.goalName) && Objects.equals(targetAmount, goal.targetAmount)
                && Objects.equals(savedAmount, goal.savedAmount) && Objects.equals(startTime, goal.startTime);
    }

    /**
     * Generates a hash code for this goal. The hash code is based on the user ID, goal name, target amount,
     * saved amount, duration, and start time.
     *
     * @return A hash code for this goal.
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, goalName, targetAmount, savedAmount, duration, startTime);
    }

    /**
     * Returns a string representation of the goal, including the goal name and the saved/target amounts.
     *
     * @return A string representation of the goal.
     */
    @Override
    public String toString() {
        return "Goal: " + goalName + " [" + savedAmount + "/" + targetAmount + "]";
    }
}