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

/**
 * The main application class for Frisian Checkers.
 * This class sets up the game board, user interface, and integrates the game logic for both
 * human and AI players. It also manages timers, captures, evaluation bar, and user interactions.
 */
public class CheckersApp extends Application {
    public static final int TILE_SIZE = 60; // Size of each tile in pixels
    public static final int SIZE = 10; // Number of tiles in one row/column
    public int noOfPlayers = 0; // Number of human players
    public GameLogic gameLogic; // Game logic handler
    private Label playerOneTimerLabel; // Timer display for Player 1
    private Label playerTwoTimerLabel; // Timer display for Player 2
    private Label captureMessageLabel; // Displays messages related to captures
    private PlayerTimer playerOneTimer; // Timer object for Player 1
    private PlayerTimer playerTwoTimer; // Timer object for Player 2
    private ProgressBar evaluationBar; // Bar displaying the evaluation score
    private Label evaluationScoreLabel; // Label displaying the evaluation score as text
    private double minObservedScore = -20; // Minimum observed evaluation score
    private double maxObservedScore = 20; // Maximum observed evaluation score
    private Group tileGroup; // Group to hold all tile objects
    public Group pieceGroup; // Group to hold all piece objects
    private Group boardGroup; // Group to hold board visuals
    public CapturedPiecesTracker capturedPiecesTracker; // Tracks captured pieces

    boolean isPlayerOneTurn; // Tracks which player's turn it is
    private long previousPlayerOneTime; // Tracks Player 1's previous time
    private long previousPlayerTwoTime; // Tracks Player 2's previous time
    private Agent agent1; // The first agent
    private Agent agent2; // The second agent
    static boolean autoRestart = true; // Tracks if the game should auto-restart

    /**
     * Gets the group containing the board visuals.
     *
     * @return The group of board visuals.
     */
    public Group getBoardGroup() {
        return boardGroup;
    }

    /**
     * Constructor for a two-human-player game.
     */
    public CheckersApp() {
        gameLogic = new GameLogic(this);
        noOfPlayers = 2;
        setDefaultValues();
    }

    /**
     * Constructor for a single-player game against an AI agent.
     *
     * @param agent The AI agent to play against.
     */
    public CheckersApp(Agent agent) {
        agent1 = agent;
        agent2 = null;
        gameLogic = new GameLogic(this, agent);
        agent.setGameLogic(gameLogic);
        noOfPlayers = 1;
        setDefaultValues();
    }

    /**
     * Constructor for a zero-human-player game (AI vs. AI).
     *
     * @param agent1 The first AI agent.
     * @param agent2 The second AI agent.
     */
    public CheckersApp(Agent agent1, Agent agent2) {
        this.agent1 = agent1;
        this.agent2 = agent2;
        gameLogic = new GameLogic(this, agent1, agent2);
        agent1.setGameLogic(gameLogic);
        agent2.setGameLogic(gameLogic);
        noOfPlayers = 0;
        setDefaultValues();
    }

    /**
     * Sets the default values for the application, including timers and groups.
     */
    public void setDefaultValues() {
        isPlayerOneTurn = gameLogic.g.getIsWhiteTurn();
        evaluationBar = new ProgressBar(0.5);
        capturedPiecesTracker = new CapturedPiecesTracker();
        tileGroup = new Group();
        pieceGroup = new Group();
        boardGroup = new Group();
        pieceGroup.getChildren().clear();
        boardGroup.getChildren().clear();
    }

    /**
     * Starts the application by setting up the main stage and scene.
     *
     * @param primaryStage The primary stage for the application.
     * @throws Exception If an error occurs during startup.
     */
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

    public void autoRestart() {
        if (autoRestart && gameLogic.g.isGameOver() && noOfPlayers == 0) {
            //wait for 2 seconds before restarting
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            resetGUI();
        }
    }

