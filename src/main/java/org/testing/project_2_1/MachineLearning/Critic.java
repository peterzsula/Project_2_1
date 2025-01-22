package org.testing.project_2_1.MachineLearning;

import java.util.Arrays;
import java.util.List;

import org.testing.project_2_1.Agents.Agent;
import org.testing.project_2_1.Agents.AlphaBetaAgent;
import org.testing.project_2_1.GameLogic.*;
import org.testing.project_2_1.Moves.*;


public class Critic{
    GameState pastGame;
    GameState currentGame;
    double[] weights;
    List<Turn> turns;
    Turn currentTurn;
    public static int whiteWins = 0; 
    public static int blackWins = 0; 
    public static int draws = 0;
    public static int simsRan = 0;

    public Critic(double[] weights) {
        this.pastGame = new GameState();
        Agent white = new AlphaBetaAgent(true, pastGame, 3);
        Agent black = new AlphaBetaAgent(false, pastGame, 3);
        while (pastGame.getWinner() == 0) {
            white.simulate();
            if (pastGame.getWinner() != 0) break;
            black.simulate();
        }
        if (pastGame.getWinner() == 1) {
            whiteWins++;
        } else if (pastGame.getWinner() == -1) {
            blackWins++;
        } else {
            draws++;
        }
        this.turns = pastGame.getTurnsPlayed();
        this.currentTurn = turns.removeFirst();
        this.weights = weights;
        this.currentGame = new GameState();
    }

    private GameState skipToRandomMove(){
        if (turns.isEmpty()) {
           return null;
        }
        while (currentTurn.isRandomChoice() == false && turns.size() > 0) {
            currentGame.takeTurn(currentTurn);
            currentTurn = turns.removeFirst();
        }
        return currentGame;
    }

    private double[] train() {
        GameState nextRandomGameState = skipToRandomMove();
        while (nextRandomGameState != null) {
            Turn betterTurn = findBetterTurn();
            GameState betterGameState = new GameState(nextRandomGameState);
            betterGameState.takeTurn(betterTurn);
            double targetValue = betterGameState.evaluateBoard(weights);
            updateWeights(currentGame, 0.01, targetValue);
        }
        return weights;
    }

    private Turn findBetterTurn(){
        Agent tempAgent = new AlphaBetaAgent(currentGame.isWhiteTurn(), currentGame, 6);
        currentGame.takeTurn(currentTurn);
        currentTurn = turns.removeFirst();
        return tempAgent.findBetterTurn();
    }

    private void updateWeights(GameState gameState, double learningRate, double targetValue) {
        double predictedValue = gameState.evaluateBoard(weights);
        double error = targetValue - predictedValue;

        for (int i = 0; i < weights.length; i++) {
            weights[i] += learningRate * error * gameState.getParameters()[i]; // LMS update rule
        }
    }

    public static void main(String[] args) {
        int TRAINING_GAMES = 100;
        double[] weights = {1, -1, 3, -3, 1, -1, 2, -2};
        for (int i = 0; i < TRAINING_GAMES; i++) {
            // playing the game
            Critic critic = new Critic(weights);
            weights = critic.train();
            System.out.println();
            System.out.println("White wins: " + whiteWins);
            System.out.println("Black wins: " + blackWins);
            System.out.println("Draws: " + draws);
            System.out.println(Arrays.toString(weights));
        }
    }
}