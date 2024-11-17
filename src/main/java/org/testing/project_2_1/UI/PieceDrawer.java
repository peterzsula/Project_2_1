package org.testing.project_2_1.UI;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

import static org.testing.project_2_1.UI.CheckersApp.TILE_SIZE;

import org.testing.project_2_1.GameLogic.Piece;
import org.testing.project_2_1.GameLogic.PieceType;
import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.Moves.Move;

import java.util.ArrayList;
import java.util.List;

public class PieceDrawer extends StackPane {
    private double mouseX, mouseY;
    private int oldX, oldY;
    private Piece piece;
    private CheckersApp app;
    private List<Rectangle> highlightedTiles = new ArrayList<>();

    public PieceDrawer(Piece piece, CheckersApp app) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null when creating PieceDrawer.");
        }
        this.piece = piece;
        this.app = app;
        drawPiece(piece.getType(), piece.getX(), piece.getY());
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
        ellipse.setFill(type == PieceType.BLACK
                ? Color.valueOf("#808080") : Color.valueOf("#fff9f4"));
        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(TILE_SIZE * 0.03);
        ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2);

        getChildren().addAll(bg, ellipse);

        setOnMousePressed(e -> {
            oldX = (int) Math.floor(e.getSceneX() / TILE_SIZE);
            oldY = (int) Math.floor(e.getSceneY() / TILE_SIZE);
            highlightMoves();
        });

        setOnMouseDragged(e -> {
            relocate(e.getSceneX() - TILE_SIZE / 2, e.getSceneY() - TILE_SIZE / 2);
        });

        setOnMouseReleased(e -> {
            int newX = (int) Math.floor(e.getSceneX() / TILE_SIZE);
            int newY = (int) Math.floor(e.getSceneY() / TILE_SIZE);

            Move move = app.gameLogic.g.determineMoveType(oldX, oldY, newX, newY);
            if (move == null || move instanceof org.testing.project_2_1.Moves.InvalidMove) {
                System.out.println("Invalid move attempted.");
                abortMove();
                return;
            }

            boolean success = app.gameLogic.takeMove(move);
            if (success) {
                piece.movePiece(move);
            } else {
                System.out.println("Move failed.");
                abortMove();
            }
            clearHighlight();
        });
    }

    private void highlightMoves() {
        clearHighlight();

        // Get all pieces with possible moves
        List<Piece> movablePieces = GameLogic.getMovablePieces(app.gameLogic.g);

        // Iterate through the pieces to prioritize captures
        List<Move> captureMoves = new ArrayList<>();
        for (Piece movablePiece : movablePieces) {
            List<Move> moves = GameLogic.getLegalMoves(movablePiece, app.gameLogic.g);
            for (Move move : moves) {
                if (move.isCapture()) {
                    captureMoves.add(move);
                }
            }
        }

        // If capture moves exist, highlight only captures
        if (!captureMoves.isEmpty()) {
            for (Move move : captureMoves) {
                highlightTile(move.getToX(), move.getToY(), Color.RED);
            }
            return;
        }

        // Highlight normal moves if no captures are available
        for (Piece movablePiece : movablePieces) {
            List<Move> moves = GameLogic.getLegalMoves(movablePiece, app.gameLogic.g);
            for (Move move : moves) {
                if (move.isNormal()) {
                    highlightTile(move.getToX(), move.getToY(), Color.YELLOW);
                }
            }
        }
    }

    private void highlightTile(int x, int y, Color color) {
        if (x >= 0 && x < 10 && y >= 0 && y < 10) { // Assuming a 10x10 board
            Rectangle highlight = new Rectangle(TILE_SIZE, TILE_SIZE);
            highlight.setFill(color);
            highlight.setOpacity(0.5);

            highlight.relocate(x * TILE_SIZE, y * TILE_SIZE);

            highlightedTiles.add(highlight);
            app.getBoardGroup().getChildren().add(highlight);
        }
    }

    private void clearHighlight() {
        for (Rectangle highlight : highlightedTiles) {
            app.getBoardGroup().getChildren().remove(highlight);
        }
        highlightedTiles.clear();
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
