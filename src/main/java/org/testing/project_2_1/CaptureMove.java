package org.testing.project_2_1;

public class CaptureMove extends NormalMove {

    private Piece capturedPiece;

    public CaptureMove(Piece piece, Piece capturedPiece, int toX, int toY) {
        super(piece, toX, toY);
        this.capturedPiece = capturedPiece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }
}