    /**
     * Creates the main content of the application, including the board and UI components.
     *
     * @return The root node of the application scene.
     */
    public Parent createContent() {
        Pane boardPane = new Pane();
        boardPane.setPrefSize(SIZE * TILE_SIZE, SIZE * TILE_SIZE);

        // Label to tell when captures are required
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

        // Create labels for player information
        Label playerOneTitle = new Label("PLAYER 1");
        Label playerTwoTitle = new Label("PLAYER 2");
        Label playerOneTimeLabel = new Label("Time Remaining:");
        Label playerTwoTimeLabel = new Label("Time Remaining:");
        Label playerOneCapturedLabel = new Label("Captured Pieces:");
        Label playerTwoCapturedLabel = new Label("Captured Pieces:");

        // Reset Button
        Button resetButton = new Button("Restart");
        resetButton.setOnAction(e -> {
            resetGUI();
        });

        // Undo Button
        Button undoButton = new Button("Undo");
        undoButton.setOnAction(e -> undoLastMove());

        // Pause Button
        Button pauseButton = new Button("Pause/Resume");
        pauseButton.setOnAction(e -> pauseAgents());

        // Place buttons in a horizontal box
        HBox buttonBox = new HBox(10, resetButton, undoButton, pauseButton);
        buttonBox.setPadding(new Insets(10));

        styleTitle(playerOneTitle);
        styleTitle(playerTwoTitle);
        styleInfoLabel(playerOneTimeLabel);
        styleInfoLabel(playerTwoTimeLabel);
        styleInfoLabel(playerOneCapturedLabel);
        styleInfoLabel(playerTwoCapturedLabel);

        // Captured pieces labels
        Label playerOneCapturedCount = capturedPiecesTracker.blackCapturedLabel;
        Label playerTwoCapturedCount = capturedPiecesTracker.whiteCapturedLabel;

        VBox playerOneBox = new VBox(10, playerOneTitle, playerOneTimeLabel, playerOneTimerLabel, playerOneCapturedLabel, playerOneCapturedCount);
        VBox playerTwoBox = new VBox(10, playerTwoTitle, playerTwoTimeLabel, playerTwoTimerLabel, playerTwoCapturedLabel, playerTwoCapturedCount);

        playerOneBox.setStyle("-fx-background-color: #f0f8ff; -fx-border-color: #b0b0b0; -fx-border-width: 2px; -fx-border-radius: 5px;");
        playerTwoBox.setStyle("-fx-background-color: #f0f8ff; -fx-border-color: #b0b0b0; -fx-border-width: 2px; -fx-border-radius: 5px;");
        playerOneBox.setPadding(new Insets(15));
        playerTwoBox.setPadding(new Insets(15));

        VBox rightPanel = new VBox(20, playerOneBox, playerTwoBox, captureMessageLabel, evaluationBar, evaluationScoreLabel, buttonBox);
        rightPanel.setStyle("-fx-padding: 10 10 10 20;");
        rightPanel.setPrefWidth(260);

        HBox root = new HBox();
        root.getChildren().addAll(boardPane, rightPanel);
        root.setSpacing(20);
        return root;
    }
    /**
     * Pauses both agents (if any) by invoking their pause methods.
     */
    private void pauseAgents() {
        if (gameLogic.agent != null) {
            gameLogic.agent.pause();
        }
        if (gameLogic.opponent != null) {
            gameLogic.opponent.pause();
        }
    }

    /**
     * Handles moving a piece from its old position to a new position on the board.
     * Updates the timers based on the turn.
     * 
     * @param oldX   The x-coordinate of the piece's current position.
     * @param oldY   The y-coordinate of the piece's current position.
     * @param piece  The piece being moved.
     * @param newX   The x-coordinate of the piece's new position.
     * @param newY   The y-coordinate of the piece's new position.
     */
    public void movePiece(int oldX, int oldY, Piece piece, int newX, int newY) {
        Move move = gameLogic.g.determineMoveType(oldX, oldY, newX, newY);
        piece.getPieceDrawer().clearHighlight();

        if (gameLogic.takeMove(move)) {
            piece.movePiece(move);

            // Update the timers
            if (isPlayerOneTurn) {
                playerOneTimer.stopCountdown();
                playerTwoTimer.startCountdown();
            } else {
                playerTwoTimer.stopCountdown();
                playerOneTimer.startCountdown();
            }
        }
    }

