package org.testing.project_2_1;

public class Capture {
    public Piece piece;
    public int toX;
    public int toY;

    public Capture(Piece piece, int toX, int toY) {
        this.piece = piece;
        this.toX = toX;
        this.toY = toY;
    }

    public Piece getPiece() {
        return piece;
    }

    public int getToX() {
        return toX;
    }

    public int getToY() {
        return toY;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass())
            return false;
        Capture other = (Capture) obj;
        if (piece == null) {
            if (other.piece != null)
                return false;
        } else if (!piece.equals(other.piece))
            return false;
        if (toX != other.toX)
            return false;
        if (toY != other.toY)
            return false;
        return true;
    }
}
