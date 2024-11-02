package org.testing.project_2_1;

public abstract class Move {
    protected Piece piece;
    protected int toX;
    protected int toY;
    protected MoveType type;
    
    public Move(Piece piece, int toX, int toY) {
        this.piece = piece;
        this.toX = toX;
        this.toY = toY;
    }

    public MoveType getType() {
        return type;
    }

    public int getToX() {
        return toX;
    }

    public int getToY() {
        return toY;
    }

    public Piece getPiece() {
        return piece;
    }

    @Override
    public String toString() {
        if (type == MoveType.INVALID) {
            return "INVALID Move from X=" + piece.x + ", Y=" + piece.y + ", to X=" + toX + ", to Y=" + toY;
        }
        if (type == MoveType.NORMAL) {
            return "Move from X=" + piece.x + ", Y=" + piece.y + ", to X=" + toX + ", to Y=" + toY;
        }
        return "INDERTERMINED Move from X=" + piece.x + ", Y=" + piece.y;
        
    }
    
}