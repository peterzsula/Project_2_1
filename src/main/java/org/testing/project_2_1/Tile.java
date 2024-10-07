package org.testing.project_2_1;


import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
public class Tile extends Rectangle {
    private boolean isBlack;
    private int x;
    private int y;

    private Piece piece;

    public boolean hasPiece() {
        return piece != null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public Tile(int x, int y) {
        this.isBlack = (x + y) % 2 == 1;
        this.x = x;
        this.y = y;
        setWidth(CheckersApp.TILE_SIZE);
        setHeight(CheckersApp.TILE_SIZE);

        relocate(x * CheckersApp.TILE_SIZE, y * CheckersApp.TILE_SIZE);

        setFill(isBlack ? Color.valueOf("#D2B48C") : Color.valueOf("#FFFAF0"));
    }
}