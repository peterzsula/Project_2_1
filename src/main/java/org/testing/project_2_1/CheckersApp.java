package org.testing.project_2_1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CheckersApp extends Application {
    public static final int TILE_SIZE = 60;
    public static final int SIZE = 10;

    private Label playerOneTimerLabel;
    private Label playerTwoTimerLabel;
    private PlayerTimer playerOneTimer;
    private PlayerTimer playerTwoTimer;

    private Group tileGroup = new Group();
    public Group pieceGroup = new Group();
    private Group boardGroup = new Group(); 
    private CapturedPiecesTracker capturedPiecesTracker;

    private boolean isPlayerOneTurn = true; // Track the current turn
    GameLogic gameLogic;

    public Group getBoardGroup() {
        return boardGroup;
    }

    public CheckersApp() {
        gameLogic = new GameLogic(this);
    }

    public CheckersApp(Agent agent, boolean isAgentWhite) {
        gameLogic = new GameLogic(this, agent, isAgentWhite);
        agent.setGameLogic(gameLogic);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent(), SIZE * TILE_SIZE + 320, SIZE * TILE_SIZE); 
        primaryStage.setTitle("FRISIAN DRAUGHTS");
        primaryStage.setScene(scene);
        primaryStage.show();

        playerOneTimer.startCountdown();
    }

    public void resetGame() {
        //TODO: Implement resetGame method
        gameLogic.resetGame();
    }

    public Parent createContent() {
        Pane boardPane = new Pane();
        boardPane.setPrefSize(SIZE * TILE_SIZE, SIZE * TILE_SIZE);
        capturedPiecesTracker = new CapturedPiecesTracker();

        playerOneTimerLabel = new Label("05:00");
        playerTwoTimerLabel = new Label("05:00");

        playerOneTimer = new PlayerTimer(playerOneTimerLabel, 5000); 
        playerTwoTimer = new PlayerTimer(playerTwoTimerLabel, 5000); 

        playerOneTimer.setTotalTime(300_000);
        playerTwoTimer.setTotalTime(300_000);

        styleLabel(playerOneTimerLabel, 16, "black");
        styleLabel(playerTwoTimerLabel, 16, "black");

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

        boardGroup.getChildren().addAll(tileGroup, pieceGroup);
        boardPane.getChildren().add(boardGroup);

        Label playerOneTitle = new Label("PLAYER 1");
        Label playerTwoTitle = new Label("PLAYER 2");
        Label playerOneTimeLabel = new Label("Time Remaining:");
        Label playerTwoTimeLabel = new Label("Time Remaining:");
        Label playerOneCapturedLabel = new Label("Captured Pieces:");
        Label playerTwoCapturedLabel = new Label("Captured Pieces:");
        Button resetButton = new Button("RESET GAME");
        resetButton.setOnAction(e -> gameLogic.resetGame());
        Button undoButton = new Button("UNDO MOVE");
        undoButton.setOnAction(e -> gameLogic.undoLastMove());


        styleTitle(playerOneTitle);
        styleTitle(playerTwoTitle);      
        styleInfoLabel(playerOneTimeLabel);
        styleInfoLabel(playerTwoTimeLabel);
        styleInfoLabel(playerOneCapturedLabel);
        styleInfoLabel(playerTwoCapturedLabel);

        Label playerOneCapturedCount = capturedPiecesTracker.getCapturedPiecesDisplay("Player 1");
        Label playerTwoCapturedCount = capturedPiecesTracker.getCapturedPiecesDisplay("Player 2");

        VBox playerOneBox = new VBox(10, playerOneTitle, playerOneTimeLabel, playerOneTimerLabel, 
                                     playerOneCapturedLabel, playerOneCapturedCount);
        VBox playerTwoBox = new VBox(10, playerTwoTitle, playerTwoTimeLabel, playerTwoTimerLabel, 
                                     playerTwoCapturedLabel, playerTwoCapturedCount);

        
        playerOneBox.setStyle("-fx-background-color: #f0f8ff; -fx-border-color: #b0b0b0; -fx-border-width: 2px; -fx-border-radius: 5px;");
        playerTwoBox.setStyle("-fx-background-color: #f0f8ff; -fx-border-color: #b0b0b0; -fx-border-width: 2px; -fx-border-radius: 5px;");
        playerOneBox.setPadding(new Insets(15));
        playerTwoBox.setPadding(new Insets(15));

        VBox rightPanel = new VBox(20, playerOneBox, playerTwoBox);
        rightPanel.setStyle("-fx-padding: 10 10 10 20;");
        rightPanel.setPrefWidth(260);

        HBox root = new HBox();
        root.getChildren().addAll(boardPane, rightPanel, resetButton, undoButton);
        root.setSpacing(20);

        return root;
    }

    private void styleLabel(Label label, int fontSize, String textColor) {
        label.setStyle("-fx-font-size: " + fontSize + "px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");
    }

    private void styleTitle(Label titleLabel) {
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setStyle("-fx-text-fill: black; -fx-underline: true;");
    }

    private void styleInfoLabel(Label infoLabel) {
        infoLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        infoLabel.setStyle("-fx-text-fill: black;");
    }

    public void movePiece(Piece piece, int newX, int newY) {
        piece.pieceDrawer.setOnMouseReleased(e -> {
            Move move = gameLogic.determineMoveType(piece, newX, newY);
            gameLogic.takeTurn(move);
            piece.pieceDrawer.clearHighlight(); 
    
            if (isPlayerOneTurn) {
                playerOneTimer.stopCountdown();  
                playerOneTimer.startMove();      
                playerTwoTimer.startCountdown(); 
            } else {
                playerTwoTimer.stopCountdown();  
                playerTwoTimer.startMove();      
                playerOneTimer.startCountdown(); 
            }

            isPlayerOneTurn = !isPlayerOneTurn;
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
