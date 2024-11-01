package org.testing.project_2_1;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CheckersApp extends Application {
    public static final int TILE_SIZE = 60;
    public static final int SIZE = 10;

    private Label timerLabel;
    private Label countdownLabel; // Temporitzador de compte enrere de la partida
    private MoveTimer moveTimer; // Temporitzador de torn
    private GameCountdown gameCountdown; // Temporitzador de compte enrere per a tota la partida

    private Group tileGroup = new Group();
    public Group pieceGroup = new Group();
    private CapturedPiecesTracker capturedPiecesTracker;

    GameLogic gameLogic;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent(), SIZE * TILE_SIZE + 320, SIZE * TILE_SIZE); // Ampliar l'ample de la finestra per assegurar-se que hi ha prou espai
        primaryStage.setTitle("FRISIAN DRAUGHTS");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Parent createContent() {
        Pane boardPane = new Pane();
        gameLogic = new GameLogic(this);
        boardPane.setPrefSize(SIZE * TILE_SIZE, SIZE * TILE_SIZE);
        capturedPiecesTracker = new CapturedPiecesTracker();

        // Inicialització del temporitzador de torn
        timerLabel = new Label("Opponent's turn ends in: 30s");
        styleLabel(timerLabel, 16, "darkgreen");
        moveTimer = new MoveTimer(timerLabel, this::changeTurn);

        // Inicialització del temporitzador de compte enrere de partida
        countdownLabel = new Label("Game ends in: 05:00");
        styleLabel(countdownLabel, 18, "black"); // Lletra més gran i color negre
        gameCountdown = new GameCountdown(countdownLabel, this::displayGameOverMessage);

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

        VBox rightPanel = new VBox(20); // Ajusta espaiat a 20 per a la distribució uniforme
        rightPanel.getChildren().addAll(timerLabel, countdownLabel, capturedPiecesTracker.getCapturedPiecesDisplay());
        rightPanel.setStyle("-fx-padding: 10 10 10 20; -fx-alignment: top-left;");
        rightPanel.setPrefWidth(260);

        HBox root = new HBox();
        root.getChildren().addAll(boardPane, rightPanel);
        root.setSpacing(50);

        return root;
    }

    private void styleLabel(Label label, int fontSize, String textColor) {
        label.setStyle("-fx-font-size: " + fontSize + "px; -fx-font-weight: bold; -fx-background-color: #d3d3d3; " +
                "-fx-padding: 10px 20px; -fx-border-color: #b0b0b0; -fx-border-width: 2px; " +
                "-fx-text-fill: " + textColor + "; -fx-font-family: 'Arial'; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        label.setPrefWidth(230);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setWrapText(true);
    }

    public void movePiece(Piece piece, int newX, int newY) {
        piece.pieceDrawer.setOnMouseReleased(e -> {
            gameLogic.takeTurn(piece, newX, newY);
            moveTimer.reset(); // Reinicia el temporitzador després de cada moviment
        });
    }

    private void changeTurn() {
        timerLabel.setText("Changing opponent's turn...");
        moveTimer.showTurnChangeMessage(); // Mostra el missatge de canvi de torn
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE;
    }

    public int toBoard(double pixel) {
        return (int) (pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    private void displayGameOverMessage() {
        countdownLabel.setText("Game Over!"); // Mostra el missatge de final de partida
        System.out.println("Game Over!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
