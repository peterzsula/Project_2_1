package org.testing.project_2_1.Moves;

import org.testing.project_2_1.GameLogic.Piece;

public class InvalidMove extends Move {
    public InvalidMove(int fromX, int fromY, Piece piece, int toX, int toY) {
        super(fromX, fromY, piece, toX, toY);
        this.type = MoveType.INVALID;
    }

    @Override
    public String toString() {
        return "INVALID Move from X=" + piece.getX() + ", Y=" + piece.getY() + ", to X=" + toX + ", to Y=" + toY;
    }
}
