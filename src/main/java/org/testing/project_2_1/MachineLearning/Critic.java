package org.testing.project_2_1.MachineLearning;
import java.util.List;

import org.testing.project_2_1.Agents.Agent;
import org.testing.project_2_1.Agents.AlphaBetaAgent;
import org.testing.project_2_1.GameLogic.*;
import org.testing.project_2_1.Moves.*;

public class Critic {
    GameState pastGame;
    GameState currentGame;
    Agent agent;

    public Critic(GameState gamestate){
        this.pastGame = gamestate;
        agent = new AlphaBetaAgent(true, gamestate, 5);
    }

    public void skipToRandomMove(){
        currentGame = new GameState();
        Turn currentTurn = pastGame.getTurnsPlayed().get(0);
        while (currentTurn.isRandomChoice() == false) {
            for (Move move : currentTurn.getMoves()) {
                currentGame.move(move);
            }
        }
    }

    public Turn findBetterTurn(){
        // TODO: find a better turn
        return null;
    }

}
