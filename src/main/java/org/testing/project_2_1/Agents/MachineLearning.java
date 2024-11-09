package org.testing.project_2_1.Agents;
import java.lang.reflect.Array;
import java.util.ArrayList;

import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.Moves.*;

public class MachineLearning implements Agent {
    private GameLogic gameLogic;
    private boolean isWhite;

    public MachineLearning(boolean isWhite) {
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
        System.out.println("MachineLearning agent making move");
        ArrayList<Move> moves = gameLogic.getLegalMoves();
        for (Move move : moves) {
            move.setEvaluation(gameLogic.evaluateMove(move));
        }
    }
    
}
