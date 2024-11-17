package org.testing.project_2_1.UI;

import org.testing.project_2_1.GameLogic.Tile;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TileDrawer extends Rectangle {
    Tile tile;
    CheckersApp app;

    public TileDrawer(Tile tile, CheckersApp app) {
        this.tile = tile;
        this.app = app;
        drawTile();
    }

    public void drawTile(){
        setWidth(CheckersApp.TILE_SIZE);
        setHeight(CheckersApp.TILE_SIZE);

        relocate(tile.getX() * CheckersApp.TILE_SIZE, tile.getY() * CheckersApp.TILE_SIZE);

        setFill(tile.isBlack() ? Color.valueOf("#D2B48C") : Color.valueOf("#FFFAF0"));
    }

    public void highlight() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'highlight'");
    }

    public void clearHighlight() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clearHighlight'");
    }
    
    
}
