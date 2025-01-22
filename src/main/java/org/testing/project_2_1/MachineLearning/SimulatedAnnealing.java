package org.testing.project_2_1.MachineLearning;

import org.testing.project_2_1.Agents.*;
import org.testing.project_2_1.GameLogic.GameState;

/**
 * Implements the Simulated Annealing algorithm for optimizing evaluation coefficients.
 * The algorithm adjusts the coefficients to maximize the performance of a game-playing agent.
 */
public class SimulatedAnnealing {
    double temperature;
    double coolingRate;
    int iterations;
    double[] bestSolution;
    double performance;
    double[] oldCoefficients = {1, -1, 3, -3, 1, -1, 3, -3};

    public SimulatedAnnealing(double temperature, double coolingRate, int iterations) {
        this.temperature = temperature;
        this.coolingRate = coolingRate;
        this.iterations = iterations;
    }

    /**
     * Optimizes the evaluation coefficients using Simulated Annealing.
     *
     * @return The best solution (coefficients) found during the optimization process.
     */
    public double[] optimize() {
        double[] currentSolution = {1, -1, 3, -3, 1, -1, 3, -3}; // Initialize with reasonable default values
        bestSolution = currentSolution.clone();
        double currentPerformance = evaluate(currentSolution, 10); // Start with fewer simulations
        performance = currentPerformance;

        int stagnationCounter = 0; // Tracks consecutive iterations without improvement.
        int maxStagnation = 50;    // Maximum allowed stagnation before restarting.

        for (int i = 0; i < iterations; i++) {
            double[] newSolution = currentSolution.clone();
            int randomIndex = (int) (Math.random() * newSolution.length);

            // Dynamically adjust the perturbation size based on temperature.
            double perturbation = (Math.random() * 0.1 - 0.05) * (temperature / 100.0);
            newSolution[randomIndex] = Math.max(0.5, Math.min(5.0, newSolution[randomIndex] + perturbation));

            // Evaluate the new solution with more simulations in later iterations.
            double newPerformance = evaluate(newSolution, i > iterations / 2 ? 50 : 10);
            double acceptanceProbability = acceptanceProbability(currentPerformance, newPerformance, temperature);

            // Accept the new solution based on acceptance probability.
            if (acceptanceProbability > Math.random()) {
                currentSolution = newSolution.clone();
                currentPerformance = newPerformance;
            }

            // Update the best solution if performance improves.
            if (currentPerformance > performance) {
                performance = currentPerformance;
                bestSolution = currentSolution.clone();
                stagnationCounter = 0; // Reset stagnation counter.
            } else {
                stagnationCounter++;
            }

            // Restart with a new random solution if stagnation occurs.
            if (stagnationCounter > maxStagnation) {
                currentSolution = new double[]{1, 1, 1}; // Restart with default coefficients.
                currentPerformance = evaluate(currentSolution, 10);
                stagnationCounter = 0;
                System.out.println("Restarting with a new random solution...");
            }

            temperature *= coolingRate; // Gradually reduce the temperature.
        }

        return bestSolution; // Return the best solution found.
    }

    /**
     * Evaluates the performance of a given solution (coefficients).
     *
     * @param solution       The evaluation coefficients to test.
     * @param numSimulations The number of simulations to run.
     * @return The performance score (win rate) of the solution.
     */
    double evaluate(double[] solution, int numSimulations) {
        GameState gameState = new GameState(); // Initialize the game state.
        Agent white = new AlphaBetaAgent(true, gameState, 3, solution); // White agent with solution coefficients.
        Agent black = new AlphaBetaAgent(false, gameState, 3, oldCoefficients); // Black agent with reference coefficients.

        int whiteWins = 0;
        int blackWins = 0;
        int draws = 0;

        for (int simsRan = 0; simsRan < numSimulations; simsRan++) {
            while (gameState.getWinner() == 0) {
                white.simulate();
                black.simulate();
            }

            if (gameState.getWinner() == 1) {
                whiteWins++;
            } else if (gameState.getWinner() == -1) {
                blackWins++;
            } else {
                draws++;
            }
        }

        return (double) whiteWins / numSimulations; // Return the win rate of the solution.
    }

    /**
     * Calculates the acceptance probability for a new solution.
     * Higher temperature and better performance increase the probability of acceptance.
     *
     * @param currentPerformance The current performance score.
     * @param newPerformance     The performance score of the new solution.
     * @param temperature        The current temperature.
     * @return The probability of accepting the new solution.
     */
    double acceptanceProbability(double currentPerformance, double newPerformance, double temperature) {
        if (newPerformance > currentPerformance) {
            return 1.0; // Always accept if the new performance is better.
        }
        return Math.exp((newPerformance - currentPerformance) / temperature); // Probability based on temperature.
    }
}
