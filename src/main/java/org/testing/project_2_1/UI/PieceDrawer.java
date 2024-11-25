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

public class PieceDrawer extends StackPane {
    private int oldX, oldY;
    private Piece piece;
    private CheckersApp app;
    private List<Rectangle> highlightedTiles = new ArrayList<>();
    private static DropShadow glowEffect; 

    static {
        glowEffect = new DropShadow();
        glowEffect.setColor(Color.YELLOW);
        glowEffect.setRadius(20);
    }

    public Piece getPiece() {
        return piece;
    }

    public PieceDrawer(Piece piece, CheckersApp app) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null when creating PieceDrawer.");
        }
        this.piece = piece;
        this.app = app;
        app.pieceGroup.getChildren().add(this);
        drawPiece(piece.getType(), piece.getX(), piece.getY());
        updateGlow(); // Apply glow if this piece has possible moves
    }

    private void drawPiece(PieceType type, int x, int y) {
        move(x, y);

        Ellipse bg = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
        bg.setFill(Color.BLACK);
        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(TILE_SIZE * 0.03);
        bg.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        bg.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2 + TILE_SIZE * 0.07);

        Ellipse ellipse = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
        ellipse.setFill(type.color == "black"
                ? Color.valueOf("#808080") : Color.valueOf("#fff9f4"));
        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(TILE_SIZE * 0.03);
        ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2);

        getChildren().addAll(bg, ellipse);

        // Highlight moves only if the human player is interacting
        setOnMousePressed(e -> {
            if (app.noOfPlayers > 0) { // Human player interacting
                oldX = (int) Math.floor(e.getSceneX() / TILE_SIZE);
                oldY = (int) Math.floor(e.getSceneY() / TILE_SIZE);

                // Highlight moves
                highlightMoves();
            }
        });

        setOnMouseDragged(e -> {
            if (app.noOfPlayers > 0) { // Human player interacting
                relocate(e.getSceneX() - TILE_SIZE / 2, e.getSceneY() - TILE_SIZE / 2);
            }
        });

        setOnMouseReleased(e -> {
            if (app.noOfPlayers > 0) { // Human player interacting
                clearHighlight(); 
        
                int newX = (int) Math.floor(e.getSceneX() / TILE_SIZE);
                int newY = (int) Math.floor(e.getSceneY() / TILE_SIZE);
        
                Move move = app.gameLogic.g.determineMoveType(oldX, oldY, newX, newY);
                if (move == null || move.isInvalid()) {
                    abortMove();
                    return;
                }
        
                boolean success = app.gameLogic.takeMove(move);
                if (success) {
                    piece.movePiece(move);
        
                    // Force game state update and glow recalculation
                    app.updateGlows();
                } else {
                    abortMove();
                }
            }
        });
        
        
    }

    private void highlightMoves() {
        clearHighlight(); // Remove any existing highlights
    
        if (app.noOfPlayers == 0) { // Do not highlight moves for agents
            return;
        }
    
        // Ensure the piece is glowing (has available moves)
        if (getEffect() != glowEffect) {
            return;
        }
    
        // Get all legal moves for this piece
        List<Move> availableMoves = app.gameLogic.g.getLegalMoves(piece);
    
        if (availableMoves.isEmpty()) {
            return; // No moves to highlight
        }
    
        // Check if there are any capture moves
        List<Move> captureMoves = availableMoves.stream()
                .filter(Move::isCapture)
                .toList();
    
        if (!captureMoves.isEmpty()) {
            // Highlight only capture moves in red
            for (Move move : captureMoves) {
                highlightTile(move.getToX(), move.getToY(), Color.RED);
            }
            return; // Exit early to avoid highlighting normal moves
        }
    
        //Highlight normal moves in yellow if no capture moves exist
        for (Move move : availableMoves) {
            highlightTile(move.getToX(), move.getToY(), Color.YELLOW);
        }
    }
    

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

    public void clearHighlight() {
        for (Rectangle highlight : highlightedTiles) {
            app.getBoardGroup().getChildren().remove(highlight);
        }
        highlightedTiles.clear();
    }

    public void updateGlow() {
    
        // Ensure the piece belongs to the current player
        ArrayList<Piece> currentPlayerPieces = app.gameLogic.g.getListOfPieces();
        if (!currentPlayerPieces.contains(piece)) {
            setEffect(null); 
            return;
        }
    
        // Check if the piece has available moves
        List<Move> availableMoves = app.gameLogic.g.getLegalMoves();
        boolean canMove = availableMoves.stream()
            .anyMatch(move -> move.getFromX() == piece.getX() && move.getFromY() == piece.getY());
    
        if (canMove) {
            setEffect(glowEffect); 
        } else {
            setEffect(null); 
        }
    }
 
    public void move(int x, int y) {
        relocate(x * TILE_SIZE, y * TILE_SIZE);
    }

    public void abortMove() {
        relocate(piece.getX() * TILE_SIZE, piece.getY() * TILE_SIZE);
    }

    public void promoteToKing() {
        Ellipse crown = new Ellipse(TILE_SIZE * 0.2, TILE_SIZE * 0.1);
        crown.setFill(Color.GOLD);
        crown.setStroke(Color.BLACK);
        crown.setStrokeWidth(TILE_SIZE * 0.03);
        getChildren().add(crown);
    }

    public void demoteToNormal() {
        if (getChildren().size() > 0) {
            getChildren().remove(getChildren().size() - 1);
        }
    }
}
