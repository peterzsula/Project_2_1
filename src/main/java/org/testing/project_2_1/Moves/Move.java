package org.testing.project_2_1.Moves;

import org.testing.project_2_1.GameLogic.Piece;

/**
 * Represents a generic move in the game. This class serves as a base class for specific move types,
 * such as normal moves and capture moves. It contains common properties and methods shared across all moves.
 */
public abstract class Move {
    protected Piece piece; // The piece being moved
    protected final int fromX; // Starting x-coordinate of the move
    protected final int fromY; // Starting y-coordinate of the move
    protected final int toX; // Ending x-coordinate of the move
    protected final int toY; // Ending y-coordinate of the move
    protected MoveType type; // The type of the move (e.g., NORMAL, CAPTURE, INVALID)
    protected boolean promotion; // Indicates if the move results in a promotion
    protected boolean isTurnEnding; // Indicates if the move ends the current turn

    /**
     * Constructs a `Move` with the specified parameters.
     *
     * @param fromX  The starting x-coordinate of the piece.
     * @param fromY  The starting y-coordinate of the piece.
     * @param piece  The piece being moved.
     * @param toX    The ending x-coordinate of the piece.
     * @param toY    The ending y-coordinate of the piece.
     */
    public Move(int fromX, int fromY, Piece piece, int toX, int toY) {
        this.piece = piece;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.promotion = false;
    }

    /**
     * Gets the type of the move.
     *
     * @return The move type.
     */
    public MoveType getType() {
        return type;
    }

    /**
     * Gets the x-coordinate of the destination.
     *
     * @return The ending x-coordinate of the move.
     */
    public int getToX() {
        return toX;
    }

    /**
     * Gets the y-coordinate of the destination.
     *
     * @return The ending y-coordinate of the move.
     */
    public int getToY() {
        return toY;
    }

    /**
     * Gets the piece involved in the move.
     *
     * @return The piece being moved.
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Gets the x-coordinate of the starting position.
     *
     * @return The starting x-coordinate of the move.
     */
    public int getFromX() {
        return fromX;
    }

    /**
     * Gets the y-coordinate of the starting position.
     *
     * @return The starting y-coordinate of the move.
     */
    public int getFromY() {
        return fromY;
    }

    /**
     * Sets whether the move results in a promotion.
     *
     * @param promotion True if the move results in a promotion; false otherwise.
     */
    public void setPromotion(boolean promotion) {
        this.promotion = promotion;
    }

    /**
     * Checks if the move ends the player's turn.
     *
     * @return True if the move ends the turn; false otherwise.
     */
    public boolean isTurnEnding() {
        return isTurnEnding;
    }

    /**
     * Sets whether the move ends the player's turn.
     *
     * @param turnEnding True if the move ends the turn; false otherwise.
     */
    public void setTurnEnding(boolean turnEnding) {
        isTurnEnding = turnEnding;
    }

    /**
     * Determines if this move is equal to another object.
     * Two moves are considered equal if their starting and ending coordinates match.
     *
     * @param obj The object to compare.
     * @return True if the object is a `Move` or `Capture` with the same coordinates; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Move) {
            Move move = (Move) obj;
            return move.fromX == fromX && move.fromY == fromY && move.toX == toX && move.toY == toY;
        }
        if (obj instanceof Capture) {
            Capture move = (Capture) obj;
            return move.fromX == fromX && move.fromY == fromY && move.toX == toX && move.toY == toY;
        }
        return false;
    }

    /**
     * Provides a string representation of the move for debugging purposes.
     *
     * @return A string describing the move.
     */
    @Override
    public String toString() {
        if (type == MoveType.INVALID) {
            return "INVALID Move from X=" + fromX + ", Y=" + fromY + ", to X=" + toX + ", to Y=" + toY;
        }
        if (type == MoveType.NORMAL) {
            return "Move from X=" + fromX + ", Y=" + fromY + ", to X=" + toX + ", to Y=" + toY;
        }
        return "INDETERMINED Move from X=" + fromX + ", Y=" + fromY + ", to X=" + toX + ", to Y=" + toY;
    }

    /**
     * Checks if the move is a capture.
     *
     * @return True if the move is a capture; false otherwise.
     */
    public boolean isCapture() {
        return type == MoveType.CAPTURE;
    }

    /**
     * Checks if the move is a normal move.
     *
     * @return True if the move is normal; false otherwise.
     */
    public boolean isNormal() {
        return type == MoveType.NORMAL;
    }

    /**
     * Checks if the move is invalid.
     *
     * @return True if the move is invalid; false otherwise.
     */
    public boolean isInvalid() {
        return type == MoveType.INVALID;
    }

    /**
     * Checks if the move results in a promotion.
     *
     * @return True if the move promotes the piece; false otherwise.
     */
    public boolean isPromotion() {
        return promotion;
    }
}