    /**
     * Resets the graphical user interface and timers to their initial state.
     * Ensures all pieces and tiles are properly redrawn and re-initialized.
     */
    public void resetGUI() {
        if (noOfPlayers == 0) {
            gameLogic = new GameLogic(this, agent1, agent2);
        }
        else if (noOfPlayers == 1) {
            gameLogic = new GameLogic(this, agent1);
        } else {
            gameLogic = new GameLogic(this);   
        }
        if (pieceGroup == null) {
            pieceGroup = new Group();
        }
        if (tileGroup == null) {
            tileGroup = new Group();
        }

        playerOneTimer.reset();
        playerTwoTimer.reset();

        // Start the correct timer based on the turn
        if (isPlayerOneTurn) {
            playerOneTimer.startCountdown();
            playerTwoTimer.stopCountdown();
        } else {
            playerTwoTimer.startCountdown();
            playerOneTimer.stopCountdown();
        }

        pieceGroup.getChildren().clear();

        // Redraw the board and pieces
        for (Tile[] row : gameLogic.g.getBoard()) {
            for (Tile tile : row) {
                if (tile.tileDrawer != null) {
                    tileGroup.getChildren().add(tile.tileDrawer);
                }
                if (tile.hasPiece()) {
                    Piece piece = tile.getPiece();
                    if (piece != null && piece.getPieceDrawer() == null) {
                        piece.setPieceDrawer(new PieceDrawer(piece, this));
                    }
                }
            }
        }

        // Handle AI moves if necessary
        if (noOfPlayers == 0 || (!isPlayerOneTurn && noOfPlayers == 1)) {
            gameLogic.agent.makeMove();
        } else if (noOfPlayers == 1 && gameLogic.agent != null) {
            gameLogic.agent.setGameLogic(gameLogic);
            if (gameLogic.g.getIsWhiteTurn() == gameLogic.agent.isWhite()) {
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

    /**
     * Adds all pieces and tiles to the game board and initializes their graphical representation.
     * 
     * @param boardPane The Pane where the board and pieces will be displayed.
     */
    public void addPiecestoBoard(Pane boardPane) {
        for (Tile[] row : gameLogic.g.getBoard()) {
            for (Tile tile : row) {
                tile.setTileDrawer(new TileDrawer(tile, this));
                tileGroup.getChildren().add(tile.tileDrawer);

                if (tile.hasPiece()) {
                    Piece piece = tile.getPiece();
                    PieceDrawer drawer = new PieceDrawer(piece, this);
                    piece.setPieceDrawer(drawer);
                }
            }
        }
        boardGroup.getChildren().addAll(tileGroup, pieceGroup);
        boardPane.getChildren().add(boardGroup);
    }

    /**
     * Styles a label with a specific font size and color.
     * 
     * @param label     The label to style.
     * @param fontSize  The font size for the label.
     * @param textColor The color of the text.
     */
    private void styleLabel(Label label, int fontSize, String textColor) {
        label.setStyle("-fx-font-size: " + fontSize + "px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");
    }

    /**
     * Styles a title label with bold text and underlined formatting.
     * 
     * @param titleLabel The title label to style.
     */
    private void styleTitle(Label titleLabel) {
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setStyle("-fx-text-fill: black; -fx-underline: true;");
    }

    /**
     * Styles an informational label with semi-bold text formatting.
     * 
     * @param infoLabel The informational label to style.
     */
    private void styleInfoLabel(Label infoLabel) {
        infoLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        infoLabel.setStyle("-fx-text-fill: black;");
    }

    /**
     * Updates the capture message label with the provided message.
     * 
     * @param message The message to display.
     */
    public void updateCaptureMessage(String message) {
        captureMessageLabel.setText(message);
    }

    /**
     * Undoes the last move made in the game, restoring the previous board state and timers.
     */
    public void undoLastMove() {
        playerOneTimer.stopCountdown();
        playerTwoTimer.stopCountdown();

        if (noOfPlayers == 0) {
            gameLogic.undoLastMove(gameLogic.g);
        }
        if (noOfPlayers == 1) {
            gameLogic.undoLastMove(gameLogic.g);
            gameLogic.undoLastMove(gameLogic.g);
        } else {
            gameLogic.undoLastMove(gameLogic.g);
        }
        playerOneTimer.setTotalTime(previousPlayerOneTime * 1000);
        playerTwoTimer.setTotalTime(previousPlayerTwoTime * 1000);

        if (isPlayerOneTurn) {
            playerOneTimer.startCountdown();
        } else {
            playerTwoTimer.startCountdown();
        }
    }

    /**
     * Updates the evaluation bar and score label to reflect the current game state.
     */
    public void updateEvaluationBar() {
        double evaluationScore = gameLogic.g.evaluateBoard();
        updateObservedRange(evaluationScore);
        double normalizedScore = normalizeEvaluation(evaluationScore);

        evaluationBar.setProgress(normalizedScore);
        if (evaluationScore > 0) {
            evaluationBar.setStyle("-fx-accent: lightgreen;");
        } else if (evaluationScore < 0) {
            evaluationBar.setStyle("-fx-accent: lightcoral;");
        } else {
            evaluationBar.setStyle("-fx-accent: gray;");
        }

        evaluationScoreLabel.setText(String.format("Score: %.1f", evaluationScore));
    }

    /**
     * Normalizes the evaluation score to fit between 0 and 1 based on observed ranges.
     * 
     * @param evaluationScore The raw evaluation score.
     * @return The normalized score between 0 and 1.
     */
    private double normalizeEvaluation(double evaluationScore) {
        if (maxObservedScore == minObservedScore) {
            return 0.5;
        }
        return (evaluationScore - minObservedScore) / (maxObservedScore - minObservedScore);
    }

    /**
     * Updates the observed range for evaluation scores.
     * 
     * @param evaluationScore The current evaluation score to update the range with.
     */
    private void updateObservedRange(double evaluationScore) {
        if (evaluationScore < minObservedScore) {
            minObservedScore = evaluationScore;
        } else if (evaluationScore > maxObservedScore) {
            maxObservedScore = evaluationScore;
        }
    }

    /**
     * Clears all highlights from the board.
     */
    public void clearHighlights() {
        for (Tile[] row : gameLogic.g.getBoard()) {
            for (Tile tile : row) {
                tile.tileDrawer.clearHighlight();
            }
        }
    }

    /**
     * Updates the glowing effect for pieces with available moves.
     */
    public void updateGlows() {
        List<Piece> pieces = gameLogic.g.getAllPieces();
        for (Piece piece : pieces) {
            piece.getPieceDrawer().updateGlow();
        }
    }

    /**
     * The main method to launch the application.
     * 
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}