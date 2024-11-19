package org.testing.project_2_1.UI;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class CapturedPiecesTracker {
    private int blackCaptured = 0;
    private int whiteCaptured = 0;

    public Label blackCapturedLabel;
    public Label whiteCapturedLabel;

    private int playerOneCapturedCount = 0;
    private int playerTwoCapturedCount = 0;

    private VBox blackWhiteCapturedBox;

    public CapturedPiecesTracker() {
        blackCapturedLabel = new Label(String.valueOf(blackCaptured));
        whiteCapturedLabel = new Label(String.valueOf(whiteCaptured));
        blackWhiteCapturedBox = new VBox(10);
        blackWhiteCapturedBox.getChildren().addAll(blackCapturedLabel, whiteCapturedLabel);
        setLabelStyles(blackCapturedLabel, whiteCapturedLabel);
        setTrackerStyles(blackWhiteCapturedBox);
    }

    private void setLabelStyles(Label... labels) {
        for (Label label : labels) {
            label.setStyle("-fx-font-size: 18px; -fx-text-fill: black; -fx-font-weight: bold;");
        }
    }

    private void setTrackerStyles(VBox box) {
        box.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(10), null)));
        box.setPadding(new Insets(10));
        box.setBorder(new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));
    }

    public void incrementBlackCaptured() {
        blackCaptured++;
        blackCapturedLabel.setText(String.valueOf(blackCaptured));
    }

    public void incrementWhiteCaptured() {
        whiteCaptured++;
        whiteCapturedLabel.setText(String.valueOf(whiteCaptured));
    }

    public void decrementWhiteCaptured() {
        whiteCaptured--;
        whiteCapturedLabel.setText(String.valueOf(whiteCaptured));
    }

    public void decrementBlackCaptured() {
        blackCaptured--;
        blackCapturedLabel.setText(String.valueOf(blackCaptured));
    }

    public VBox getBlackWhiteCapturedDisplay() {
        return blackWhiteCapturedBox;
    }

    // Increment methods for player-specific captured pieces
    public void capturePiece(String player) {
        if (player.equals("Player 1")) {
            playerOneCapturedCount++;
            blackCapturedLabel.setText(String.valueOf(playerOneCapturedCount));
        } else if (player.equals("Player 2")) {
            playerTwoCapturedCount++;
            whiteCapturedLabel.setText(String.valueOf(playerTwoCapturedCount));
        }
    }

    // Reset method to reset captured pieces counts and labels
    public void reset() {
        blackCaptured = 0;
        whiteCaptured = 0;
        playerOneCapturedCount = 0;
        playerTwoCapturedCount = 0;

        // Reset the labels to "0"
        blackCapturedLabel.setText(String.valueOf(blackCaptured));
        whiteCapturedLabel.setText(String.valueOf(whiteCaptured));
    }
}
