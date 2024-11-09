package org.testing.project_2_1.Moves;

import org.testing.project_2_1.GameLogic.Piece;

public class InvalidMove extends Move {
    public InvalidMove(Piece piece, int toX, int toY) {
        super(piece, toX, toY);
        this.type = MoveType.INVALID;
    }

    @Override
    public String toString() {
        return "INVALID Move from X=" + piece.x + ", Y=" + piece.y + ", to X=" + toX + ", to Y=" + toY;
    }
}