package org.testing.project_2_1.Agents;
import org.testing.project_2_1.GameLogic.GameLogic;


import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Random;

import javafx.animation.PauseTransition;

public class AgentMCTS implements Agent {
    private GameLogic gameLogic;
    private boolean isWhite;

    public AgentMCTS(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    @Override
    public boolean isWhite() {
        return isWhite;
    }

    @Override
    public void makeMove() {
        System.out.println("MCTS agent making move");
        PauseTransition pause = new PauseTransition(Duration.seconds(Agent.delay));
        pause.setOnFinished(event -> {
            while (gameLogic.getWhiteTurn == isWhite && !gameLogic.isGameOver()) {
                // TODO: Agent MCTS yet to implement
                // gameLogic.takeTurn(move);
                // gameLogic.evaluateBoard();
            }
        });
        pause.play();
    }

    public Agent reset() {
        return new AgentMCTS(isWhite);
    }

}
