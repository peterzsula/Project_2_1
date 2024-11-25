package org.testing.project_2_1.UI;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * A class that tracks and displays the number of captured pieces for both players in a game.
 * Provides methods to increment, decrement, and reset captured piece counts.
 * The captured pieces are displayed in a styled VBox container.
 */
public class CapturedPiecesTracker {
    // Variables to store the counts of captured pieces for black and white players
    private int blackCaptured = 0;
    private int whiteCaptured = 0;

    // Labels to display the counts of captured pieces
    public Label blackCapturedLabel;
    public Label whiteCapturedLabel;

    // Variables to store player-specific captured counts
    private int playerOneCapturedCount = 0;
    private int playerTwoCapturedCount = 0;

    // A VBox to visually display captured piece information
    private VBox blackWhiteCapturedBox;

    /**
     * Constructor to initialize the CapturedPiecesTracker.
     * Sets up labels, styles, and the layout container for displaying captured piece counts.
     */
    public CapturedPiecesTracker() {
        blackCapturedLabel = new Label(String.valueOf(blackCaptured));
        whiteCapturedLabel = new Label(String.valueOf(whiteCaptured));
        blackWhiteCapturedBox = new VBox(10); // Spacing of 10 between elements
        blackWhiteCapturedBox.getChildren().addAll(blackCapturedLabel, whiteCapturedLabel);

        // Apply styles to labels and the VBox container
        setLabelStyles(blackCapturedLabel, whiteCapturedLabel);
        setTrackerStyles(blackWhiteCapturedBox);
    }

    /**
     * Applies consistent styles to the given labels.
     * 
     * @param labels The labels to style.
     */
    private void setLabelStyles(Label... labels) {
        for (Label label : labels) {
            label.setStyle("-fx-font-size: 18px; -fx-text-fill: black; -fx-font-weight: bold;");
        }
    }

    /**
     * Applies styles to the VBox container used for displaying captured piece counts.
     * 
     * @param box The VBox container to style.
     */
    private void setTrackerStyles(VBox box) {
        box.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(10), null)));
        box.setPadding(new Insets(10));
        box.setBorder(new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));
    }

    /**
     * Increments the count of captured black pieces and updates the corresponding label.
     */
    public void incrementBlackCaptured() {
        blackCaptured++;
        blackCapturedLabel.setText(String.valueOf(blackCaptured));
    }

    /**
     * Increments the count of captured white pieces and updates the corresponding label.
     */
    public void incrementWhiteCaptured() {
        whiteCaptured++;
        whiteCapturedLabel.setText(String.valueOf(whiteCaptured));
    }

    /**
     * Decrements the count of captured white pieces and updates the corresponding label.
     */
    public void decrementWhiteCaptured() {
        whiteCaptured--;
        whiteCapturedLabel.setText(String.valueOf(whiteCaptured));
    }

    /**
     * Decrements the count of captured black pieces and updates the corresponding label.
     */
    public void decrementBlackCaptured() {
        blackCaptured--;
        blackCapturedLabel.setText(String.valueOf(blackCaptured));
    }

    /**
     * Returns the VBox container displaying captured piece counts for both players.
     * 
     * @return The VBox container with captured piece labels.
     */
    public VBox getBlackWhiteCapturedDisplay() {
        return blackWhiteCapturedBox;
    }

    /**
     * Increments the count of captured pieces for a specific player and updates the corresponding label.
     * 
     * @param player The player ("Player 1" or "Player 2") whose captured pieces count is incremented.
     */
    public void capturePiece(String player) {
        if (player.equals("Player 1")) {
            playerOneCapturedCount++;
            blackCapturedLabel.setText(String.valueOf(playerOneCapturedCount));
        } else if (player.equals("Player 2")) {
            playerTwoCapturedCount++;
            whiteCapturedLabel.setText(String.valueOf(playerTwoCapturedCount));
        }
    }

    /**
     * Resets all captured piece counts to 0 and updates the labels to reflect the reset state.
     */
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
