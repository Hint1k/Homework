package com.demo.finance.domain.model;

import com.demo.finance.domain.utils.GeneratedKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a financial goal for a user, including the goal name, target amount, saved amount,
 * duration, and the start time of the goal. This class allows for tracking and calculating the
 * progress toward achieving the goal.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Goal {

    @GeneratedKey
    private Long goalId;
    private Long userId;
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
        this.savedAmount = BigDecimal.ZERO;
        this.duration = duration;
        this.startTime = LocalDate.now();
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
        return duration == goal.duration && Objects.equals(goalId, goal.goalId) && Objects.equals(userId, goal.userId)
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
        return Objects.hash(goalId, userId, goalName, targetAmount, savedAmount, duration, startTime);
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