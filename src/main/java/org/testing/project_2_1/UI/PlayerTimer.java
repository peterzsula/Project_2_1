package org.testing.project_2_1.UI;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 * A class that manages a countdown timer for a player in a game.
 * Supports incrementing time on legal moves, starting/stopping the countdown,
 * and resetting the timer. The remaining time is displayed in a formatted label.
 */
public class PlayerTimer {
    private long totalTime; // The current total time remaining in milliseconds
    private final long initialTotalTime; // The initial total time in milliseconds
    private final long increment; // The increment time added on each legal move in milliseconds
    private Timer timer; // Timer for scheduling countdown tasks
    private TimerTask task; // The task that decrements the timer
    private boolean isRunning; // Flag to indicate if the timer is running
    private final Label timerLabel; // Label to display the remaining time

    /**
     * Constructs a PlayerTimer with the specified initial total time and increment time.
     * 
     * @param timerLabel          The Label to display the timer.
     * @param initialTotalTimeMillis The initial total time for the timer in milliseconds.
     * @param incrementMillis        The time increment for legal moves in milliseconds.
     */
    public PlayerTimer(Label timerLabel, long initialTotalTimeMillis, long incrementMillis) {
        this.totalTime = 300_000; // Default to 5 minutes
        this.initialTotalTime = initialTotalTimeMillis;
        this.increment = incrementMillis;
        this.timerLabel = timerLabel;
        this.isRunning = false;
        updateLabel(); // Initialize the label with the formatted time
    }

    /**
     * Sets the total time for the timer and updates the label.
     * 
     * @param totalTimeMillis The new total time in milliseconds.
     */
    public void setTotalTime(long totalTimeMillis) {
        this.totalTime = totalTimeMillis;
        updateLabel();
    }

    /**
     * Starts a move, applying the increment to the timer if the move is legal.
     * Stops the countdown temporarily.
     * 
     * @param isLegalMove True if the move is legal; false otherwise.
     */
    public void startMove(boolean isLegalMove) {
        if (isLegalMove) {
            stopCountdown(); // Stop the countdown when a move starts
            totalTime += increment; // Add the increment to the total time
            updateLabel(); // Update the displayed time
        }
    }

    /**
     * Starts the countdown timer, decrementing the time every second.
     * If time runs out, the timer stops and the label indicates the turn change.
     */
    public void startCountdown() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if (totalTime > 0) {
                    totalTime -= 1000; // Decrease the total time by 1 second
                    Platform.runLater(() -> updateLabel());
                } else {
                    Platform.runLater(() -> {
                        timerLabel.setText("Time's up! Changing opponent's turn.");
                        stopCountdown(); // Stop the countdown when time is up
                    });
                }
            }
        };
        isRunning = true;
        timer.scheduleAtFixedRate(task, 0, 1000); // Schedule the task to run every second
    }

    /**
     * Stops the countdown timer.
     */
    public void stopCountdown() {
        if (isRunning) {
            task.cancel(); // Cancel the current task
            timer.cancel(); // Cancel the timer
            isRunning = false;
        }
    }

    /**
     * Updates the timer label with the remaining time in "MM:SS" format.
     */
    private void updateLabel() {
        long minutes = (totalTime / 1000) / 60; // Calculate remaining minutes
        long seconds = (totalTime / 1000) % 60; // Calculate remaining seconds
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    /**
     * Resets the timer to its initial state.
     * Stops the countdown and updates the label with the initial total time.
     */
    public void reset() {
        stopCountdown();
        totalTime = initialTotalTime; // Reset to the initial time
        isRunning = false;
        updateLabel(); // Update the label with the reset time
    }

    /**
     * Gets the remaining time in seconds.
     * 
     * @return The remaining time in seconds.
     */
    public long getRemainingTimeInSeconds() {
        return totalTime / 1000;
    }
}

