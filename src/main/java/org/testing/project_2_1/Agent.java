package org.testing.project_2_1;

public interface Agent {
    public static final double delay = 0.1;
    public boolean isWhite();
    public void makeMove();
    public void setGameLogic(GameLogic gameLogic);
}
