package org.testing.project_2_1;

import org.testing.project_2_1.Agents.*;
import org.testing.project_2_1.GameLogic.GameState;
import org.testing.project_2_1.UI.CheckersApp;

import javafx.application.Application;
import javafx.stage.Stage;

public class Simulation extends Application{
    public static final boolean showGUI = false; 
    public static final int SIMULATIONS = 100; // Number of games to simulate
    static GameState gameState = new GameState(); // Initialize the game state
    static Agent white = new AlphaBetaAgent(true, gameState, 3, new double[]{1, -1, 2, -2, 1, -1, 2, -2}); 
    static Agent black = new AlphaBetaAgent(false, gameState, 3, new double[]{1, -1, 1, -1, 1, -1, 1, -1});
    public static int whiteWins = 0; 
    public static int blackWins = 0; 
    public static int draws = 0;
    public static int simsRan = 0;

    @Override
    public void start(@SuppressWarnings("exports") Stage primaryStage){
        CheckersApp app = new CheckersApp(white, black);
            try {
                app.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
    }

    /**
     * Main method for running the simulation.
     */
    public static void main(String[] args) {
        if (showGUI) {
            launch(args);
        }
        
        long startTime = System.nanoTime(); // Start measuring simulation runtime

        long totalWhiteThinkingTime = 0; // Total thinking time for White
        long totalBlackThinkingTime = 0; // Total thinking time for Black

        //long totalWhiteMemoryUsage = 0; // Total memory usage for White
        //long totalBlackMemoryUsage = 0; // Total memory usage for Black

        //Runtime runtime = Runtime.getRuntime(); // Runtime for memory measurements

        for (int i = 0; i < SIMULATIONS; i++) {
            while (gameState.getWinner() == 0) { // Continue until a winner is determined
                // Measure time and memory for White's move
                //runtime.gc(); // Suggest garbage collection before measurement
                //long whiteStartMemory = runtime.totalMemory() - runtime.freeMemory();
                long whiteStartTime = System.nanoTime();

                white.simulate(); // Simulate White's move

                long whiteEndTime = System.nanoTime();
                //long whiteEndMemory = runtime.totalMemory() - runtime.freeMemory();
                totalWhiteThinkingTime += (whiteEndTime - whiteStartTime);
                //totalWhiteMemoryUsage += (whiteEndMemory - whiteStartMemory);

                if (gameState.getWinner() != 0) break; // Check if the game has ended after White's move

                // Measure time and memory for Black's move
                //runtime.gc();
                //long blackStartMemory = runtime.totalMemory() - runtime.freeMemory();
                long blackStartTime = System.nanoTime();

                black.simulate(); // Simulate Black's move

                long blackEndTime = System.nanoTime();
                //long blackEndMemory = runtime.totalMemory() - runtime.freeMemory();
                totalBlackThinkingTime += (blackEndTime - blackStartTime);
                //totalBlackMemoryUsage += (blackEndMemory - blackStartMemory);
            }

            // Update results based on the game's outcome
            if (gameState.getWinner() == 1) {
                whiteWins++;
            } else if (gameState.getWinner() == -1) {
                blackWins++;
            } else {
                draws++;
            }

            // Write game history to csv
            //CSVHandler.writeCSV(gameState);

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
        //System.out.println("Simulation results:");
        System.out.println();
        System.out.println("White wins: " + whiteWins);
        System.out.println("Black wins: " + blackWins);
        System.out.println("Draws: " + draws);

        System.out.println("White average thinking time: " + (totalWhiteThinkingTime / 1_000_000) / SIMULATIONS + " ms");
        System.out.println("Black average thinking time: " + (totalBlackThinkingTime / 1_000_000) / SIMULATIONS + " ms");

        //System.out.println("White average memory usage: " + (totalWhiteMemoryUsage / 1_024) / SIMULATIONS + " KB");
        //System.out.println("Black average memory usage: " + (totalBlackMemoryUsage / 1_024) / SIMULATIONS + " KB");
        System.out.println("Simulation total runtime: " + (System.nanoTime() - startTime) / 1_000_000_000 + " s");
    }

}
