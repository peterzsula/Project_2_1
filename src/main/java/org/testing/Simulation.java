package org.testing;

import org.testing.project_2_1.Agents.*;
import org.testing.project_2_1.GameLogic.GameState;

public class Simulation {
    public static void main(String[] args) {
        GameState gameState = new GameState();
        Agent white = new BaselineAgent(true, gameState);
        Agent black = new BaselineAgent(false, gameState);
        int whiteWins = 0;
        int blackWins = 0;
        int draws = 0;
<<<<<<< Updated upstream
        int simulations = 100;
        for (int i = 0; i < simulations; i++) {
            while (gameState.getWinner() == 0) {
=======
        final int SIMULATIONS = 10;

        long totalWhiteThinkingTime = 0;
        long totalBlackThinkingTime = 0;

        long totalWhiteMemoryUsage = 0;
        long totalBlackMemoryUsage = 0;

        Runtime runtime = Runtime.getRuntime();

        for (int i = 0; i < SIMULATIONS; i++) {
            while (gameState.getWinner() == 0) {
                // Measure time and memory for White
                runtime.gc();
                long whiteStartMemory = runtime.totalMemory() - runtime.freeMemory();
                long whiteStartTime = System.nanoTime();

>>>>>>> Stashed changes
                white.simulate();
                black.simulate();
            }
            if (gameState.getWinner() == 1) {
                System.out.println("White wins");
                whiteWins++;
            } else if (gameState.getWinner() == -1) {
                System.out.println("Black wins");
                blackWins++;
            } else {
                System.out.println("Draw");
                draws++;
            }
            gameState = new GameState();
<<<<<<< Updated upstream
=======
            white.resetSimulation();
            black.resetSimulation();
            white.setGameState(gameState);
            black.setGameState(gameState);
>>>>>>> Stashed changes
        }
        System.out.println("Simulation results:");
        System.out.println("White wins: " + whiteWins);
        System.out.println("Black wins: " + blackWins);
        System.out.println("Draws: " + draws);
    }

}
