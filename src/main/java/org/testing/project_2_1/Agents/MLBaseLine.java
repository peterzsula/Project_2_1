package org.testing.project_2_1.Agents;

import java.util.List;

import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.GameLogic.GameState;
import org.testing.project_2_1.Moves.*;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

/**
 * A machine-learning-based agent for a checkers game.
 * Implements an agent that evaluates moves using a heuristic approach
 * combined with Alpha-Beta pruning for optimal move selection.
 */
public class MLBaseLine implements Agent {

    private GameLogic gameLogic; // Reference to the game logic
    private AlphaBetaAgent ABpruning; // Alpha-Beta pruning agent for evaluation
    private Turn currentTurn; // Current turn being processed by the agent
    private boolean isWhite; // Indicates whether the agent is playing as white
    private int maxDepth; // Depth limit for Alpha-Beta pruning

    /**
     * Constructs a new MLBaseLine agent.
     * @param isWhite Specifies if the agent plays as the white player.
     */
    public MLBaseLine(boolean isWhite) {
        this.isWhite = isWhite;
        this.maxDepth = 3; // Default depth for evaluation
        this.ABpruning = new AlphaBetaAgent(isWhite, this.maxDepth);
        currentTurn = new Turn(); // Initialize an empty turn
    }

    /**
     * Sets the game logic reference for the agent.
     * Also sets the game logic for the Alpha-Beta pruning agent if initialized.
     * @param gameLogic The game logic to associate with the agent.
     */
    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        if (this.ABpruning != null) {
            this.ABpruning.setGameLogic(gameLogic);
        }
    }

    /**
     * Sets the game state for the agent.
     * Updates the game state in the Alpha-Beta pruning agent if initialized.
     * @param gameState The current game state to set.
     */
    @Override
    public void setGameState(GameState gameState) {
        this.gameLogic.g = gameState;
        if (this.ABpruning != null) {
            this.ABpruning.setGameState(gameState);
        }
    }

    /**
     * Indicates if the agent is playing as the white player.
     * @return True if the agent is white, otherwise false.
     */
    @Override
    public boolean isWhite() {
        return isWhite;
    }

    /**
     * Makes a move as part of the agent's turn.
     * Evaluates legal turns using a heuristic and Alpha-Beta pruning,
     * and selects the best move based on the evaluation.
     */
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
                System.out.println("Taking turn with evaluation " + currentTurn.getEvaluation() + " " + move.toString());
                gameLogic.takeMove(move);
            }
        });
        pause.play();
    }

    /**
     * Resets the agent to its initial state.
     * @return A new instance of the agent with the same configuration.
     */
    @Override
    public Agent reset() {
        return new MLBaseLine(isWhite);
    }

    /**
     * Retrieves the best turn based on evaluation scores.
     * Maximizes or minimizes the evaluation depending on the agent's color.
     * @param turns A list of evaluated turns.
     * @return The turn with the best evaluation score.
     */
    private Turn getBestTurn(List<Turn> turns) {
        Turn bestTurn = turns.get(0);
        if (isWhite) {
            for (Turn turn : turns) {
                if (turn.getEvaluation() > bestTurn.getEvaluation()) {
                    bestTurn = turn;
                }
            }
        } else {
            for (Turn turn : turns) {
                if (turn.getEvaluation() < bestTurn.getEvaluation()) {
                    bestTurn = turn;
                }
            }
        }
        return bestTurn;
    }

    /**
     * Evaluates a turn using Alpha-Beta pruning to determine its utility.
     * Simulates the turn on a copy of the game state, then evaluates using minimax.
     * @param turn The turn to evaluate.
     * @param originalGS The original game state.
     * @param depth The depth limit for evaluation.
     * @param isMaxPlayerWhite Whether the maximizing player is white.
     * @return The evaluation score of the turn.
     */
    public double evaluateTurn(Turn turn, GameState originalGS, int depth, boolean isMaxPlayerWhite) {
        GameState g0 = new GameState(originalGS);
        for (Move move : turn.getMoves()) {
            g0.move(move);
        }

        // Calls minimax with alpha-beta pruning from AlphaBetaAgent class
        return ABpruning.minimaxPruning(g0, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaxPlayerWhite);
    }

    /**
     * Simulates the agent's actions.
     * Currently unimplemented.
     * @throws UnsupportedOperationException If called.
     */
    @Override
    public void simulate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'simulate'");
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
