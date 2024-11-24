package org.testing.project_2_1.UI;

import org.testing.project_2_1.Agents.*;
import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.GameLogic.Piece;
import org.testing.project_2_1.GameLogic.Tile;
import org.testing.project_2_1.Moves.Move;

import javafx.scene.image.Image;
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
import javafx.scene.control.ProgressBar;
import java.util.List;

public class CheckersApp extends Application {
    public static final int TILE_SIZE = 60;
    public static final int SIZE = 10;
    public int noOfPlayers = 0;
    public GameLogic gameLogic;
    private Label playerOneTimerLabel;
    private Label playerTwoTimerLabel;
    private Label captureMessageLabel;
    private PlayerTimer playerOneTimer;
    private PlayerTimer playerTwoTimer;
    private ProgressBar evaluationBar;
    private Label evaluationScoreLabel;
    private double minObservedScore = -20;
    private double maxObservedScore = 20;
    private Group tileGroup;
    public Group pieceGroup;
    private Group boardGroup;
    public CapturedPiecesTracker capturedPiecesTracker;

    boolean isPlayerOneTurn;
    private long previousPlayerOneTime;
    private long previousPlayerTwoTime;

    public Group getBoardGroup() {
        return boardGroup;
    }

    public CheckersApp() {
        gameLogic = new GameLogic(this);
        noOfPlayers = 2;
        setDefaultValues();
    }

    public CheckersApp(Agent agent) {
        gameLogic = new GameLogic(this, agent);
        agent.setGameLogic(gameLogic);
        noOfPlayers = 1;
        setDefaultValues();
    }

    public CheckersApp(Agent agent1, Agent agent2) {
        gameLogic = new GameLogic(this, agent1, agent2);
        agent1.setGameLogic(gameLogic);
        agent2.setGameLogic(gameLogic);
        noOfPlayers = 0;
        setDefaultValues();
    }

    public void setDefaultValues(){
        isPlayerOneTurn = gameLogic.g.getIsWhiteTurn();
        evaluationBar = new ProgressBar(0.5);
        capturedPiecesTracker = new CapturedPiecesTracker();
        tileGroup = new Group();
        pieceGroup = new Group();
        boardGroup = new Group();
        pieceGroup.getChildren().clear();
        boardGroup.getChildren().clear();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent(), SIZE * TILE_SIZE + 320, SIZE * TILE_SIZE);
        primaryStage.setTitle("FRISIAN DRAUGHTS");
        primaryStage.setScene(scene);
        primaryStage.show();

        Image icon = new Image("pixel-frisian.png");
        primaryStage.getIcons().add(icon);

        playerOneTimer.startCountdown();

        if (noOfPlayers == 0 || (!isPlayerOneTurn && noOfPlayers == 1)) {
            gameLogic.agent.makeMove();
        } else if (noOfPlayers == 1 && gameLogic.agent.isWhite() && isPlayerOneTurn) {
            gameLogic.agent.makeMove();
        }
    
