package org.testing.project_2_1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class GameCountdown {
    private static final int INITIAL_TIME = 300;
    private int timeRemaining;
    private Timeline countdown;
    private Label countdownLabel;
    private Runnable onCountdownEnd; 

    public GameCountdown(Label countdownLabel, Runnable onCountdownEnd) {
        this.countdownLabel = countdownLabel;
        this.onCountdownEnd = onCountdownEnd;
        this.timeRemaining = INITIAL_TIME;
        updateCountdownLabel(); 
        start(); 
    }

    public void start() {
        timeRemaining = INITIAL_TIME;
        if (countdown != null) {
            countdown.stop(); 
        }

        countdown = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            updateCountdownLabel(); 

            if (timeRemaining <= 0) {
                countdown.stop();
                onCountdownEnd.run(); 
            }
        }));
        countdown.setCycleCount(Timeline.INDEFINITE);
        countdown.playFromStart();
    }

    public void reset() {
        timeRemaining = INITIAL_TIME;
        updateCountdownLabel();
        start();
    }

    private void updateCountdownLabel() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        countdownLabel.setText(String.format("Game ends in: %02d:%02d", minutes, seconds));
    }
}
