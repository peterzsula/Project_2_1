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
        double[] currentSolution = new double[3];
        bestSolution = new double[3];
        double currentPerformance = evaluate(currentSolution);
        performance = currentPerformance;
        System.arraycopy(currentSolution, 0, bestSolution, 0, 10);

        for (int i = 0; i < iterations; i++) {
            double[] newSolution = new double[3];
            System.arraycopy(currentSolution, 0, newSolution, 0, 10);

            int randomIndex = (int) (Math.random() * 10);
            newSolution[randomIndex] += Math.random() * 0.1 - 0.05;

            double newPerformance = evaluate(newSolution);
            double acceptanceProbability = acceptanceProbability(currentPerformance, newPerformance, temperature);

            if (acceptanceProbability > Math.random()) {
                System.arraycopy(newSolution, 0, currentSolution, 0, 10);
                currentPerformance = newPerformance;
            }

            if (currentPerformance > performance) {
                performance = currentPerformance;
                System.arraycopy(currentSolution, 0, bestSolution, 0, 10);
            }

            temperature *= 1 - coolingRate;
        }

        return bestSolution;
    }

    double evaluate(double[] solution) {
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
        return whiteWins/100;
    }

    double acceptanceProbability(double currentPerformance, double newPerformance, double temperature) {
        if (newPerformance > currentPerformance) {
            return 1;
        }
        return Math.exp((newPerformance - currentPerformance) / temperature);
    }
}
