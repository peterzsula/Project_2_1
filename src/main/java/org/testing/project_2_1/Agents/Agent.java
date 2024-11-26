package org.testing.project_2_1.Agents;

import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.GameLogic.GameState;

/**
 * Interface representing an agent in the game.
 * Agents can be human or AI, and they interact with the game logic
 * to make moves and manage game state.
 */
public interface Agent {
    /**
     * The delay (in seconds) for an agent's move to simulate thinking time.
     */
    public static final double delay = 0.05;

    /**
     * Determines whether the agent is playing as white.
     * @return True if the agent is white, otherwise false.
     */
    public boolean isWhite();

    /**
     * Makes a move during the agent's turn.
     * The implementation defines how the agent selects and executes moves.
     */
    public void makeMove();

    /**
     * Simulates the agent's actions during a game.
     * Used for automated gameplay simulations.
     */
    public void simulate();

    /**
     * Sets the game logic for the agent.
     * This links the agent to the game's rules and interactions.
     * @param gameLogic The game's logic object.
     */
    public void setGameLogic(GameLogic gameLogic);

    /**
     * Pauses or resumes the agent's actions.
     * The implementation determines how the agent handles being paused.
     */
    public void pause();

    /**
     * Resets the agent to its initial state.
     * Creates a new instance of the agent with the same configuration.
     * @return A new Agent instance.
     */
    public Agent reset();

    /**
     * Sets the current game state for the agent.
     * Updates the agent's reference to the active game state.
     * @param gameState The current game state.
     */
    public void setGameState(GameState gameState);
}
