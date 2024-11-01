package org.testing.project_2_1;

public class NormalMove extends Move {
    public NormalMove(Piece piece, int toX, int toY) {
        super(piece, toX, toY);
        this.type = MoveType.NORMAL;
    }

    @Override
    public String toString() {
        return "Valid Move from X=" + piece.x + ", Y=" + piece.y + ", to X=" + toX + ", to Y=" + toY;
    }
}
