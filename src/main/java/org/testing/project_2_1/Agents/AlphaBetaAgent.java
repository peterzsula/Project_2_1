package org.testing.project_2_1.Agents;
import org.testing.project_2_1.GameLogic.GameLogic;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class AlphaBetaAgent implements Agent { //for now uses minimax algorithm, still not ab-pruning
    private GameLogic gameLogic;
    private boolean isWhite;
    public boolean isWhiteTurn;

    private static final int maxDepth = 3;

    public AlphaBetaAgent(boolean isWhite) {
        this.isWhite=isWhite;
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
        System.out.println("Alpha-Beta agent making move");
        PauseTransition pause = new PauseTransition(Duration.seconds(Agent.delay));
        pause.setOnFinished(event -> {
            while (gameLogic.isWhiteTurn == isWhite && !gameLogic.isGameOver()) {
                Move bestMove = findBestMove();
                System.out.println("Take turn with move " + bestMove);
                gameLogic.takeTurn(bestMove);
                gameLogic.evaluateBoard();
                isWhiteTurn = true;
            }
        });
        pause.play();
    }
    private Move findBestMove() {
        ArrayList<Move> legalMoves = gameLogic.getLegalMoves();
        Move bestMove = null;
        int bestValue;
        if (isWhite) {
            bestValue = Integer.MIN_VALUE;
        } else {
            bestValue = Integer.MAX_VALUE;
        }

        for (Move move : legalMoves) {
            gameLogic.takeTurn(move);
            int boardValue = minimax(maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, !isWhite);

            if (isWhite && boardValue > bestValue) {
                bestValue = boardValue;
                bestMove = move;
            } else if (!isWhite && boardValue < bestValue) {
                bestValue = boardValue;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int minimax(int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0 || gameLogic.isGameOver()) {
            return (int) evaluateBoard();
        }

        ArrayList<Move> legalMoves = gameLogic.getLegalMoves();
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : legalMoves) {
                gameLogic.takeTurn(move);
                int eval = minimax(depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : legalMoves) {
                gameLogic.takeTurn(move);
                int eval = minimax(depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }
    private int evaluateBoard() {
        return (int) gameLogic.evaluateBoard();
    }
    public Agent reset() {
        return new AgentMCTS(isWhite);
    }
}
