package org.testing.project_2_1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class GameCountdown {
    private static final int INITIAL_TIME = 300; // 5 minuts (300 segons)
    private int timeRemaining;
    private Timeline countdown;
    private Label countdownLabel;
    private Runnable onCountdownEnd; // Funció a executar quan expira el compte enrere

    public GameCountdown(Label countdownLabel, Runnable onCountdownEnd) {
        this.countdownLabel = countdownLabel;
        this.onCountdownEnd = onCountdownEnd;
        this.timeRemaining = INITIAL_TIME;
        updateCountdownLabel(); // Configura el text inicial del compte enrere
        start(); // Comença el compte enrere automàticament
    }

    // Inicia el compte enrere
    public void start() {
        timeRemaining = INITIAL_TIME;
        if (countdown != null) {
            countdown.stop(); // Atura el compte enrere si ja estava en marxa
        }

        countdown = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            updateCountdownLabel(); // Actualitza el label amb el temps restant

            if (timeRemaining <= 0) {
                countdown.stop();
                onCountdownEnd.run(); // Executa l'acció quan el compte enrere arriba a zero
            }
        }));
        countdown.setCycleCount(Timeline.INDEFINITE);
        countdown.playFromStart();
    }

    // Reinicia el compte enrere
    public void reset() {
        timeRemaining = INITIAL_TIME;
        updateCountdownLabel();
        start();
    }

    // Actualitza el text del label amb el format MM:SS
    private void updateCountdownLabel() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        countdownLabel.setText(String.format("Game ends in: %02d:%02d", minutes, seconds));
    }
}
