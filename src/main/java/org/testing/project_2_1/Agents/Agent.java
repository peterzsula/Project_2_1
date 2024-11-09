package org.testing.project_2_1.Agents;
import org.testing.project_2_1.GameLogic.GameLogic;

public interface Agent {
    public static final double delay = 0.1;
    public boolean isWhite();
    public void makeMove();
    public void setGameLogic(GameLogic gameLogic);
}
