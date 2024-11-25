package org.testing.project_2_1.GameLogic;

import org.testing.project_2_1.UI.TileDrawer;

/**
 * Represents a single tile on the game board.
 * A tile can have a color (black or white) and optionally hold a piece.
 * It also manages the visual representation of the tile through a `TileDrawer`.
 */
public class Tile {
    public TileDrawer tileDrawer; // Visual representation of the tile
    private boolean isBlack; // Indicates if the tile is black
    private Piece piece; // The piece currently occupying the tile, if any
    private int x, y; // The x and y coordinates of the tile on the board

    /**
     * Constructs a `Tile` at the specified coordinates.
     * The color of the tile is determined based on its coordinates.
     *
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     */
    public Tile(int x, int y) {
        this.isBlack = (x + y) % 2 == 1; // Alternating black and white tiles
        this.piece = null;
        this.x = x;
        this.y = y;
        this.tileDrawer = null;
    }

    /**
     * Gets the `TileDrawer` associated with this tile.
     *
     * @return The `TileDrawer`, or null if not set.
     */
    public TileDrawer getTileDrawer() {
        return tileDrawer;
    }

    /**
     * Sets the `TileDrawer` for this tile.
     *
     * @param tileDrawer The `TileDrawer` to associate with this tile.
     */
    public void setTileDrawer(TileDrawer tileDrawer) {
        this.tileDrawer = tileDrawer;
    }

    /**
     * Checks if the tile currently has a piece.
     *
     * @return True if the tile has a piece; false otherwise.
     */
    public boolean hasPiece() {
        return piece != null;
    }

    /**
     * Gets the piece currently on this tile.
     *
     * @return The piece on the tile, or null if the tile is empty.
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Sets the piece on this tile.
     *
     * @param piece The piece to place on the tile.
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    /**
     * Checks if the tile is black.
     *
     * @return True if the tile is black; false otherwise.
     */
    public boolean isBlack() {
        return isBlack;
    }

    /**
     * Gets the x-coordinate of the tile.
     *
     * @return The x-coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the tile.
     *
     * @return The y-coordinate.
     */
    public int getY() {
        return y;
    }
}
