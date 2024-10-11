package org.testing.project_2_1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CheckersApp extends Application {
    public static final int TILE_SIZE = 60;
    public static final int SIZE = 10;

    private Label timerLabel;
    private Timeline timer;
    private int timeRemaining;

    private Group tileGroup = new Group();
    public Group pieceGroup = new Group();
    private CapturedPiecesTracker capturedPiecesTracker;

    GameLogic gameLogic;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent(), SIZE * TILE_SIZE + 300, SIZE * TILE_SIZE);
        primaryStage.setTitle("FRISIAN DRAUGHTS");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Parent createContent() {
        Pane boardPane = new Pane();
        gameLogic = new GameLogic(this);
        boardPane.setPrefSize(SIZE * TILE_SIZE, SIZE * TILE_SIZE);
        capturedPiecesTracker = new CapturedPiecesTracker();
        timerLabel = new Label("Time remaining: 30s");
        setTimerStyle();
        startMoveTimer();

        for (Tile[] row : gameLogic.board) {
            for (Tile tile : row) {
                tileGroup.getChildren().add(tile.tileDrawer);
                if (tile.hasPiece()) {
                    Piece piece = tile.getPiece();
                    piece.setPieceDrawer(new PieceDrawer(piece, this));
                    pieceGroup.getChildren().add(piece.pieceDrawer);
                }
            }
            
        }

        boardPane.getChildren().addAll(tileGroup, pieceGroup);

        VBox rightPanel = new VBox(50); // Create a vertical layout with 10px spacing
        rightPanel.getChildren().addAll(timerLabel, capturedPiecesTracker.getCapturedPiecesDisplay());
        rightPanel.setStyle("-fx-padding: 0 0px; -fx-alignment: top-right;");

        HBox root = new HBox();
        root.getChildren().addAll(boardPane, rightPanel); // Add the rightPanel on the side of the board
        root.setSpacing(50);

        return root;
    }

    //TODO: fix the timer
    private void startMoveTimer() {
        timeRemaining = 30;

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            timerLabel.setText("Time remaining: " + timeRemaining + "s");

            if (timeRemaining <= 10) {
                setLowTimeStyle();
            } else {
                setTimerStyle();
            }

            if (timeRemaining <= 0) {
                timer.stop();
                displaySwitchTurnMessage();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.playFromStart();
    }

    private void resetTimer() {
        timeRemaining = 30;
        timerLabel.setText("Time remaining: " + timeRemaining + "s");
        setTimerStyle();
        timer.playFromStart();
    }

    private void displaySwitchTurnMessage() {
        gameLogic.isWhiteTurn = !gameLogic.isWhiteTurn;
        timerLabel.setText("Switching to Opponent's turn...");

        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(2), event -> resetTimer()));
        delay.play();
    }

    private void setTimerStyle() {
        timerLabel.setStyle("-fx-font-size: 24px; -fx-background-color: lightgray; -fx-padding: 10px; " +
                "-fx-border-color: black; -fx-border-width: 2px; -fx-text-fill: darkgreen; " +
                "-fx-font-family: 'Arial'; -fx-border-radius: 5px; -fx-background-radius: 5px;");
    }

    private void setLowTimeStyle() {
        timerLabel.setStyle("-fx-font-size: 24px; -fx-background-color: lightgray; -fx-padding: 10px; " +
                "-fx-border-color: black; -fx-border-width: 2px; -fx-text-fill: red; " +
                "-fx-font-family: 'Arial'; -fx-border-radius: 5px; -fx-background-radius: 5px;");
    }

    public void movePiece(Piece piece, int newX, int newY) {
        piece.pieceDrawer.setOnMouseReleased(e -> {
            gameLogic.takeTurn(piece, newX, newY);
        });
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE;
    }

    public int toBoard(double pixel) {
        return (int) (pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    private void displayGameOverMessage() {
        // Implement game over message display logic here
        System.out.println("Game Over!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
