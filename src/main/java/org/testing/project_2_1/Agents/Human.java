package org.testing.project_2_1.Agents;

import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.GameLogic.GameState;
import org.testing.project_2_1.Moves.Turn;

/**
 * Represents a human player in the game.
 * This class implements the Agent interface to allow integration with the game's logic.
 */
public class Human implements Agent {
    boolean isWhite; // Indicates if the player is playing as white
    GameLogic gameLogic; // Reference to the game logic
    GameState gameState; // Current game state

    /**
     * Constructs a Human player.
     * @param isWhite Specifies if the player is white.
     */
    public Human(boolean isWhite) {
        this.isWhite = isWhite;
    }

    /**
     * Allows the human player to make a move.
     * This method is currently unimplemented, as human players interact via the UI.
     */
    @Override
    public void makeMove() {
        // Human player moves are handled through the game's UI.
    }

    /**
     * Checks if the human player is playing as white.
     * @return True if the player is white, otherwise false.
     */
    @Override
    public boolean isWhite() {
        return isWhite;
    }

    /**
     * Sets the game logic for the human player.
     * Updates the associated game state reference.
     * @param gameLogic The game's logic object.
     */
    @Override
    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        this.gameState = gameLogic.g;
    }

    /**
     * Sets the current game state for the human player.
     * @param gameState The current game state.
     */
    @Override
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Resets the human player by creating a new instance with the same configuration.
     * @return A new Human player instance.
     */
    @Override
    public Agent reset() {
        return new Human(isWhite);
    }

    /**
     * Simulates the player's actions.
     * Currently unimplemented as human actions are not automated.
     */
    @Override
    public void simulate() {
        // Human actions are not automated.
    }

    /**
     * Pauses the player's actions.
     * Currently unimplemented as human actions are controlled via the UI.
     */
    @Override
    public void pause() {
        // Human players do not have automated pause functionality.
    }

    @Override
    public Turn findBetterTurn() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findBetterTurn'");
    }
}
