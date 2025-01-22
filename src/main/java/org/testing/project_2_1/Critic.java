package org.testing.project_2_1;

import java.util.Arrays;
import java.util.List;

import org.testing.project_2_1.Agents.Agent;
import org.testing.project_2_1.Agents.AlphaBetaAgent;
import org.testing.project_2_1.GameLogic.GameState;
import org.testing.project_2_1.Moves.Turn;

/**
 * Implements a Critic class for training evaluation coefficients of a game-playing agent.
 * The class analyzes game states and uses reinforcement learning techniques to improve performance.
 */
public class Critic {
    private GameState pastGame;      // Stores the past game state.
    private GameState currentGame;   // Current game state being analyzed and trained.
    private double[] weights;        // Coefficients for board evaluation.
    private List<Turn> turns;        // List of turns from the past game.
    private Turn currentTurn;        // The current turn being processed during training.

    public static int whiteWins = 0; // Counter for games won by white.
    public static int blackWins = 0; // Counter for games won by black.
    public static int draws = 0;     // Counter for drawn games.
    public static int simsRan = 0;   // Counter for total simulations run.

    /**
     * Constructs a Critic instance with initial evaluation weights.
     * Simulates a game between two agents, collects game data, and initializes training.
     *
     * @param weights The initial weights for board evaluation.
     */
    public Critic(double[] weights) {
        this.pastGame = new GameState(); // Start a new game state.
        Agent white = new AlphaBetaAgent(true, pastGame, 3); // White agent with depth 3.
        Agent black = new AlphaBetaAgent(false, pastGame, 3); // Black agent with depth 3.

        // Simulate the game until a winner or draw is determined.
        while (pastGame.getWinner() == 0) {
            white.simulate();
            if (pastGame.getWinner() != 0) break;
            black.simulate();
        }

        // Update statistics based on the game's result.
        if (pastGame.getWinner() == 1) {
            whiteWins++;
        } else if (pastGame.getWinner() == -1) {
            blackWins++;
        } else {
            draws++;
        }

        // Initialize training variables.
        this.turns = pastGame.getTurnsPlayed();
        this.currentTurn = turns.remove(0); // Set the first turn for training.
        this.weights = weights;
        this.currentGame = new GameState(); // Reset the current game state.
    }

    /**
     * Skips to a random move in the game history for training purposes.
     *
     * @return The game state after reaching a random move, or null if no moves are left.
     */
    private GameState skipToRandomMove() {
        if (turns.isEmpty()) {
            return null; // No turns left to process.
        }
        while (!currentTurn.isRandomChoice() && !turns.isEmpty()) {
            currentGame.takeTurn(currentTurn); // Apply the current turn to the game state.
            currentTurn = turns.remove(0); // Move to the next turn.
        }
        return currentGame;
    }

    /**
     * Trains the evaluation weights by analyzing game states and their outcomes.
     *
     * @return The updated weights after training.
     */
    private double[] train() {
        GameState nextRandomGameState = skipToRandomMove();
        while (nextRandomGameState != null) {
            // Find a better turn using a deeper search strategy.
            Turn betterTurn = findBetterTurn();

            // Simulate the game state after applying the better turn.
            GameState betterGameState = new GameState(nextRandomGameState);
            betterGameState.takeTurn(betterTurn);

            // Evaluate the target value for the better game state.
            double targetValue = betterGameState.evaluateBoard(weights);

            // Update weights based on the target value and current game state.
            updateWeights(currentGame, 0.01, targetValue);

            // Move to the next random game state.
            nextRandomGameState = skipToRandomMove();
        }
        return weights; // Return the updated weights.
    }

    /**
     * Finds a better turn using an agent with a deeper search strategy.
     *
     * @return The better turn identified by the agent.
     */
    private Turn findBetterTurn() {
        Agent tempAgent = new AlphaBetaAgent(currentGame.isWhiteTurn(), currentGame, 6); // Agent with depth 6.
        currentGame.takeTurn(currentTurn); // Apply the current turn to the game state.
        currentTurn = turns.remove(0); // Move to the next turn.
        return tempAgent.findBetterTurn(); // Return the better turn identified by the agent.
    }

    /**
     * Updates the evaluation weights using the Least Mean Squares (LMS) rule.
     *
     * @param gameState    The current game state being evaluated.
     * @param learningRate The learning rate for weight updates.
     * @param targetValue  The target value for the game state.
     */
    private void updateWeights(GameState gameState, double learningRate, double targetValue) {
        double predictedValue = gameState.evaluateBoard(weights); // Predicted evaluation.
        double error = targetValue - predictedValue; // Calculate the error.

        // Update each weight based on the error and corresponding feature value.
        for (int i = 0; i < weights.length; i++) {
            weights[i] += learningRate * error * gameState.getParameters()[i];
        }
    }

    /**
     * Main method for training evaluation weights using multiple games.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        int TRAINING_GAMES = 100; // Number of games to simulate for training.
        double[] weights = {1, -1, 3, -3, 1, -1, 2, -2}; // Initial weights.

        // Train the Critic using multiple games.
        for (int i = 0; i < TRAINING_GAMES; i++) {
            Critic critic = new Critic(weights); // Simulate a game.
            weights = critic.train(); // Train the model and update weights.

            // Print statistics after each training game.
            System.out.println();
            System.out.println("White wins: " + whiteWins);
            System.out.println("Black wins: " + blackWins);
            System.out.println("Draws: " + draws);
            System.out.println(Arrays.toString(weights));
        }
    }
}
