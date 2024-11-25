package org.testing.project_2_1.Agents;
import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.GameLogic.GameState;
import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.Turn;

import java.util.List;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class MinimaxAgent implements Agent {
    private GameLogic gameLogic;
    private GameState gameState;
    private boolean isWhite;
    private Turn currentTurn;
    private final int defaultDepth = 3;
    private int maxDepth;

    public MinimaxAgent(boolean isWhite, int maxDepth) {
        this.isWhite=isWhite;
        this.maxDepth=maxDepth;
        currentTurn = new Turn();
    }

    public MinimaxAgent(boolean isWhite, GameState gameState){
        this.isWhite = isWhite;
        this.gameState = gameState;
        currentTurn = new Turn();
        maxDepth = defaultDepth;
    }

    public void setGameLogic(GameLogic gameLogic) { 
        this.gameLogic = gameLogic;
        this.gameState = gameLogic.g;
    }

    @Override
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public boolean isWhite() {
        return isWhite;
    }

    @Override
    public void makeMove() {
        System.out.println("Minimax agent making move");
        PauseTransition pause = new PauseTransition(Duration.seconds(Agent.delay));
        pause.setOnFinished(event -> {
            if (gameLogic.g.getIsWhiteTurn() == isWhite && !gameLogic.isGameOver(gameLogic.g)) {
                List<Turn> turns = gameLogic.g.getLegalTurns();
                if (currentTurn.isEmpty()) {
                    currentTurn = getBestTurn(turns);
                }
                Move move = currentTurn.getMoves().removeFirst();
                System.out.println("Takes turn with move " + move);
                gameLogic.takeMove(move);
            }
        });
        pause.play();
    }

    private Turn getBestTurn(List<Turn> turns) {
        Turn bestTurn = null;
        int bestValue;
        if (isWhite) {
            bestValue = Integer.MIN_VALUE;
        } else {
            bestValue = Integer.MAX_VALUE;
        }

        for (Turn turn : turns) {
            GameState newState = new GameState(gameState);
            for (Move move : turn.getMoves()) {
                newState.move(move);
            }

            int boardValue = minimax(newState, maxDepth, !isWhite);

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

    private int minimax(GameState gameState, int depth, boolean maxPlayer) {
        if (depth == 0 || gameState.isGameOver()) {
            return (int) gameState.evaluateBoard();
        }

        List<Turn> legalTurns = gameState.getLegalTurns();

        if (maxPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Turn turn : legalTurns) {
                GameState newState = new GameState(gameState);
                for (Move move : turn.getMoves()) {
                    newState.move(move);
                }

                int eval = minimax(newState, depth-1, false);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Turn turn : legalTurns) {
                GameState newState = new GameState(gameState);
                for (Move move : turn.getMoves()) {
                    newState.move(move);
                }

                int eval = minimax(newState, depth - 1, true);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }
    public Agent reset() {
        return new MinimaxAgent(isWhite,maxDepth);
    }

    @Override
    public void simulate() {
        List<Turn> legalTurns = gameState.getLegalTurns();
        if (gameState.getWinner() == 0) {
            currentTurn = getBestTurn(legalTurns);
        }
        while (!currentTurn.isEmpty() && gameState.getWinner() == 0) {
            gameState.move(currentTurn.getMoves().removeFirst());
        }
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }
}

