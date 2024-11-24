package org.testing.project_2_1.Agents;
import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.GameLogic.GameState;

public interface Agent {
    public static final double delay = 0.05;
    public boolean isWhite();
    public void makeMove();
    public void simulate();
    public void setGameLogic(GameLogic gameLogic);
    public void pause();
    public Agent reset();
    public void setGameState(GameState gameState);
}
