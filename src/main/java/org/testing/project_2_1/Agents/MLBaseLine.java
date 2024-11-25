package org.testing.project_2_1.Agents;
import java.util.List;

import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.GameLogic.GameState;
import org.testing.project_2_1.Moves.*;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class MLBaseLine implements Agent {
    private GameLogic gameLogic;
    private AlphaBetaAgent ABpruning;
    private Turn currentTurn;

    private boolean isWhite;
    private int maxDepth; // used for evaluateturn()

    public MLBaseLine(boolean isWhite) {
        this.isWhite = isWhite;
        this.maxDepth = 3;
        this.ABpruning = new AlphaBetaAgent(isWhite, this.maxDepth);
        currentTurn = new Turn();
    }
    
    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        if (this.ABpruning != null) {
            this.ABpruning.setGameLogic(gameLogic);
        }
    }

    @Override
    public void setGameState(GameState gameState) {
        this.gameLogic.g = gameState;
        if (this.ABpruning != null) {
            this.ABpruning.setGameState(gameState);
        }
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
            List<Turn> turns = gameLogic.g.getLegalTurns();
            for (Turn turn : turns) {
                turn.setEvaluation(evaluateTurn(turn, gameLogic.g, maxDepth, !isWhite));
            }
            if (currentTurn.isEmpty()) {
                currentTurn = getBestTurn(turns);
            }
            Move move = currentTurn.getMoves().remove(0);
            System.out.println("taking turn with evaluation " + currentTurn.getEvaluation() + " " + move.toString());
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

    public double evaluateTurn(Turn turn, GameState originalGS, int depth, boolean isMaxPlayerWhite) {
        GameState g0 = new GameState(originalGS);
        for (Move move : turn.getMoves()) {
            g0.move(move);
        }

        // Calls minimax with alpha-beta pruning from AlphaBetaAgent class
        return ABpruning.minimaxPruning(g0, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaxPlayerWhite);
    }
    @Override
    public void simulate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'simulate'");
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }
    
}
