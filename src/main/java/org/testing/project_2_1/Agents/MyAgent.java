package org.testing.project_2_1.Agents;

import org.testing.project_2_1.GameLogic.GameLogic;

public class MyAgent implements Agent {
    private GameLogic gameLogic;
    private boolean isWhite;

    public MyAgent(boolean isWhite) {
        this.isWhite = isWhite;
    }

    @Override
    public boolean isWhite() {
        return isWhite;
    }

    @Override
    public void makeMove() {
        if (gameLogic != null) {
            System.out.println("Agent is making a move.");
        } else {
            System.out.println("GameLogic is not initialized!");
        }
    }

    @Override
    public void simulate() {
        System.out.println("Agent is simulating a move.");
    }

    @Override
    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    @Override
    public Agent reset() {
        // Clears any internal state of the agent
        this.gameLogic = null;
        System.out.println("Agent has been reset.");
        return this;
    }
}
