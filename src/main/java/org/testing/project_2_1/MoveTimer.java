package org.testing.project_2_1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class MoveTimer {
    private static final int INITIAL_TIME = 30; // Temps inicial per al torn, en segons
    private int timeRemaining;
    private Timeline timer;
    private Label timerLabel;
    private Runnable onTimeOut;

    public MoveTimer(Label timerLabel, Runnable onTimeOut) {
        this.timerLabel = timerLabel;
        this.onTimeOut = onTimeOut;
        this.timeRemaining = INITIAL_TIME;
        timerLabel.setText("Opponent's turn ends in: " + timeRemaining + "s"); // Text inicial
        setTimerStyle();
        start();
    }

    public void start() {
        timeRemaining = INITIAL_TIME;
        if (timer != null) {
            timer.stop();
        }
        
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            timerLabel.setText("Opponent's turn ends in: " + timeRemaining + "s");

            if (timeRemaining <= 10) {
                setLowTimeStyle();
            } else {
                setTimerStyle();
            }

            if (timeRemaining <= 0) {
                timer.stop();
                showTurnChangeMessage();
                onTimeOut.run();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.playFromStart();
    }

    public void reset() {
        timeRemaining = INITIAL_TIME;
        timerLabel.setText("Opponent's turn ends in: " + timeRemaining + "s");
        setTimerStyle();
        start();
    }

    // Mostra un missatge temporal per indicar el canvi de torn
    public void showTurnChangeMessage() {
        timerLabel.setText("Changing opponent's turn");
        setChangingTurnStyle();

        // Pausa de 2 segons abans de reiniciar el temporitzador
        Timeline pause = new Timeline(new KeyFrame(Duration.seconds(2), e -> reset()));
        pause.setCycleCount(1);
        pause.play();
    }

    private void setTimerStyle() {
        timerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #d3d3d3; " +
                "-fx-padding: 10px 20px; -fx-border-color: #b0b0b0; -fx-border-width: 2px; " +
                "-fx-text-fill: darkgreen; -fx-font-family: 'Arial'; -fx-border-radius: 10px; -fx-background-radius: 10px;");
    }

    private void setLowTimeStyle() {
        timerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #d3d3d3; " +
                "-fx-padding: 10px 20px; -fx-border-color: #b0b0b0; -fx-border-width: 2px; " +
                "-fx-text-fill: red; -fx-font-family: 'Arial'; -fx-border-radius: 10px; -fx-background-radius: 10px;");
    }

    private void setChangingTurnStyle() {
        timerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #d3d3d3; " +
                "-fx-padding: 10px 20px; -fx-border-color: #b0b0b0; -fx-border-width: 2px; " +
                "-fx-text-fill: blue; -fx-font-family: 'Arial'; -fx-border-radius: 10px; -fx-background-radius: 10px;");
    }
}
