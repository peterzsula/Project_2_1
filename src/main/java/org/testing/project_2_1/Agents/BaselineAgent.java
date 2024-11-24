package org.testing.project_2_1.Agents;

import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.GameLogic.GameState;
import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.Turn;

import javafx.util.Duration;
import java.util.List;
import java.util.Random;

import javafx.animation.PauseTransition;

public class BaselineAgent implements Agent {
    private PauseTransition pause;
    private boolean onPause;
    private GameLogic gameLogic;
    private GameState gameState;
    private boolean isWhite;
    private Turn currentTurn;

    public BaselineAgent(boolean isWhite) {
        this.isWhite = isWhite;
        pause = new PauseTransition(Duration.seconds(Agent.delay));
        onPause = false;
        currentTurn = new Turn();
    }

    public BaselineAgent(boolean isWhite, GameState gameState) {
        this.isWhite = isWhite;
        currentTurn = new Turn();
        this.gameState = gameState;
    }
    
    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public boolean isWhite() {
        return isWhite;
    }

    @Override
    public void makeMove() {
        System.out.println("Baseline agent making move");
        
        pause.setOnFinished(event -> {
        if (gameLogic.g.getIsWhiteTurn() == isWhite && !gameLogic.g.isGameOver()) {
            if (currentTurn.isEmpty()) {
                List<Turn> legalTurns = gameLogic.g.getLegalTurns();
                int randomIndex = new Random().nextInt(legalTurns.size());
                currentTurn = legalTurns.get(randomIndex);
            }
            Move move = currentTurn.getMoves().removeFirst();
            System.out.println("Take turn with move " + move);
            gameLogic.takeMove(move);
            GameLogic.evaluateBoard(gameLogic.g);
        }
    });
    pause.play();
    }

    @Override
    public BaselineAgent reset() {
        return new BaselineAgent(isWhite);
    }

    @Override
    public void simulate() {
        List<Turn> legalTurns = gameState.getLegalTurns();
        if ( gameState.getWinner() == 0) {
            int randomIndex = new Random().nextInt(legalTurns.size());
            currentTurn = legalTurns.get(randomIndex);
        }
        while (!currentTurn.isEmpty() && gameState.getWinner() == 0) {
            gameState.move(currentTurn.getMoves().removeFirst());
        }        
    }

    @Override
    public void pause() {
        if (onPause) {
            pause = new PauseTransition(Duration.seconds(Integer.MAX_VALUE));
            onPause = false;
            System.out.println("resume agent");
        } else {
            pause = new PauseTransition(Duration.seconds(Agent.delay));
            onPause = true;
            System.out.println("pause agent");
        }
    }
    
}
