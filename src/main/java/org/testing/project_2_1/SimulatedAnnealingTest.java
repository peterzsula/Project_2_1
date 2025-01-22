package org.testing.project_2_1;

import org.testing.project_2_1.Agents.AlphaBetaAgent;
import org.testing.project_2_1.GameLogic.GameState;
import org.testing.project_2_1.MachineLearning.SimulatedAnnealing;

import java.util.Arrays;

/**
 * Tests the performance of Simulated Annealing for optimizing evaluation coefficients
 * used by game-playing agents. This class evaluates the initial and optimized coefficients
 * to verify if performance improvements are achieved.
 */
public class SimulatedAnnealingTest {

    /**
     * Main method to execute the Simulated Annealing test.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        // Initialize Simulated Annealing parameters.
        double initialTemperature = 100.0; // Starting temperature.
        double coolingRate = 0.01;         // Cooling rate for temperature reduction.
        int iterations = 500;             // Number of iterations for optimization.

        // Initialize the Simulated Annealing object.
        SimulatedAnnealing sa = new SimulatedAnnealing(initialTemperature, coolingRate, iterations);
        System.out.println("Starting Simulated Annealing...");

        // Run the optimization process.
        double[] optimizedCoefficients = sa.optimize();

        // Test the performance of the initial and optimized coefficients.
        double[] initialCoefficients = {1, 3, 1}; // Default coefficients.
        double initialPerformance = testAgentPerformance(initialCoefficients, 10); // Test initial performance.
        double optimizedPerformance = testAgentPerformance(optimizedCoefficients, 10); // Test optimized performance.

        // Output results of the optimization.
        System.out.println("Initial Coefficients: " + Arrays.toString(initialCoefficients));
        System.out.println("Initial Performance: " + initialPerformance);
        System.out.println("Optimized Coefficients: " + Arrays.toString(optimizedCoefficients));
        System.out.println("Optimized Performance: " + optimizedPerformance);

        // Check if the optimization improved performance.
        if (optimizedPerformance > initialPerformance) {
            System.out.println("Simulated Annealing successfully improved performance!");
        } else {
            System.out.println("Simulated Annealing did not improve performance. Review implementation.");
        }
    }

    /**
     * Tests the performance of an agent with the given evaluation coefficients.
     *
     * @param coefficients The evaluation coefficients to test.
     * @param simulations  The number of games to simulate.
     * @return The win rate of the agent with the given coefficients.
     */
    private static double testAgentPerformance(double[] coefficients, int simulations) {
        int whiteWins = 0; // Counter for white agent's wins.

        // Simulate the specified number of games.
        for (int i = 0; i < simulations; i++) {
            GameState gameState = new GameState(); // Reset game state for each simulation.

            // Create agents with the given coefficients.
            AlphaBetaAgent whiteAgent = new AlphaBetaAgent(true, gameState, 3, coefficients);
            AlphaBetaAgent blackAgent = new AlphaBetaAgent(false, gameState, 3, new double[]{1, 3, 1}); // Default black agent.

            // Simulate the game until a winner is determined.
            while (gameState.getWinner() == 0) {
                whiteAgent.simulate();
                blackAgent.simulate();
            }

            // Track wins for the white agent.
            if (gameState.getWinner() == 1) {
                whiteWins++;
            }
        }

        // Return the win rate of the white agent.
        return (double) whiteWins / simulations;
    }
}
