package org.testing.project_2_1.GameLogic;

public enum PieceType {
    BLACK(1, "black"),
    WHITE(-1, "white"),
    BLACKKING(0, "black"),
    WHITEKING(0, "white");

    public final int moveDir;
    public final String color;

    PieceType(int moveDir, String color) {
        this.moveDir = moveDir;
        this.color = color;
    }
    public String getColor(){
        return color;
    }
}