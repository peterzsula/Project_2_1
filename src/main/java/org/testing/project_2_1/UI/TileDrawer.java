package org.testing.project_2_1.UI;

import org.testing.project_2_1.GameLogic.Tile;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TileDrawer extends Rectangle {
    Tile tile;
    CheckersApp app;
    private boolean isHighlighted = false; // Tracks the highlight state

    public TileDrawer(Tile tile, CheckersApp app) {
        this.tile = tile;
        this.app = app;
        drawTile();
    }

    public void drawTile() {
        setWidth(CheckersApp.TILE_SIZE);
        setHeight(CheckersApp.TILE_SIZE);

        relocate(tile.getX() * CheckersApp.TILE_SIZE, tile.getY() * CheckersApp.TILE_SIZE);

        // Set the default tile color based on its type
        setFill(tile.isBlack() ? Color.valueOf("#D2B48C") : Color.valueOf("#FFFAF0"));
        setStroke(null); // Ensure no border by default
    }

    public void highlight() {
        // Change the appearance of the tile to indicate a highlight
        if (!isHighlighted) {
            setStroke(Color.RED); // Add a red border
            setStrokeWidth(3);
            isHighlighted = true;
        }
    }

    public void clearHighlight() {
        // Revert the appearance of the tile to its default state
        if (isHighlighted) {
            setStroke(null); // Remove the border
            isHighlighted = false;
        }
    }
}
