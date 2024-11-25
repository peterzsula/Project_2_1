package org.testing.project_2_1.Moves;

/**
 * Represents the different types of moves that can occur in the game.
 */
public enum MoveType {
    /**
     * An invalid move that does not adhere to the game's rules.
     */
    INVALID,

    /**
     * A normal move where a piece moves to an empty adjacent tile without capturing.
     */
    NORMAL,

    /**
     * A capture move where a piece jumps over an opponent's piece, removing it from the game.
     */
    CAPTURE
}
