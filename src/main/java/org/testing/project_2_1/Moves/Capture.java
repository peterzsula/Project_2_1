package org.testing.project_2_1.Moves;

import org.testing.project_2_1.GameLogic.Piece;
import org.testing.project_2_1.GameLogic.PieceType;

public class Capture extends Move {
    private Piece capturedPiece;

    public Capture(Piece piece, Piece capturedPiece, int toX, int toY) {
        super(piece, toX, toY);
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
        return "Move from X=" + piece.x + ", from Y=" + piece.y + ", to X=" + toX + ", to Y=" + toY +
            ", CAPTURE AT X=" + capturedPiece.x + ", Y=" + capturedPiece.y;
    }

    public static void main(String[] args) {
        Piece piece = new Piece(PieceType.BLACK, 0, 0);
        Piece capturedPiece = new Piece(PieceType.WHITE, 1, 1);
        Capture capture = new Capture(piece, capturedPiece, 1, 1);
        Capture capture2 = new Capture(piece, capturedPiece, 1, 1);
        System.out.println(capture.equals(capture2));
    }

    
}
