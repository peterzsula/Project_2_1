package org.testing.project_2_1.MachineLearning;

import org.testing.project_2_1.Agents.*;
import org.testing.project_2_1.GameLogic.GameState;

public class SimulatedAnnealing {
    double temperature;
    double coolingRate;
    int iterations;
    double[] bestSolution;
    double performance;
    double[] oldCoefficients = {1, 3, 1};

    public SimulatedAnnealing(double temperature, double coolingRate, int iterations) {
        this.temperature = temperature;
        this.coolingRate = coolingRate;
        this.iterations = iterations;
    }

    public double[] optimize() {
        double[] currentSolution = {1, 1, 1}; // Initialize with reasonable default values
        bestSolution = currentSolution.clone();
        double currentPerformance = evaluate(currentSolution, 10); // Start with fewer simulations
        performance = currentPerformance;

        int stagnationCounter = 0; // Tracks iterations with no improvement
        int maxStagnation = 50; // Threshold for restarting

        for (int i = 0; i < iterations; i++) {
            double[] newSolution = currentSolution.clone();
            int randomIndex = (int) (Math.random() * newSolution.length);

            // Adjust the perturbation size dynamically based on temperature
            double perturbation = (Math.random() * 0.1 - 0.05) * (temperature / 100.0);
            newSolution[randomIndex] = Math.max(0.5, Math.min(5.0, newSolution[randomIndex] + perturbation));

            // Evaluate new solution
            double newPerformance = evaluate(newSolution, i > iterations / 2 ? 50 : 10); // More simulations in later iterations
            double acceptanceProbability = acceptanceProbability(currentPerformance, newPerformance, temperature);

            if (acceptanceProbability > Math.random()) {
                currentSolution = newSolution.clone();
                currentPerformance = newPerformance;
            }

            // Update the best solution if performance improves
            if (currentPerformance > performance) {
                performance = currentPerformance;
                bestSolution = currentSolution.clone();
                stagnationCounter = 0; // Reset stagnation counter
            } else {
                stagnationCounter++;
            }
            // Restart with a new random solution if stagnation occurs
            if (stagnationCounter > maxStagnation) {
                currentSolution = new double[]{1, 1, 1};
                currentPerformance = evaluate(currentSolution, 10);
                stagnationCounter = 0;
                System.out.println("Restarting with a new random solution...");
            }

            temperature *= coolingRate; // Gradually cool the temperature
        }

        return bestSolution;
    }


    double evaluate(double[] solution, int numSimulations) {
        GameState gameState = new GameState(); // Initialize the game state
        Agent white = new AlphaBetaAgent(true, gameState, 3, solution);
        Agent black = new AlphaBetaAgent(false, gameState, 3, oldCoefficients);
        int whiteWins = 0;
        int blackWins = 0;
        int draws = 0;
        int simsRan = 0;
        while (simsRan < 100) {
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
            simsRan++;
        }
        return (double) whiteWins / numSimulations; // Return win rate as performance
    }

    double acceptanceProbability(double currentPerformance, double newPerformance, double temperature) {
        if (newPerformance > currentPerformance) {
            return 1.0;
        }
        return Math.exp((newPerformance - currentPerformance) / temperature);
    }
}
