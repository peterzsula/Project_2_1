package org.testing.project_2_1.UI;

import org.testing.project_2_1.GameLogic.Tile;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TileDrawer extends Rectangle {
    Tile tile;
    public TileDrawer(Tile tile){
        setWidth(CheckersApp.TILE_SIZE);
        setHeight(CheckersApp.TILE_SIZE);

        relocate(tile.getX() * CheckersApp.TILE_SIZE, tile.getY() * CheckersApp.TILE_SIZE);

        setFill(tile.isBlack() ? Color.valueOf("#D2B48C") : Color.valueOf("#FFFAF0"));
    }
    
    
}
