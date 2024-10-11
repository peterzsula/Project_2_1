package org.testing.project_2_1;

public class MoveResult {
    private MoveType type;
    private Capture capture;

    public MoveType getType() {
        return type;
    }

    public Piece getPieceTaken() {
        return capture.getPiece();
    }

    public Capture getCapture() {
        return capture;
    }

    public MoveResult(MoveType type) {
        this(type, null);
    }

    public MoveResult(MoveType type, Capture capture) {
        this.type = type;
        this.capture = capture;
    }

    @Override
    public String toString() {
        return "MoveResult [type=" + type + "]";
    }

    
}