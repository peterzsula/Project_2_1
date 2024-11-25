package org.testing.project_2_1.Moves;

import org.testing.project_2_1.GameLogic.Piece;

/**
 * Represents a normal move in the game, where a piece moves from one tile to another
 * without capturing any opponent pieces.
 */
public class NormalMove extends Move {

    /**
     * Constructs a `NormalMove` with the specified parameters.
     * Sets the move type to NORMAL and marks the move as turn-ending by default.
     *
     * @param fromX  The starting x-coordinate of the piece.
     * @param fromY  The starting y-coordinate of the piece.
     * @param piece  The piece that is being moved.
     * @param toX    The destination x-coordinate of the piece.
     * @param toY    The destination y-coordinate of the piece.
     */
    public NormalMove(int fromX, int fromY, Piece piece, int toX, int toY) {
        super(fromX, fromY, piece, toX, toY);
        this.type = MoveType.NORMAL; // Set the type of move as NORMAL
        this.isTurnEnding = true; // A normal move ends the player's turn
    }

    /**
     * Provides a string representation of the move for debugging and logging purposes.
     *
     * @return A string describing the move, including starting and ending coordinates.
     */
    @Override
    public String toString() {
        return "Valid Move from X=" + fromX + ", Y=" + fromY + ", to X=" + toX + ", to Y=" + toY;
    }
}
