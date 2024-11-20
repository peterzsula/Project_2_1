package org.testing.project_2_1.Agents;
import java.util.List;

import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.Moves.*;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class MLBaseLine implements Agent {
    private GameLogic gameLogic;
    private boolean isWhite;
    private int maxDepth; // used for evaluateturn()

    public MLBaseLine(boolean isWhite) {
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
        PauseTransition pause = new PauseTransition(Duration.seconds(Agent.delay));
        pause.setOnFinished(event -> {
        if (gameLogic.g.getIsWhiteTurn() == isWhite && !gameLogic.isGameOver(gameLogic.g)) {
            List<Turn> turns = GameLogic.getLegalTurns(gameLogic.g);
            for (Turn turn : turns) {
                turn.setEvaluation(gameLogic.evaluateTurn(turn, gameLogic.g, maxDepth, !isWhite));
            }
            Turn bestTurn = getBestTurn(turns);
            Move move = bestTurn.getMoves().remove(0);
            System.out.println("taking turn with evaluation " + bestTurn.getEvaluation() + " " + move.toString());
            gameLogic.takeMove(move);
        }
    });
    pause.play();
    }

    @Override
    public Agent reset() {
        return new MLBaseLine(isWhite);
    }

    private Turn getBestTurn(List<Turn> turns) {
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

    @Override
    public void simulate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'simulate'");
    }
    
}
