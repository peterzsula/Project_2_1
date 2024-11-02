package org.testing.project_2_1;

import java.util.ArrayList;
import java.util.Random;

public class BaselineAgent implements Agent {
    private GameLogic gameLogic;

    public BaselineAgent() {
    }
    
    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    @Override
    public void makeMove() {
        System.out.println("Baseline agent making move");
        Random random = new Random();
        Move move = null;
        while (gameLogic.isWhiteTurn == gameLogic.isAgentWhite) {
            ArrayList<Move> legalMoves = gameLogic.getLegalMoves();
            int randomIndex = random.nextInt(legalMoves.size());
            move = legalMoves.get(randomIndex);
            gameLogic.takeTurn(move);
        }
    }

    
}
