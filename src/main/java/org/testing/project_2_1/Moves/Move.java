package org.testing.project_2_1.Moves;

import org.testing.project_2_1.GameLogic.Piece;

public abstract class Move {
    protected Piece piece;
    protected final int fromX;
    protected final int fromY;
    protected final int toX;
    protected final int toY;
    protected MoveType type;
    protected boolean promotion;
    protected boolean isTurnEnding;
    
    public Move(int fromX, int fromY, Piece piece, int toX, int toY) {
        this.piece = piece;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.promotion = false;
    }

    public MoveType getType() {
        return type;
    }

    public int getToX() {
        return toX;
    }

    public int getToY() {
        return toY;
    }

    public Piece getPiece() {
        return piece;
    }

    public int getFromX() {
        return fromX;
    }

    public int getFromY() {
        return fromY;
    }

    public void setPromotion(boolean promotion) {
        this.promotion = promotion;
    }

    public boolean isTurnEnding() {
        return isTurnEnding;
    }

    public void setTurnEnding(boolean turnEnding) {
        isTurnEnding = turnEnding;
    }

    @Override
    public String toString() {
        if (type == MoveType.INVALID) {
            return "INVALID Move from X=" + fromX + ", Y=" + fromY + ", to X=" + toX + ", to Y=" + toY;
        }
        if (type == MoveType.NORMAL) {
            return "Move from X=" + fromX + ", Y=" + fromY + ", to X=" + toX + ", to Y=" + toY;
        }
        return "INDERTERMINED Move from X=" + fromX + ", Y=" + fromY + ", to X=" + toX + ", to Y=" + toY;
        
    }

    public boolean isCapture() {
        return type == MoveType.CAPTURE;
    }

    public boolean isNormal() {
        return type == MoveType.NORMAL;
    }

    public boolean isInvalid() {
        return type == MoveType.INVALID;
    }

    public boolean isPromotion() {
        return promotion;
    }
    
}