package org.testing.project_2_1;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// TODO: this is just CoPilot code, not the actual implementation
public class AlphaBetaSearch implements Agent {
    private boolean isWhite;
    private GameLogic gameLogic;
    private int maxDepth;
    private int nodesVisited;
    private int nodesEvaluated;
    private int maxPruned;
    private int minPruned;

    public AlphaBetaSearch(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    @Override
    public void makeMove() {
        nodesVisited = 0;
        nodesEvaluated = 0;
        maxPruned = 0;
        minPruned = 0;
        System.out.println("AlphaBeta agent making move");
        Move bestMove = alphaBetaSearch(maxDepth);
        gameLogic.takeTurn(bestMove);
    }

    public Move alphaBetaSearch(int depth) {
        nodesVisited++;
        Double alpha = Double.MIN_VALUE;
        Double beta = Double.MAX_VALUE;
        Move bestMove = null;
        Double bestValue = Double.MIN_VALUE;
        ArrayList<Move> legalMoves = gameLogic.getLegalMoves();
        for (Move move : legalMoves) {
            gameLogic.takeTurn(move);
            Double value = minValue(alpha, beta, depth - 1);
            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
            gameLogic.undoLastMove();
        }
        return bestMove;
    }

    public double maxValue(Double alpha, Double beta, int depth) {
        nodesVisited++;
        if (depth == 0 || gameLogic.isGameOver()) {
            nodesEvaluated++;
            return gameLogic.evaluateBoard();
        }
        Double value = Double.MIN_VALUE;
        ArrayList<Move> legalMoves = gameLogic.getLegalMoves();
        for (Move move : legalMoves) {
            gameLogic.takeTurn(move);
            value = Math.max(value, minValue(alpha, beta, depth - 1));
            gameLogic.undoLastMove();
            if (value >= beta) {
                maxPruned++;
                return value;
            }
            alpha = Math.max(alpha, value);
        }
        return value;
    }

    public double minValue(Double alpha, Double beta, int depth) {
        nodesVisited++;
        if (depth == 0 || gameLogic.isGameOver()) {
            nodesEvaluated++;
            return gameLogic.evaluateBoard();
        }
        double value = Integer.MAX_VALUE;
        ArrayList<Move> legalMoves = gameLogic.getLegalMoves();
        for (Move move : legalMoves) {
            gameLogic.takeTurn(move);
            value = Math.min(value, maxValue(alpha, beta, depth - 1));
            gameLogic.undoLastMove();
            if (value <= alpha) {
                minPruned++;
                return value;
            }
            beta = Math.min(beta, value);
        }
        return value;
    }

    @Override
    public boolean isWhite() {
        return isWhite;
    }
    
    
}
