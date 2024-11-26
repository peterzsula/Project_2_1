package org.testing.project_2_1.Agents;

import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.GameLogic.GameState;
import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.Turn;

import java.util.List;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

/**
 * Implements a Minimax-based agent for playing a checkers game.
 * This agent uses the Minimax algorithm with a specified depth to evaluate
 * and determine the optimal moves. It supports both Human vs AI and AI vs AI games.
 */
public class MinimaxAgent implements Agent {
    private GameLogic gameLogic; // Reference to the game logic
    private GameState gameState; // Current game state
    private boolean isWhite; // Indicates if the agent is playing as white
    private Turn currentTurn; // The current turn being executed by the agent
    private final int defaultDepth = 3; // Default search depth for Minimax
    private int maxDepth; // Maximum depth for Minimax search

    /**
     * Constructs a MinimaxAgent with a specified color and depth.
     * @param isWhite Whether the agent plays as white.
     * @param maxDepth The maximum depth for the Minimax algorithm.
     */
    public MinimaxAgent(boolean isWhite, int maxDepth) {
        this.isWhite = isWhite;
        this.maxDepth = maxDepth;
        this.currentTurn = new Turn();
    }

    /**
     * Constructs a MinimaxAgent with a specified color and initial game state.
     * @param isWhite Whether the agent plays as white.
     * @param gameState The initial game state.
     */
    public MinimaxAgent(boolean isWhite, GameState gameState) {
        this.isWhite = isWhite;
        this.gameState = gameState;
        this.currentTurn = new Turn();
        this.maxDepth = defaultDepth;
    }

    /**
     * Constructs a MinimaxAgent with a specified color, initial game state, and depth.
     * @param isWhite Whether the agent plays as white.
     * @param gameState The initial game state.
     * @param maxDepth The maximum depth for the Minimax algorithm.
     */
    public MinimaxAgent(boolean isWhite, GameState gameState, int maxDepth) {
        this.isWhite = isWhite;
        this.gameState = gameState;
        this.currentTurn = new Turn();
        this.maxDepth = maxDepth;
    }

    /**
     * Sets the game logic for the agent.
     * Updates the current game state reference.
     * @param gameLogic The game logic to associate with the agent.
     */
    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        this.gameState = gameLogic.g;
    }

    /**
     * Sets the current game state for the agent.
     * @param gameState The game state to set.
     */
    @Override
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Indicates whether the agent is playing as white.
     * @return True if the agent is white, otherwise false.
     */
    @Override
    public boolean isWhite() {
        return isWhite;
    }

    /**
     * Makes a move by evaluating possible turns and selecting the best move.
     * Uses a pause to simulate the agent's thinking time.
     */
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
                Move move = currentTurn.getMoves().remove(0);
                System.out.println("Takes turn with move " + move);
                gameLogic.takeMove(move);
            }
        });
        pause.play();
    }

    /**
     * Determines the best turn using the Minimax algorithm.
     * Evaluates all legal turns and returns the turn with the best value.
     * @param turns A list of possible turns.
     * @return The turn with the best evaluation score.
     */
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

    /**
     * Implements the Minimax algorithm with depth-limited search.
     * Recursively evaluates game states to determine the optimal score.
     * @param gameState The current game state.
     * @param depth The depth limit for the search.
     * @param maxPlayer Whether the current player is maximizing.
     * @return The evaluation score for the game state.
     */
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

                int eval = minimax(newState, depth - 1, false);
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

    /**
     * Resets the agent by creating a new instance with the same configuration.
     * @return A new instance of the MinimaxAgent.
     */
    @Override
    public Agent reset() {
        return new MinimaxAgent(isWhite, maxDepth);
    }

    /**
     * Simulates the agent's actions by playing the best moves until a winner is determined.
     */
    @Override
    public void simulate() {
        List<Turn> legalTurns = gameState.getLegalTurns();
        if (gameState.getWinner() == 0) {
            currentTurn = getBestTurn(legalTurns);
        }
        while (!currentTurn.isEmpty() && gameState.getWinner() == 0) {
            List<Move> moves = currentTurn.getMoves();
            if (!moves.isEmpty()) {
                gameState.move(moves.remove(0));
            }
        }
    }

    /**
     * Pauses the agent's actions.
     * Currently unimplemented.
     * @throws UnsupportedOperationException If called.
     */
    @Override
    public void pause() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }
}
