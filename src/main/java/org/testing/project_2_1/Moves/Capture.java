package org.testing.project_2_1.Moves;

import org.testing.project_2_1.GameLogic.Piece;

public class Capture extends Move {
    private Piece capturedPiece;

    public Capture(int fromX, int fromY, Piece piece, Piece capturedPiece, int toX, int toY) {
        super(fromX, fromY, piece, toX, toY);
        this.capturedPiece = capturedPiece;
        this.type = MoveType.CAPTURE;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Capture) {
            Capture other = (Capture) obj;
            return other.getToX() == toX && other.getToY() == toY && other.getFromX() == fromX && other.getFromY() == fromY;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Move from X=" + piece.getX() + ", from Y=" + piece.getY() + ", to X=" + toX + ", to Y=" + toY +
            ", CAPTURE AT X=" + capturedPiece.getX() + ", Y=" + capturedPiece.getY();
    }
    
}
