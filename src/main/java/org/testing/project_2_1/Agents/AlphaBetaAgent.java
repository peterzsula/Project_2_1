package org.testing.project_2_1.Agents;
import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.GameLogic.GameState;
import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.Turn;

import java.util.ArrayList;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class AlphaBetaAgent implements Agent { //for now uses minimax algorithm, still not ab-pruning
    private GameLogic gameLogic;
    private boolean isWhite;
    public boolean isWhiteTurn;

    private static final int maxDepth = 3;

    // TODO: add parameter for depth
    public AlphaBetaAgent(boolean isWhite) {
        this.isWhite=isWhite;
    }

    public void setGameLogic(GameLogic gameLogic) { this.gameLogic = gameLogic; }
    @Override
    public boolean isWhite() {
        return isWhite;
    }

    @Override
    public void makeMove() {
        System.out.println("Alpha-Beta agent making move");
        PauseTransition pause = new PauseTransition(Duration.seconds(Agent.delay));
        pause.setOnFinished(event -> {
            if (gameLogic.g.getIsWhiteTurn() == isWhite && !gameLogic.isGameOver(gameLogic.g)) {
                ArrayList<Turn> turns = GameLogic.getLegalTurns(gameLogic.g);
                Turn bestTurn = getBestTurn(turns);
                Move move = bestTurn.getMoves().remove(0);
                System.out.println("Takes turn with move " + move);
                gameLogic.takeMove(move);
            }
        });
        pause.play();
    }

    private Turn getBestTurn(ArrayList<Turn> turns) {
        Turn bestTurn = null;
        int bestValue;
        if (isWhite) {
            bestValue = Integer.MIN_VALUE;
        } else {
            bestValue = Integer.MAX_VALUE;
        }

        for (Turn turn : turns) {
            GameState newState = new GameState(gameLogic.g);
            for (Move move : turn.getMoves()) {
                newState.move(move);
            }

            int boardValue = minimax(newState, maxDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, !isWhite);

            if (isWhite && boardValue > bestValue) {
                bestValue = boardValue;
                bestTurn = turn;
            } else if (!isWhite && boardValue < bestValue) {
                bestValue = boardValue;
                bestTurn = turn;
            }
        }
        return bestTurn;
    }

    private int minimax(GameState gameState, int depth, int alpha, int beta, boolean maxPlayer) {
        if (depth == 0 || gameLogic.isGameOver(gameState)) {
            return (int) GameLogic.evaluateBoard(gameState);
        }

        ArrayList<Turn> legalTurns = GameLogic.getLegalTurns(gameState);

        if (maxPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Turn turn : legalTurns) {
                GameState newState = new GameState(gameState);
                for (Move move : turn.getMoves()) {
                    newState.move(move);
                }

                int eval = minimax(newState, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Turn turn : legalTurns) {
                GameState newState = new GameState(gameState);
                for (Move move : turn.getMoves()) {
                    newState.move(move);
                }

                int eval = minimax(newState, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }
    public Agent reset() {
        return new AlphaBetaAgent(isWhite);
    }

    @Override
    public void simulate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'simulate'");
    }
}
