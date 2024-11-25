package org.testing.project_2_1.Moves;

import org.testing.project_2_1.GameLogic.Piece;

/**
 * Represents an invalid move in the game.
 * This move type indicates an action that does not adhere to the game's rules or is otherwise disallowed.
 */
public class InvalidMove extends Move {

    /**
     * Constructs an `InvalidMove` with the specified parameters.
     * Sets the move type to INVALID.
     *
     * @param fromX  The starting x-coordinate of the piece.
     * @param fromY  The starting y-coordinate of the piece.
     * @param piece  The piece that attempted the invalid move.
     * @param toX    The destination x-coordinate of the piece.
     * @param toY    The destination y-coordinate of the piece.
     */
    public InvalidMove(int fromX, int fromY, Piece piece, int toX, int toY) {
        super(fromX, fromY, piece, toX, toY);
        this.type = MoveType.INVALID;
    }

    /**
     * Provides a string representation of the invalid move for debugging purposes.
     *
     * @return A string describing the invalid move, including starting and ending coordinates.
     */
    @Override
    public String toString() {
        return "INVALID Move from X=" + fromX + ", Y=" + fromY + ", to X=" + toX + ", to Y=" + toY;
    }
}
