package org.testing.project_2_1.GameLogic;

import org.testing.project_2_1.UI.TileDrawer;

public class Tile{
    public TileDrawer tileDrawer;
    private boolean isBlack;
    private Piece piece;
    private int x, y;

    public Tile(int x, int y) {
        this.isBlack = (x + y) % 2 == 1;
        this.piece = null;
        this.x = x;
        this.y = y;
        this.tileDrawer = new TileDrawer(this);
    }
    
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
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}