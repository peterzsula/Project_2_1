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
        int simulations = 100;
        for (int i = 0; i < simulations; i++) {
            while (gameState.getWinner() == 0) {
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
        }
        System.out.println("Simulation results:");
        System.out.println("White wins: " + whiteWins);
        System.out.println("Black wins: " + blackWins);
        System.out.println("Draws: " + draws);
    }

}
