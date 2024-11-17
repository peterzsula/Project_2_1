package org.testing.project_2_1.UI;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

import static org.testing.project_2_1.UI.CheckersApp.SIZE;
import static org.testing.project_2_1.UI.CheckersApp.TILE_SIZE;

import org.testing.project_2_1.GameLogic.Piece;
import org.testing.project_2_1.GameLogic.PieceType;
import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.Turn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PieceDrawer extends StackPane {
    private int oldX, oldY;
    private Piece piece;
    private CheckersApp app;
    private List<Rectangle> highlightedTiles = new ArrayList<>();

    public PieceDrawer(Piece piece, CheckersApp app) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null when creating PieceDrawer."); //bit unnecessary
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
            if (move == null || move.isInvalid()) {
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
        List<Turn> availableTurns = GameLogic.getLegalTurns(this.piece, app.gameLogic.g); 
        List<Move> availableMoves = new ArrayList<>(); // this should be a method in GameLogic
        for (Turn turn : availableTurns) {
            availableMoves.add(turn.getMoves().getFirst());
        }
        if (availableMoves.isEmpty()) {
            return;
        }
        // If capture moves exist, highlight only captures
        if (availableMoves.getFirst().isCapture()) {
            for (Move move : availableMoves) {
                highlightTile(move.getToX(), move.getToY(), Color.RED);
            }
            return;
        }

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
