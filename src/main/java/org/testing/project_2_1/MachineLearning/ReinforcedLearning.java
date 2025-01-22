package org.testing.project_2_1.MachineLearning;

import java.util.Arrays;
import java.util.List;

import org.testing.project_2_1.Agents.Agent;
import org.testing.project_2_1.Agents.AlphaBetaAgent;
import org.testing.project_2_1.GameLogic.GameState;
import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.Turn;

/**
 * Implements a reinforcement learning framework for training weights
 * used in evaluating game states. The class plays multiple games using
 * Alpha-Beta agents, tracks outcomes, and updates weights through training.
 */
public class ReinforcedLearning {
    private GameState pastGame; // The game state from a completed game.
    private GameState currentGame; // The current game state being trained on.
    private double[] weights; // Weights used to evaluate the board state.
    private List<Turn> turns; // List of turns from the past game.
    private Turn currentTurn; // The current turn being processed during training.

    public static int whiteWins = 0; // Count of white's wins during training.
    public static int blackWins = 0; // Count of black's wins during training.
    public static int draws = 0; // Count of draws during training.
    public static int simsRan = 0; // Count of simulations executed.

    /**
     * Constructs a ReinforcedLearning instance for a single training game.
     * It initializes agents for both players, plays the game, and collects
     * data for training.
     *
     * @param weights The initial weights used for board evaluation.
     */
    public ReinforcedLearning(double[] weights) {
        this.pastGame = new GameState(); // Start a new game state.
        Agent white = new AlphaBetaAgent(true, pastGame, 3, weights); // White agent with depth 3.
        Agent black = new AlphaBetaAgent(false, pastGame, 3); // Black agent with depth 3.

        // Play the game until a winner is determined or a draw occurs.
        while (pastGame.getWinner() == 0) {
            white.simulate(); // White agent simulates its move.
            if (pastGame.getWinner() != 0) break;
            black.simulate(); // Black agent simulates its move.
        }

        // Update win/draw statistics.
        if (pastGame.getWinner() == 1) {
            whiteWins++;
        } else if (pastGame.getWinner() == -1) {
            blackWins++;
        } else {
            draws++;
        }

        // Extract the turns played and initialize for training.
        this.turns = pastGame.getTurnsPlayed();
        this.currentTurn = turns.remove(0);
        this.weights = weights; // Store the initial weights.
        this.currentGame = new GameState(); // Reset the current game state.
    }

    /**
     * Trains the weights using the game data.
     * It evaluates each successive game state, calculates target values,
     * and updates the weights based on the error.
     *
     * @return The updated weights after training.
     */
    public double[] train() {
        for (int i = 0; i < turns.size() - 1; i++) {
            // Find the successor game state based on the current turn.
            GameState successorGameState = findSuccessorGameState(currentGame);

            // Evaluate the target value for the successor state.
            double targetValue = successorGameState.evaluateBoard(weights);

            // Update the weights based on the error and learning rate.
            updateWeights(currentGame, 0.01, targetValue);

            // Move to the next game state.
            currentGame = successorGameState;
        }
        return weights; // Return the updated weights.
    }

    /**
     * Finds the successor game state based on the current turn.
     *
     * @param gameState The current game state.
     * @return The successor game state after applying the moves of the current turn.
     */
    private GameState findSuccessorGameState(GameState gameState) {
        GameState successorGameState = new GameState(gameState);
        for (int i = 0; i < 2; i++) {
            successorGameState.takeTurn(currentTurn);
            currentTurn = turns.removeFirst();
        }
        return successorGameState;
    }

    /**
     * Updates the weights using the LMS (Least Mean Squares) rule.
     * The weights are adjusted based on the error between the predicted
     * and target values.
     *
     * @param gameState   The current game state.
     * @param learningRate The learning rate for weight updates.
     * @param targetValue  The target value for the successor state.
     */
    private void updateWeights(GameState gameState, double learningRate, double targetValue) {
        double predictedValue = gameState.evaluateBoard(weights);
        double error = targetValue - predictedValue;
    
        int[] parameters = gameState.getParameters();
        for (int i = 0; i < weights.length; i++) {
            weights[i] += learningRate * error * gameState.getParameters()[i]; // LMS update rule.
        }
    }

    /**
     * Main method for training the reinforcement learning model.
     * Simulates multiple training games and updates weights iteratively.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        int TRAINING_GAMES = 100; // Number of training games.
        double[] weights = {1, -1, 3, -3, 1, -1, 2, -2}; // Initial weights.

        // Train the model for the specified number of games.
        for (int i = 0; i < TRAINING_GAMES; i++) {
            ReinforcedLearning rl = new ReinforcedLearning(weights); // Play a training game.
            weights = rl.train(); // Update weights after training.

            // Print statistics after each game.
            System.out.println();
            System.out.println("White wins: " + whiteWins);
            System.out.println("Black wins: " + blackWins);
            System.out.println("Draws: " + draws);
            System.out.println(Arrays.toString(weights));
        }
    }
}
