package org.testing.project_2_1;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class PlayerTimer {
    private long totalTime;
    private final long initialTotalTime;
    private final long increment; 
    private Timer timer;
    private TimerTask task;
    private boolean isRunning;
    private final Label timerLabel;

    public PlayerTimer(Label timerLabel, long initialTotalTimeMillis, long incrementMillis) {
        this.totalTime = 300_000;
        this.initialTotalTime = initialTotalTimeMillis;
        this.increment = incrementMillis;
        this.timerLabel = timerLabel;
        this.isRunning = false;
        updateLabel();
    }

    public void setTotalTime(long totalTimeMillis) {
        this.totalTime = totalTimeMillis;
        updateLabel();
    }

    public void startMove() {
        stopCountdown();      
        totalTime += increment; 
        updateLabel();         
    }

    public void startCountdown() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if (totalTime > 0) {
                    totalTime -= 1000;
                    Platform.runLater(() -> updateLabel());
                } else {
                    Platform.runLater(() -> {
                        timerLabel.setText("Time's up! Changing opponent's turn.");
                        stopCountdown(); 
                    });
                }
            }
        };
        isRunning = true;
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void stopCountdown() {
        if (isRunning) {
            task.cancel();
            timer.cancel();
            isRunning = false;
        }
    }

    private void updateLabel() {
        long minutes = (totalTime / 1000) / 60;
        long seconds = (totalTime / 1000) % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    public void reset() {
        stopCountdown();
        totalTime = initialTotalTime;
        isRunning = false;
        updateLabel();
    }
    public long getRemainingTimeInSeconds() {
        return totalTime / 1000;
    }
}
