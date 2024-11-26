package org.testing.project_2_1;

import org.testing.project_2_1.Agents.*;
import org.testing.project_2_1.GameLogic.GameState;

public class Simulation {

    /**
     * Entry point for the simulation program.
     * Simulates multiple games between two agents (White and Black) using Alpha-Beta pruning at different depths.
     * Tracks wins, draws, and performance metrics such as thinking time and memory usage for each agent.
     * 
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        GameState gameState = new GameState(); // Initialize the game state
        Agent white = new MinimaxAgent(true, gameState, 3); // White agent with depth 3
        Agent black = new MinimaxAgent(false, gameState, 4); // Black agent with depth 6
        int whiteWins = 0; // Counter for white wins
        int blackWins = 0; // Counter for black wins
        int draws = 0; // Counter for draws
        long startTime = System.nanoTime(); // Start measuring simulation runtime
        final int SIMULATIONS = 10; // Number of games to simulate

        long totalWhiteThinkingTime = 0; // Total thinking time for White
        long totalBlackThinkingTime = 0; // Total thinking time for Black

        long totalWhiteMemoryUsage = 0; // Total memory usage for White
        long totalBlackMemoryUsage = 0; // Total memory usage for Black

        Runtime runtime = Runtime.getRuntime(); // Runtime for memory measurements

        for (int i = 0; i < SIMULATIONS; i++) {
            while (gameState.getWinner() == 0) { // Continue until a winner is determined
                // Measure time and memory for White's move
                runtime.gc(); // Suggest garbage collection before measurement
                long whiteStartMemory = runtime.totalMemory() - runtime.freeMemory();
                long whiteStartTime = System.nanoTime();

                white.simulate(); // Simulate White's move

                long whiteEndTime = System.nanoTime();
                long whiteEndMemory = runtime.totalMemory() - runtime.freeMemory();
                totalWhiteThinkingTime += (whiteEndTime - whiteStartTime);
                totalWhiteMemoryUsage += (whiteEndMemory - whiteStartMemory);

                if (gameState.getWinner() != 0) break; // Check if the game has ended after White's move

                // Measure time and memory for Black's move
                runtime.gc();
                long blackStartMemory = runtime.totalMemory() - runtime.freeMemory();
                long blackStartTime = System.nanoTime();

                black.simulate(); // Simulate Black's move

                long blackEndTime = System.nanoTime();
                long blackEndMemory = runtime.totalMemory() - runtime.freeMemory();
                totalBlackThinkingTime += (blackEndTime - blackStartTime);
                totalBlackMemoryUsage += (blackEndMemory - blackStartMemory);
            }

            // Update results based on the game's outcome
            if (gameState.getWinner() == 1) {
                whiteWins++;
            } else if (gameState.getWinner() == -1) {
                blackWins++;
            } else {
                draws++;
            }

            // Print game summary
            System.out.println("Game " + (i + 1) + " finished. White wins: " + whiteWins + ", Black wins: " + blackWins + ", Draws: " + draws);

            // Reset the game state for the next simulation
            gameState = new GameState();
            white.reset();
            black.reset();
            white.setGameState(gameState);
            black.setGameState(gameState);
        }

        // Print simulation results and performance metrics
        System.out.println("Simulation results:");
        System.out.println("White wins: " + whiteWins);
        System.out.println("Black wins: " + blackWins);
        System.out.println("Draws: " + draws);

        System.out.println("White average thinking time: " + (totalWhiteThinkingTime / 1_000_000) / SIMULATIONS + " ms");
        System.out.println("Black average thinking time: " + (totalBlackThinkingTime / 1_000_000) / SIMULATIONS + " ms");

        System.out.println("White average memory usage: " + (totalWhiteMemoryUsage / 1_024) / SIMULATIONS + " KB");
        System.out.println("Black average memory usage: " + (totalBlackMemoryUsage / 1_024) / SIMULATIONS + " KB");
        System.out.println("Simulation total runtime: " + (System.nanoTime() - startTime) / 1_000_000_000 + " s");
    }
}
