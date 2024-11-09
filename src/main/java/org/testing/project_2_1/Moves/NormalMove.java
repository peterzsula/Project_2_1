package org.testing.project_2_1.Moves;

import org.testing.project_2_1.GameLogic.Piece;

public class NormalMove extends Move {
    public NormalMove(int fromX, int fromY, Piece piece, int toX, int toY) {
        super(fromX, fromY, piece, toX, toY);
        this.type = MoveType.NORMAL;
    }

    @Override
    public String toString() {
        return "Valid Move from X=" + piece.getX()+ ", Y=" + piece.getY() + ", to X=" + toX + ", to Y=" + toY;
    }
}
