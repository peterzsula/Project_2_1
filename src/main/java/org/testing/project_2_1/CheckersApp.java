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
    boolean whitesTurn = true;
    public static final int TILE_SIZE = 80;
    public static final int SIZE = 10;

    private Label timerLabel;
    private Timeline timer;
    private int timeRemaining;

    private Tile[][] board = new Tile[SIZE][SIZE];
    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();
    private CapturedPiecesTracker capturedPiecesTracker;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent(), SIZE * TILE_SIZE + 300, SIZE * TILE_SIZE);
        primaryStage.setTitle("FRISIAN DRAUGHTS");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Parent createContent() {
        Pane boardPane = new Pane();
        boardPane.setPrefSize(SIZE * TILE_SIZE, SIZE * TILE_SIZE);

        timerLabel = new Label("Time remaining: 30s");
        setTimerStyle();
        startMoveTimer();

        capturedPiecesTracker = new CapturedPiecesTracker();

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Tile tile = new Tile(x, y);
                board[x][y] = tile;

                tileGroup.getChildren().add(tile);

                Piece piece = null;
                if (y <= 3 && tile.isBlack()) {
                    piece = makePiece(PieceType.BLACK, x, y);
                } else if (y >= 6 && tile.isBlack()) {
                    piece = makePiece(PieceType.WHITE, x, y);
                }

                if (piece != null) {
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
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
        whitesTurn = !whitesTurn;
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

    private MoveResult tryMove(Piece piece, int newX, int newY) {
        int x0 = toBoard(piece.getOldX());
        int y0 = toBoard(piece.getOldY());
        Tile tile = board[newX][newY];

        if (tile.hasPiece()) {return new MoveResult(MoveType.INVALID);}

        if (!tile.isBlack()) {return new MoveResult(MoveType.INVALID);}

        boolean isKing = piece.getType() == PieceType.BLACKKING || piece.getType() == PieceType.WHITEKING;

        // If the piece is a king, allow multi-tile diagonal moves and captures
        if (isKing) {
            // Check for normal diagonal move (multi-tile)
            if (isMoveDiagonal(x0, y0, newX, newY) && isPathClear(x0, y0, newX, newY)) {
                return new MoveResult(MoveType.NORMAL);
            }

            // Check for diagonal capture for king
            if (Math.abs(newX - x0) >= 2 && Math.abs(newY - y0) >= 2 && isCapturePath(x0, y0, newX, newY)) {
                Piece capturedPiece = getCapturedPieceOnPath(x0, y0, newX, newY);
                return new MoveResult(MoveType.CAPTURE, capturedPiece);
            }
        }

        else {
            //Capture logic for normal pieces
        }
        // Horizontal capture logic for normal pieces
        if (newY == y0 && Math.abs(newX - x0) == 4) {
            int x1 = (newX + x0) / 2;
            Tile halfWay = board[x1][y0];
            if (halfWay.hasPiece() && !halfWay.getPiece().getType().color.equals(piece.getType().color)) {
                return new MoveResult(MoveType.CAPTURE, halfWay.getPiece());
            }
        }

        // Vertical capture logic for normal pieces
        if (newX == x0 && Math.abs(newY - y0) == 4) {
            int y1 = (newY + y0) / 2;
            Tile halfWay = board[x0][y1];
            if (halfWay.hasPiece() && !halfWay.getPiece().getType().color.equals(piece.getType().color)) {
                return new MoveResult(MoveType.CAPTURE, halfWay.getPiece());
            }
        }

        // Diagonal capture logic for normal pieces
        if (Math.abs(newX - x0) == 2 && Math.abs(newY - y0) == 2) {
            int x1 = (newX + x0) / 2;
            int y1 = (newY + y0) / 2;
            Tile halfWay = board[x1][y1];
            if (halfWay.hasPiece() && !halfWay.getPiece().getType().color.equals(piece.getType().color)) {
                return new MoveResult(MoveType.CAPTURE, halfWay.getPiece());
            }
        }

        // Normal diagonal move for regular pieces
        if (isMoveDiagonalNormal(x0, y0, newX, newY) && piece.getType().moveDir == (newY - y0)) {
            return new MoveResult(MoveType.NORMAL);
        }

        return new MoveResult(MoveType.INVALID);
    }


    // Check for king promotion after a move
    private void handleKingPromotion(Piece piece, int newY) {
        if (piece.getType() == PieceType.BLACK && newY == SIZE - 1) {
            piece.promoteToKing();
        } else if (piece.getType() == PieceType.WHITE && newY == 0) {
            piece.promoteToKing();
        }
    }

    private int toBoard(double pixel) {
        return (int) (pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    private Piece makePiece(PieceType type, int x, int y) {
        Piece piece = new Piece(type, x, y);

        piece.setOnMouseReleased(e -> {
            int newX = toBoard(piece.getLayoutX());
            int newY = toBoard(piece.getLayoutY());

            MoveResult result;

            if (newX < 0 || newY < 0 || newX >= SIZE || newY >= SIZE) {
                result = new MoveResult(MoveType.INVALID);
            } else {
                result = tryMove(piece, newX, newY);
            }

            int x0 = toBoard(piece.getOldX());
            int y0 = toBoard(piece.getOldY());

            switch (result.getType()) {
                case INVALID:
                    piece.abortMove();
                    break;
                case NORMAL:
                    resetTimer();
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    // Check if the piece should be promoted to a king
                    handleKingPromotion(piece, newY); // << ADD THIS LINE
                    break;
                case CAPTURE:
                    resetTimer();
                    if (piece.getType().color.equals("black")) {
                        capturedPiecesTracker.incrementWhiteCaptured();
                    } else {
                        capturedPiecesTracker.incrementBlackCaptured();
                    }
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    Piece otherPiece = result.getPiece();
                    board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                    pieceGroup.getChildren().remove(otherPiece);
                    // Check if the piece should be promoted to a king after capture
                    handleKingPromotion(piece, newY); // << ADD THIS LINE
                    break;
            }
        });

        return piece;
    }


    // Helper method to check if move is diagonal for king
    private boolean isMoveDiagonal(int x0, int y0, int newX, int newY) {
        return Math.abs(newX - x0) == Math.abs(newY - y0);
    }

    // Helper method to check if move is diagonal for normal pieces
    private boolean isMoveDiagonalNormal(int x0, int y0, int newX, int newY) {
        return Math.abs(newX - x0) == 1 && Math.abs(newY - y0) == 1;
    }


    // Check if the path for king movement (diagonal, horizontal, vertical) is clear
    private boolean isPathClear(int x0, int y0, int newX, int newY) {
        int dx = Integer.signum(newX - x0);
        int dy = Integer.signum(newY - y0);

        int x = x0 + dx;
        int y = y0 + dy;

        while (x != newX || y != newY) {
            if (board[x][y].hasPiece()) {
                return false;  // Path is blocked
            }
            x += dx;
            y += dy;
        }
        return true;
    }

    // Check if there is a capturable piece on the path
    private boolean isCapturePath(int x0, int y0, int newX, int newY) {
        int dx = Integer.signum(newX - x0);
        int dy = Integer.signum(newY - y0);

        int x = x0 + dx;
        int y = y0 + dy;
        Piece capturedPiece = null;

        while (x != newX || y != newY) {
            if (board[x][y].hasPiece()) {
                if (capturedPiece == null && board[x][y].getPiece().getType() != board[x0][y0].getPiece().getType()) {
                    capturedPiece = board[x][y].getPiece();  // Store the opponent piece for capture
                } else {
                    return false;  // Path is blocked by more than one piece
                }
            }
            x += dx;
            y += dy;
        }

        return capturedPiece != null;  // Return true if there's exactly one piece to capture
    }

    // Return the piece to capture along the path
    private Piece getCapturedPieceOnPath(int x0, int y0, int newX, int newY) {
        int dx = Integer.signum(newX - x0);
        int dy = Integer.signum(newY - y0);

        int x = x0 + dx;
        int y = y0 + dy;

        while (x != newX || y != newY) {
            if (board[x][y].hasPiece() && board[x][y].getPiece().getType() != board[x0][y0].getPiece().getType()) {
                return board[x][y].getPiece();  // Return the capturable piece
            }
            x += dx;
            y += dy;
        }

        return null;  // No capturable piece found
    }

    public static void main(String[] args) {
        launch(args);
    }
}
