package org.testing.project_2_1.Agents;

import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.GameLogic.GameState;
import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.Turn;

import javafx.util.Duration;
import java.util.List;
import java.util.Random;

import javafx.animation.PauseTransition;

/**
 * Represents a baseline agent for the game.
 * This agent plays moves randomly from the set of legal moves
 * and can simulate moves or pause/resume its actions.
 */
public class BaselineAgent implements Agent {
    private PauseTransition pause; // Handles delay between agent moves
    private boolean onPause; // Tracks whether the agent is paused
    private GameLogic gameLogic; // Reference to the game logic
    private GameState gameState; // Current game state
    private boolean isWhite; // Indicates if the agent is playing as white
    private Turn currentTurn; // The current turn being processed by the agent

    /**
     * Constructs a baseline agent with a specified color.
     * @param isWhite Whether the agent plays as white.
     */
    public BaselineAgent(boolean isWhite) {
        this.isWhite = isWhite;
        pause = new PauseTransition(Duration.seconds(Agent.delay));
        onPause = false;
        currentTurn = new Turn();
    }

    /**
     * Constructs a baseline agent with a specified color and initial game state.
     * @param isWhite Whether the agent plays as white.
     * @param gameState The initial game state.
     */
    public BaselineAgent(boolean isWhite, GameState gameState) {
        this.isWhite = isWhite;
        currentTurn = new Turn();
        this.gameState = gameState;
    }

    /**
     * Sets the game logic for the agent.
     * @param gameLogic The game's logic object.
     */
    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    /**
     * Sets the current game state for the agent.
     * @param gameState The current game state.
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Checks if the agent is playing as white.
     * @return True if the agent is white, otherwise false.
     */
    @Override
    public boolean isWhite() {
        return isWhite;
    }

    /**
     * Makes a move by selecting a random legal turn and executing the moves.
     * Uses a delay to simulate thinking time.
     */
    @Override
    public void makeMove() {
        System.out.println("Baseline agent making move");

        pause.setOnFinished(event -> {
            if (gameLogic.g.getIsWhiteTurn() == isWhite && !gameLogic.g.isGameOver()) {
                if (currentTurn.isEmpty()) {
                    List<Turn> legalTurns = gameLogic.g.getLegalTurns();
                    int randomIndex = new Random().nextInt(legalTurns.size());
                    currentTurn = legalTurns.get(randomIndex);
                }
                Move move = currentTurn.getMoves().remove(0);
                System.out.println("Take turn with move " + move);
                gameLogic.takeMove(move);
                gameLogic.g.evaluateBoard();
            }
        });
        pause.play();
    }

    /**
     * Resets the agent by creating a new instance with the same configuration.
     * @return A new instance of the BaselineAgent.
     */
    @Override
    public BaselineAgent reset() {
        return new BaselineAgent(isWhite);
    }

    /**
     * Simulates the agent's actions by playing random moves until the game ends or the turn is empty.
     */
    @Override
    public void simulate() {
        List<Turn> legalTurns = gameState.getLegalTurns();
        if (gameState.getWinner() == 0) {
            int randomIndex = new Random().nextInt(legalTurns.size());
            currentTurn = legalTurns.get(randomIndex);
        }
        while (!currentTurn.isEmpty() && gameState.getWinner() == 0) {
            List<Move> moves = currentTurn.getMoves();
            if (!moves.isEmpty()) {
                gameState.move(moves.remove(0));
            }
        }
    }

    /**
     * Pauses or resumes the agent's actions.
     * Toggles between pausing and resuming based on the current state.
     */
    @Override
    public void pause() {
        if (onPause) {
            pause = new PauseTransition(Duration.seconds(Integer.MAX_VALUE));
            onPause = false;
            System.out.println("resume agent");
        } else {
            pause = new PauseTransition(Duration.seconds(Agent.delay));
            onPause = true;
            System.out.println("pause agent");
        }
    }
}
