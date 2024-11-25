package org.testing.project_2_1.GameLogic;

/**
 * Represents the different types of pieces in the game.
 * Each piece has a direction of movement and an associated color.
 */
public enum PieceType {
    /**
     * A regular black piece that moves in the positive direction.
     */
    BLACK(1, "black"),

    /**
     * A regular white piece that moves in the negative direction.
     */
    WHITE(-1, "white"),

    /**
     * A black king piece that can move in any direction.
     */
    BLACKKING(0, "black"),

    /**
     * A white king piece that can move in any direction.
     */
    WHITEKING(0, "white");

    /**
     * The movement direction of the piece.
     * - Regular black pieces move in the positive direction (+1).
     * - Regular white pieces move in the negative direction (-1).
     * - King pieces can move in any direction (0).
     */
    public final int moveDir;

    /**
     * The color of the piece, either "black" or "white".
     */
    public final String color;

    /**
     * Constructs a `PieceType` with the specified movement direction and color.
     *
     * @param moveDir The movement direction of the piece.
     * @param color   The color of the piece.
     */
    PieceType(int moveDir, String color) {
        this.moveDir = moveDir;
        this.color = color;
    }

    /**
     * Gets the color of the piece.
     *
     * @return The color of the piece ("black" or "white").
     */
    public String getColor() {
        return color;
    }
}