        // Ensure the application terminates when the window is closed
        primaryStage.setOnCloseRequest(event -> {
            if (playerOneTimer != null) {
                playerOneTimer.stopCountdown();
            }
            if (playerTwoTimer != null) {
                playerTwoTimer.stopCountdown();
            }
    
            // Terminate the application
            System.exit(0);
        });
    }

    public Parent createContent() {
        Pane boardPane = new Pane();
        boardPane.setPrefSize(SIZE * TILE_SIZE, SIZE * TILE_SIZE);
    
        // Label to tell you when to capture
        captureMessageLabel = new Label();
        captureMessageLabel.setFont(new Font("Arial", 16));
        captureMessageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    
        // Initialize the evaluation bar
        evaluationBar = new ProgressBar(0.5);
        evaluationBar.setPrefWidth(200);
        evaluationBar.setStyle("-fx-accent: gray;");
    
        // Label to display the evaluation score
        evaluationScoreLabel = new Label("Score: 0.0");
        evaluationScoreLabel.setFont(new Font("Arial", 14));
        evaluationScoreLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
    
        capturedPiecesTracker = new CapturedPiecesTracker();
    
        playerOneTimerLabel = new Label("05:00");
        playerTwoTimerLabel = new Label("05:00");
    
        playerOneTimer = new PlayerTimer(playerOneTimerLabel, 300_000, 5000);
        playerTwoTimer = new PlayerTimer(playerTwoTimerLabel, 300_000, 5000);
    
        playerOneTimer.setTotalTime(300_000);
        playerTwoTimer.setTotalTime(300_000);
    
        styleLabel(playerOneTimerLabel, 16, "black");
        styleLabel(playerTwoTimerLabel, 16, "black");
    
        addPiecestoBoard(boardPane);
    
        Label playerOneTitle = new Label("PLAYER 1");
        Label playerTwoTitle = new Label("PLAYER 2");
        Label playerOneTimeLabel = new Label("Time Remaining:");
        Label playerTwoTimeLabel = new Label("Time Remaining:");
        Label playerOneCapturedLabel = new Label("Captured Pieces:");
        Label playerTwoCapturedLabel = new Label("Captured Pieces:");
    
        // Simplified Reset Button
        Button resetButton = new Button("Restart");
        resetButton.setOnAction(e -> {
            gameLogic.restartGame(); // Reset game logic
            resetGUI(); // Reset the GUI
        });
    
        // Simplified Undo Button
        Button undoButton = new Button("Undo");
        undoButton.setOnAction(e -> undoLastMove());

        Button pauseButton = new Button("Pause/Resume");
        pauseButton.setOnAction(e -> {pauseAgents();});
    
        // Place buttons in a horizontal box
        HBox buttonBox = new HBox(10, resetButton, undoButton, pauseButton);
        buttonBox.setPadding(new Insets(10)); // Add some spacing around the buttons
    
        styleTitle(playerOneTitle);
        styleTitle(playerTwoTitle);
        styleInfoLabel(playerOneTimeLabel);
        styleInfoLabel(playerTwoTimeLabel);
        styleInfoLabel(playerOneCapturedLabel);
        styleInfoLabel(playerTwoCapturedLabel);
    
        Label playerOneCapturedCount = capturedPiecesTracker.blackCapturedLabel;
        Label playerTwoCapturedCount = capturedPiecesTracker.whiteCapturedLabel;
    
        VBox playerOneBox = new VBox(10, playerOneTitle, playerOneTimeLabel, playerOneTimerLabel, playerOneCapturedLabel, playerOneCapturedCount);
        VBox playerTwoBox = new VBox(10, playerTwoTitle, playerTwoTimeLabel, playerTwoTimerLabel, playerTwoCapturedLabel, playerTwoCapturedCount);
    
        playerOneBox.setStyle("-fx-background-color: #f0f8ff; -fx-border-color: #b0b0b0; -fx-border-width: 2px; -fx-border-radius: 5px;");
        playerTwoBox.setStyle("-fx-background-color: #f0f8ff; -fx-border-color: #b0b0b0; -fx-border-width: 2px; -fx-border-radius: 5px;");
        playerOneBox.setPadding(new Insets(15));
        playerTwoBox.setPadding(new Insets(15));
    
        // Add all components to the right panel
        VBox rightPanel = new VBox(20, playerOneBox, playerTwoBox, captureMessageLabel, evaluationBar, evaluationScoreLabel, buttonBox);
        rightPanel.setStyle("-fx-padding: 10 10 10 20;");
        rightPanel.setPrefWidth(260);
    
        HBox root = new HBox();
        root.getChildren().addAll(boardPane, rightPanel);
        root.setSpacing(20);
        return root;
    }
    
       
    private void pauseAgents() {
        if (gameLogic.agent != null) {
            gameLogic.agent.pause();
        }
        if (gameLogic.opponent != null) {
            gameLogic.opponent.pause();
        }
    }

    public void movePiece(int oldX, int oldY, Piece piece, int newX, int newY) {
        Move move = gameLogic.g.determineMoveType(oldX, oldY, newX, newY);
        piece.getPieceDrawer().clearHighlight();

        if (gameLogic.takeMove(move)) {
            piece.movePiece(move);

            if (isPlayerOneTurn) {
                playerOneTimer.stopCountdown();
                playerTwoTimer.startCountdown();
            } else {
                playerTwoTimer.stopCountdown();
                playerOneTimer.startCountdown();
            }
        }
    }

    public void resetGUI() {
        // Ensure pieceGroup and tileGroup are initialized
        if (pieceGroup == null) {
            pieceGroup = new Group();
        }
        if (tileGroup == null) {
            tileGroup = new Group();
        }
    
        playerOneTimer.reset();
        playerTwoTimer.reset();
        if (isPlayerOneTurn) {
            playerOneTimer.startCountdown();
            playerTwoTimer.stopCountdown();
        } else {
            playerTwoTimer.startCountdown();
            playerOneTimer.stopCountdown();
        }
    
        pieceGroup.getChildren().clear();
    
        // Update the board with tiles and pieces
        for (Tile[] row : gameLogic.g.getBoard()) {
            for (Tile tile : row) {
                if (tile.tileDrawer != null) {
                    tileGroup.getChildren().add(tile.tileDrawer);
                }
                if (tile.hasPiece()) {
                    Piece piece = tile.getPiece();
                    if (piece != null) {
                        // Initialize or reassign the PieceDrawer if null
                        if (piece.getPieceDrawer() == null) {
                            piece.setPieceDrawer(new PieceDrawer(piece, this));
                        }
                    }
                }
            }
        }
        if (noOfPlayers == 0 || (!isPlayerOneTurn && noOfPlayers == 1)) {
            gameLogic.agent.makeMove();
        }
        // Handle agent's first move if necessary
        else if (noOfPlayers == 1 && gameLogic.agent != null) {
            gameLogic.agent.setGameLogic(gameLogic); // Ensure the agent is associated with the game logic
    
            if (gameLogic.g.getIsWhiteTurn() == gameLogic.agent.isWhite()) {
                // Ensure the agent makes its first move if it's their turn
                System.out.println("Agent is making the first move...");
                try {
                    gameLogic.agent.makeMove();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error during the agent's initial move.");
                }
            }
        }

        evaluationBar.setProgress(0.5);
        evaluationScoreLabel.setText("Score: 0.0");

        capturedPiecesTracker.reset();
    }
        
    public void addPiecestoBoard(Pane boardPane) {
        for (Tile[] row : gameLogic.g.getBoard()) {
            for (Tile tile : row) {
                tile.setTileDrawer(new TileDrawer(tile, this));
                tileGroup.getChildren().add(tile.tileDrawer);
                if (tile.hasPiece()) {
                    Piece piece = tile.getPiece();
                    PieceDrawer drawer = new PieceDrawer(piece, this);
                    piece.setPieceDrawer(drawer); // Associate the drawer with the piece
                }
            }
        }
        boardGroup.getChildren().addAll(tileGroup, pieceGroup);
        boardPane.getChildren().add(boardGroup);
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

    public void updateCaptureMessage(String message) {
        captureMessageLabel.setText(message);
    }

    public void undoLastMove() {
        playerOneTimer.stopCountdown();
        playerTwoTimer.stopCountdown();

        gameLogic.undoLastMove(gameLogic.g);

        playerOneTimer.setTotalTime(previousPlayerOneTime * 1000);
        playerTwoTimer.setTotalTime(previousPlayerTwoTime * 1000);

        if (isPlayerOneTurn) {
            playerOneTimer.startCountdown();
        } else {
            playerTwoTimer.startCountdown();
        }
    }

    public void updateEvaluationBar() {
        double evaluationScore = GameLogic.evaluateBoard(gameLogic.g); // Get current evaluation
        updateObservedRange(evaluationScore);
        double normalizedScore = normalizeEvaluation(evaluationScore);

        evaluationBar.setProgress(normalizedScore);
        if (evaluationScore > 0) {
            evaluationBar.setStyle("-fx-accent: lightgreen;"); // White advantage
        } else if (evaluationScore < 0) {
            evaluationBar.setStyle("-fx-accent: lightcoral;"); // Black advantage
        } else {
            evaluationBar.setStyle("-fx-accent: gray;"); // Balanced
        }

        evaluationScoreLabel.setText(String.format("Score: %.1f", evaluationScore));
    }

    private double normalizeEvaluation(double evaluationScore) {
        if (maxObservedScore == minObservedScore) {
            return 0.5;
        }
        return (evaluationScore - minObservedScore) / (maxObservedScore - minObservedScore);
    }

    private void updateObservedRange(double evaluationScore) {
        if (evaluationScore < minObservedScore) {
            minObservedScore = evaluationScore;
        } else if (evaluationScore > maxObservedScore) {
            maxObservedScore = evaluationScore;
        }
    }

    public void clearHighlights() {
        for (Tile[] row : gameLogic.g.getBoard()) {
            for (Tile tile : row) {
                tile.tileDrawer.clearHighlight();
            }
        }
    }
    
    public void updateGlows() {
        List<Piece> pieces = gameLogic.g.getAllPieces();
        for (Piece piece : pieces) {
            piece.getPieceDrawer().updateGlow();
        }
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}
