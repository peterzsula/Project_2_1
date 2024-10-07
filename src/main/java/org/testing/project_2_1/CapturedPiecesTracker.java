package org.testing.project_2_1;


import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class CapturedPiecesTracker {
    private int blackCaptured = 0;
    private int whiteCaptured = 0;

    private Label blackCapturedLabel;
    private Label whiteCapturedLabel;
    private VBox vbox;

    public CapturedPiecesTracker() {
        blackCapturedLabel = new Label("Black Captured: 0");
        whiteCapturedLabel = new Label("White Captured: 0");

        vbox = new VBox(10); // Creates a vertical layout with spacing between labels
        vbox.getChildren().addAll(blackCapturedLabel, whiteCapturedLabel);
        setLabelStyles();
        setTrackerStyles();
    }

    private void setLabelStyles() {
        blackCapturedLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black; -fx-font-weight: bold;");
        whiteCapturedLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black; -fx-font-weight: bold;");
    }
    private void setTrackerStyles() {

        // Set background color and padding
        vbox.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(10), null)));
        vbox.setPadding(new javafx.geometry.Insets(10));

        // Add border to the tracker
        vbox.setBorder(new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));

    }

    public void incrementBlackCaptured() {
        blackCaptured++;
        blackCapturedLabel.setText("Black Captured: " + blackCaptured);
    }

    public void incrementWhiteCaptured() {
        whiteCaptured++;
        whiteCapturedLabel.setText("White Captured: " + whiteCaptured);
    }

    public VBox getCapturedPiecesDisplay() {

        return vbox;
    }
}

