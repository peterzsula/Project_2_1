package org.testing.project_2_1.Moves;

import org.testing.project_2_1.GameLogic.Piece;

public class Capture extends Move {
    private Piece capturedPiece;
    private final int captureAtX;
    private final int captureAtY;

    public Capture(int fromX, int fromY, Piece piece, Piece capturedPiece, int toX, int toY) {
        super(fromX, fromY, piece, toX, toY);
        this.capturedPiece = capturedPiece;
        this.type = MoveType.CAPTURE;
        this.captureAtX = capturedPiece.getX();
        this.captureAtY = capturedPiece.getY();
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public int getCaptureAtX() {
        return captureAtX;
    }

    public int getCaptureAtY() {
        return captureAtY;
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
        return "Move from X=" + fromX + ", from Y=" + fromY + ", to X=" + toX + ", to Y=" + toY +
            ", CAPTURE AT X=" + captureAtX + ", Y=" + captureAtY;
    }
    
}
