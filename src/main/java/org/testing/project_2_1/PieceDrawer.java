package org.testing.project_2_1;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import static org.testing.project_2_1.CheckersApp.TILE_SIZE;

public class PieceDrawer extends StackPane {
    public double mouseX, mouseY;
    int x, y;
    Piece piece;
    CheckersApp app;

    public PieceDrawer(Piece piece, CheckersApp app) {
        this.piece = piece;
        this.app = app;
        x = piece.x*TILE_SIZE;
        y = piece.y*TILE_SIZE;
        drawPiece(piece.type, piece.x, piece.y);
    }

    public void drawPiece(PieceType type, int x, int y) {

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
            mouseX = e.getSceneX() - TILE_SIZE / 2;
            mouseY = e.getSceneY() - TILE_SIZE / 2;
        });

        setOnMouseDragged(e -> {
            relocate(e.getSceneX() - TILE_SIZE / 2, e.getSceneY() - TILE_SIZE / 2);
            int newX = (int) Math.floor(e.getSceneX() / TILE_SIZE);
            int newY = (int) Math.floor(e.getSceneY() / TILE_SIZE);
            app.movePiece(piece, newX, newY);
        });
    }

    public void move(int x, int y) {
        x = x * TILE_SIZE;
        y = y * TILE_SIZE;
        relocate(x, y);
    }

    public void abortMove() {
        x = piece.x*TILE_SIZE;
        y = piece.y*TILE_SIZE;
        relocate(x, y);
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public int toBoardX() {
        return (int) (mouseX + TILE_SIZE / 2) / TILE_SIZE;
    }

    public int toBoardY() {
        return (int) (mouseY + TILE_SIZE / 2) / TILE_SIZE;
    }

    public void promoteToKing() {
        Ellipse crown = new Ellipse(TILE_SIZE * 0.2, TILE_SIZE * 0.1);
            crown.setFill(Color.GOLD);
            crown.setStroke(Color.BLACK);
            crown.setStrokeWidth(TILE_SIZE * 0.03);
            getChildren().add(crown);
    }
    
}
