package org.testing.project_2_1.Moves;

import org.testing.project_2_1.GameLogic.Piece;

public class InvalidMove extends Move {
    public InvalidMove(int fromX, int fromY, Piece piece, int toX, int toY) {
        super(fromX, fromY, piece, toX, toY);
        this.type = MoveType.INVALID;
    }

    @Override
    public String toString() {
        return "INVALID Move from X=" + fromX + ", Y=" + fromY + ", to X=" + toX + ", to Y=" + toY;
    }
}
