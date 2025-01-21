package org.testing.project_2_1;

import org.testing.project_2_1.Agents.AlphaBetaAgent;
import org.testing.project_2_1.GameLogic.GameState;
import org.testing.project_2_1.MachineLearning.SimulatedAnnealing;

import java.util.Arrays;

public class SimulatedAnnealingTest {

    public static void main(String[] args) {
        double initialTemperature = 100.0;
        double coolingRate = 0.01;
        int iterations = 500;

        // Initialize Simulated Annealing
        SimulatedAnnealing sa = new SimulatedAnnealing(initialTemperature, coolingRate, iterations);
        System.out.println("Starting Simulated Annealing...");

        // Run optimization
        double[] optimizedCoefficients = sa.optimize();

        // Test performances
        double[] initialCoefficients = {1, 3, 1};
        double initialPerformance = testAgentPerformance(initialCoefficients, 10);
        double optimizedPerformance = testAgentPerformance(optimizedCoefficients, 10);

        // Output results
        System.out.println("Initial Coefficients: " + Arrays.toString(initialCoefficients));
        System.out.println("Initial Performance: " + initialPerformance);
        System.out.println("Optimized Coefficients: " + Arrays.toString(optimizedCoefficients));
        System.out.println("Optimized Performance: " + optimizedPerformance);


        // Check success
        if (optimizedPerformance > initialPerformance) {
            System.out.println("Simulated Annealing successfully improved performance!");
        } else {
            System.out.println("Simulated Annealing did not improve performance. Review implementation.");
        }
    }

    private static double testAgentPerformance(double[] coefficients, int simulations) {
         // Number of games to test
        int whiteWins = 0;

        for (int i = 0; i < simulations; i++) {
            GameState gameState = new GameState(); // Reset game state for each simulation

            // Create agents with the given coefficients
            AlphaBetaAgent whiteAgent = new AlphaBetaAgent(true, gameState, 3, coefficients);
            AlphaBetaAgent blackAgent = new AlphaBetaAgent(false, gameState, 3, new double[]{1, 3, 1});

            // Simulate a game
            while (gameState.getWinner() == 0) {
                whiteAgent.simulate();
                blackAgent.simulate();
            }

            // Track results
            if (gameState.getWinner() == 1) {
                whiteWins++;
            }
        }

        // Return the win rate of the white agent
        return (double) whiteWins / simulations;
    }
}