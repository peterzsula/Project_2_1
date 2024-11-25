package org.testing.project_2_1.Moves;

import org.testing.project_2_1.GameLogic.Piece;

/**
 * Represents a capture move in the game, where a piece captures an opponent's piece.
 * This move involves jumping over the captured piece and removing it from the board.
 */
public class Capture extends Move {
    private Piece capturedPiece; // The piece that is being captured
    private final int captureAtX; // The x-coordinate of the captured piece
    private final int captureAtY; // The y-coordinate of the captured piece

    /**
     * Constructs a `Capture` move with the specified parameters.
     * Sets the move type to CAPTURE and initializes the coordinates of the captured piece.
     *
     * @param fromX         The starting x-coordinate of the piece making the move.
     * @param fromY         The starting y-coordinate of the piece making the move.
     * @param piece         The piece making the capture.
     * @param capturedPiece The piece being captured.
     * @param toX           The destination x-coordinate of the capturing piece.
     * @param toY           The destination y-coordinate of the capturing piece.
     */
    public Capture(int fromX, int fromY, Piece piece, Piece capturedPiece, int toX, int toY) {
        super(fromX, fromY, piece, toX, toY);
        this.capturedPiece = capturedPiece;
        this.type = MoveType.CAPTURE;
        this.captureAtX = capturedPiece.getX();
        this.captureAtY = capturedPiece.getY();
    }

    /**
     * Gets the piece that is being captured.
     *
     * @return The captured piece.
     */
    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    /**
     * Gets the x-coordinate of the captured piece.
     *
     * @return The x-coordinate of the captured piece.
     */
    public int getCaptureAtX() {
        return captureAtX;
    }

    /**
     * Gets the y-coordinate of the captured piece.
     *
     * @return The y-coordinate of the captured piece.
     */
    public int getCaptureAtY() {
        return captureAtY;
    }

    /**
     * Determines if this capture move is equal to another object.
     * Two capture moves are considered equal if their starting and ending coordinates match.
     *
     * @param obj The object to compare.
     * @return True if the object is a `Capture` move with the same coordinates; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Capture) {
            Capture other = (Capture) obj;
            return other.getToX() == toX && other.getToY() == toY && 
                   other.getFromX() == fromX && other.getFromY() == fromY;
        }
        return false;
    }

    /**
     * Provides a string representation of the capture move for debugging purposes.
     *
     * @return A string describing the move, including the coordinates of the capture.
     */
    @Override
    public String toString() {
        return "Move from X=" + fromX + ", from Y=" + fromY + ", to X=" + toX + ", to Y=" + toY +
            ", CAPTURE AT X=" + captureAtX + ", Y=" + captureAtY;
    }
}
