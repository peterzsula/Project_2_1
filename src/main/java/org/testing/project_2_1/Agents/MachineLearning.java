package org.testing.project_2_1.Agents;
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
        ArrayList<Turn> turns = GameLogic.getLegalTurns(gameLogic.b);
        System.out.println("number of turns " + turns.size());
        for (Turn turn : turns) {
            turn.setEvaluation(gameLogic.evaluateTurn(turn, gameLogic.b));
        }
        Turn bestTurn = getBestTurn(turns);
        System.out.println("taking turn with evaluation " + bestTurn.getEvaluation() + bestTurn.getMoves().get(0).toString());
        gameLogic.takeTurn(bestTurn);
    }

    @Override
    public Agent reset() {
        return new MachineLearning(isWhite);
    }

    private Turn getBestTurn(ArrayList<Turn> turns) {
        Turn bestTurn = turns.get(0);
        if (isWhite) {
            for (Turn turn : turns) {
                if (turn.getEvaluation() > bestTurn.getEvaluation()) {
                    bestTurn = turn;
                }
            }
        }
        else {
            for (Turn turn : turns) {
                if (turn.getEvaluation() < bestTurn.getEvaluation()) {
                    bestTurn = turn;
                }
            }
        }
        return bestTurn;
    }
    
}
