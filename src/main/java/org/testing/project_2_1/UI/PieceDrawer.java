package org.testing.project_2_1.UI;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

import static org.testing.project_2_1.UI.CheckersApp.SIZE;
import static org.testing.project_2_1.UI.CheckersApp.TILE_SIZE;

import org.testing.project_2_1.GameLogic.Piece;
import org.testing.project_2_1.GameLogic.PieceType;
import org.testing.project_2_1.Moves.Move;

import java.util.ArrayList;
import java.util.List;

/**
 * The `PieceDrawer` class represents the visual representation of a game piece in Frisian Checkers.
 * It handles drawing the piece, highlighting legal moves, promoting/demoting the piece,
 * and managing user interactions such as dragging and releasing the piece.
 */
public class PieceDrawer extends StackPane {
    private int oldX, oldY; // Stores the original position of the piece before a move
    private Piece piece; // Logical representation of the game piece
    private CheckersApp app; // Reference to the main application
    private List<Rectangle> highlightedTiles = new ArrayList<>(); // Tracks highlighted tiles for legal moves
    private static DropShadow glowEffect; // Effect to indicate pieces with available moves

    static {
        glowEffect = new DropShadow();
        glowEffect.setColor(Color.YELLOW);
        glowEffect.setRadius(20);
    }

    /**
     * Constructs a `PieceDrawer` for a given piece and associates it with the application.
     *
     * @param piece The logical game piece.
     * @param app   The main `CheckersApp` instance.
     */
    public PieceDrawer(Piece piece, CheckersApp app) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null when creating PieceDrawer.");
        }
        this.piece = piece;
        this.app = app;
        app.pieceGroup.getChildren().add(this); // Add this drawer to the application's piece group
        drawPiece(piece.getType(), piece.getX(), piece.getY());
        updateGlow(); // Apply glow effect if the piece has available moves
    }

    /**
     * Draws the game piece with its appearance and position.
     *
     * @param type The type of the piece (e.g., normal or king).
     * @param x    The x-coordinate of the piece.
     * @param y    The y-coordinate of the piece.
     */
    private void drawPiece(PieceType type, int x, int y) {
        move(x, y); // Position the piece

        // Background ellipse (shadow effect)
        Ellipse bg = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
        bg.setFill(Color.BLACK);
        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(TILE_SIZE * 0.03);
        bg.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        bg.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2 + TILE_SIZE * 0.07);

        // Foreground ellipse (piece color)
        Ellipse ellipse = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
        ellipse.setFill(type.color.equals("black")
                ? Color.valueOf("#808080") : Color.valueOf("#fff9f4"));
        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(TILE_SIZE * 0.03);
        ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2);

        getChildren().addAll(bg, ellipse);

        // Add interaction handlers for dragging and releasing the piece
        setOnMousePressed(e -> {
            if (app.noOfPlayers > 0) { // Only handle human player interaction
                oldX = (int) Math.floor(e.getSceneX() / TILE_SIZE);
                oldY = (int) Math.floor(e.getSceneY() / TILE_SIZE);
                highlightMoves(); // Highlight available moves
            }
        });

        setOnMouseDragged(e -> {
            if (app.noOfPlayers > 0) {
                relocate(e.getSceneX() - TILE_SIZE / 2, e.getSceneY() - TILE_SIZE / 2);
            }
        });

        setOnMouseReleased(e -> {
            if (app.noOfPlayers > 0) {
                clearHighlight(); // Clear highlights after releasing the piece

                int newX = (int) Math.floor(e.getSceneX() / TILE_SIZE);
                int newY = (int) Math.floor(e.getSceneY() / TILE_SIZE);

                Move move = app.gameLogic.g.determineMoveType(oldX, oldY, newX, newY);
                if (move == null || move.isInvalid()) {
                    abortMove(); // Revert the piece to its original position
                    return;
                }

                boolean success = app.gameLogic.takeMove(move);
                if (success) {
                    piece.movePiece(move);
                    app.updateGlows(); // Update game state and piece glows
                } else {
                    abortMove();
                }
            }
        });
    }

    /**
     * Highlights the tiles corresponding to the piece's legal moves.
     */
    private void highlightMoves() {
        clearHighlight(); // Remove existing highlights

        if (app.noOfPlayers == 0) {
            return; // Skip highlighting for AI moves
        }

        if (getEffect() != glowEffect) {
            return; // Only highlight pieces with available moves
        }

        List<Move> availableMoves = app.gameLogic.g.getLegalMoves(piece);

        if (availableMoves.isEmpty()) {
            return; // No moves available to highlight
        }

        List<Move> captureMoves = availableMoves.stream()
                .filter(Move::isCapture)
                .toList();

        if (!captureMoves.isEmpty()) {
            for (Move move : captureMoves) {
                highlightTile(move.getToX(), move.getToY(), Color.RED); // Highlight captures in red
            }
            return;
        }

        for (Move move : availableMoves) {
            highlightTile(move.getToX(), move.getToY(), Color.YELLOW); // Highlight normal moves in yellow
        }
    }

    /**
     * Highlights a specific tile on the board.
     *
     * @param x     The x-coordinate of the tile.
     * @param y     The y-coordinate of the tile.
     * @param color The color to highlight the tile with.
     */
    private void highlightTile(int x, int y, Color color) {
        if (x >= 0 && x < SIZE && y >= 0 && y < SIZE) {
            Rectangle highlight = new Rectangle(TILE_SIZE, TILE_SIZE);
            highlight.setFill(color);
            highlight.setOpacity(0.5);
            highlight.relocate(x * TILE_SIZE, y * TILE_SIZE);
            highlightedTiles.add(highlight);
            app.getBoardGroup().getChildren().add(highlight);
        }
    }

    /**
     * Clears all highlighted tiles.
     */
    public void clearHighlight() {
        for (Rectangle highlight : highlightedTiles) {
            app.getBoardGroup().getChildren().remove(highlight);
        }
        highlightedTiles.clear();
    }

    /**
     * Updates the glow effect based on the piece's available moves.
     */
    public void updateGlow() {
        ArrayList<Piece> currentPlayerPieces = app.gameLogic.g.getListOfPieces();
        if (!currentPlayerPieces.contains(piece)) {
            setEffect(null); // Remove glow for inactive pieces
            return;
        }

        List<Move> availableMoves = app.gameLogic.g.getLegalMoves();
        boolean canMove = availableMoves.stream()
                .anyMatch(move -> move.getFromX() == piece.getX() && move.getFromY() == piece.getY());

        setEffect(canMove ? glowEffect : null); // Apply glow if moves are available
    }

    /**
     * Moves the piece to a new position on the board.
     *
     * @param x The new x-coordinate.
     * @param y The new y-coordinate.
     */
    public void move(int x, int y) {
        relocate(x * TILE_SIZE, y * TILE_SIZE);
    }

    /**
     * Reverts the piece to its original position.
     */
    public void abortMove() {
        relocate(piece.getX() * TILE_SIZE, piece.getY() * TILE_SIZE);
    }

    /**
     * Promotes the piece to a king by adding a crown symbol.
     */
    public void promoteToKing() {
        Ellipse crown = new Ellipse(TILE_SIZE * 0.2, TILE_SIZE * 0.1);
        crown.setFill(Color.GOLD);
        crown.setStroke(Color.BLACK);
        crown.setStrokeWidth(TILE_SIZE * 0.03);
        getChildren().add(crown);
    }

    /**
     * Demotes the piece from king status by removing the crown symbol.
     */
    public void demoteToNormal() {
        if (getChildren().size() > 0) {
            getChildren().remove(getChildren().size() - 1);
        }
    }
}
