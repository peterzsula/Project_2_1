package org.testing.project_2_1;

import org.testing.project_2_1.Agents.*;
import org.testing.project_2_1.Agents.AlphaBetaAgent;
import org.testing.project_2_1.GameLogic.GameState;

public class Simulation {
    public static void main(String[] args) {
        GameState gameState = new GameState();
        Agent white = new AlphaBetaAgent(true, gameState, 3);
        Agent black = new AlphaBetaAgent(false, gameState, 6);
        int whiteWins = 0;
        int blackWins = 0;
        int draws = 0;
        long startTime = System.nanoTime();
        final int SIMULATIONS = 100;


        long totalWhiteThinkingTime = 0;
        long totalBlackThinkingTime = 0;

        long totalWhiteMemoryUsage = 0;
        long totalBlackMemoryUsage = 0;

        Runtime runtime = Runtime.getRuntime();

        for (int i = 0; i < SIMULATIONS; i++) {
            while (gameState.getWinner() == 0) {
                // Measure time and memory for White
                runtime.gc(); // Suggest garbage collection before measuring
                long whiteStartMemory = runtime.totalMemory() - runtime.freeMemory();
                long whiteStartTime = System.nanoTime();

                white.simulate();

                long whiteEndTime = System.nanoTime();
                long whiteEndMemory = runtime.totalMemory() - runtime.freeMemory();
                totalWhiteThinkingTime += (whiteEndTime - whiteStartTime);
                totalWhiteMemoryUsage += (whiteEndMemory - whiteStartMemory);

                if (gameState.getWinner() != 0) break;

                // Measure time and memory for Black
                runtime.gc();
                long blackStartMemory = runtime.totalMemory() - runtime.freeMemory();
                long blackStartTime = System.nanoTime();

                black.simulate();

                long blackEndTime = System.nanoTime();
                long blackEndMemory = runtime.totalMemory() - runtime.freeMemory();
                totalBlackThinkingTime += (blackEndTime - blackStartTime);
                totalBlackMemoryUsage += (blackEndMemory - blackStartMemory);
            }

            if (gameState.getWinner() == 1) {
                whiteWins++;
            } else if (gameState.getWinner() == -1) {
                blackWins++;
            } else {
                draws++;
            }
            System.out.println("Game " + (i + 1) + " finished. White wins: " + whiteWins + ", Black wins: " + blackWins + ", Draws: " + draws);
            gameState = new GameState();
            white.reset();
            black.reset();
            white.setGameState(gameState);
            black.setGameState(gameState);
        }

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