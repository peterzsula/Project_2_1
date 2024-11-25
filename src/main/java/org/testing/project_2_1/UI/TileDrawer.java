package org.testing.project_2_1.UI;

import org.testing.project_2_1.GameLogic.Tile;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * A class that represents the visual representation of a game tile in a checkers game.
 * Extends the `Rectangle` class to allow customization of tile appearance and positioning.
 * Provides functionality to draw the tile and manage highlighting for user interaction.
 */
public class TileDrawer extends Rectangle {
    private Tile tile; // The logical representation of the tile
    private CheckersApp app; // Reference to the main application
    private boolean isHighlighted = false; // Tracks the highlight state of the tile

    /**
     * Constructs a `TileDrawer` for a specific tile in the game.
     * 
     * @param tile The logical tile object to represent visually.
     * @param app  The main CheckersApp instance for context.
     */
    public TileDrawer(Tile tile, CheckersApp app) {
        this.tile = tile;
        this.app = app;
        drawTile(); // Initialize the visual representation of the tile
    }

    /**
     * Draws the tile by setting its size, position, and default appearance.
     */
    public void drawTile() {
        // Set the size of the tile based on the application's tile size
        setWidth(CheckersApp.TILE_SIZE);
        setHeight(CheckersApp.TILE_SIZE);

        // Position the tile based on its logical coordinates
        relocate(tile.getX() * CheckersApp.TILE_SIZE, tile.getY() * CheckersApp.TILE_SIZE);

        // Set the default color based on whether the tile is black or white
        setFill(tile.isBlack() ? Color.valueOf("#D2B48C") : Color.valueOf("#FFFAF0"));

        // Ensure no border by default
        setStroke(null);
    }

    /**
     * Highlights the tile by adding a red border, indicating it's selected or active.
     */
    public void highlight() {
        if (!isHighlighted) {
            setStroke(Color.RED); // Add a red border
            setStrokeWidth(3); // Set the border thickness
            isHighlighted = true; // Mark the tile as highlighted
        }
    }

    /**
     * Clears the highlight of the tile by removing the border and resetting its appearance.
     */
    public void clearHighlight() {
        if (isHighlighted) {
            setStroke(null); // Remove the border
            isHighlighted = false; // Mark the tile as no longer highlighted
        }
    }
}
