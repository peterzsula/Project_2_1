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
        if (getClass() != obj.getClass())
            return false;
        Capture other = (Capture) obj;
        if (piece == null) {
            if (other.piece != null)
                return false;
        } else if (!piece.equals(other.piece))
            return false;
        if (capturedPiece == null) {
            if (other.capturedPiece != null)
                return false;
        } else if (!capturedPiece.equals(other.capturedPiece))
            return false;
        if (toX != other.toX)
            return false;
        if (toY != other.toY)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Move from X=" + piece.getX() + ", from Y=" + piece.getY() + ", to X=" + toX + ", to Y=" + toY +
            ", CAPTURE AT X=" + capturedPiece.getX() + ", Y=" + capturedPiece.getY();
    }
    
}
