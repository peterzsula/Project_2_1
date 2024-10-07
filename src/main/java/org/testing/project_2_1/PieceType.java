package org.testing.project_2_1;

public enum PieceType {
    BLACK(1, "black"),
    WHITE(-1, "white"),
    BLACKKING(0, "black"),
    WHITEKING(0, "white");

    final int moveDir;
    final String color;

    PieceType(int moveDir, String color) {
        this.moveDir = moveDir;
        this.color = color;
    }
    public String getColor(){
        return color;
    }
}