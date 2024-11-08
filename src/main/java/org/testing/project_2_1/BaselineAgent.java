package org.testing.project_2_1;

import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Random;

import javafx.animation.PauseTransition;

public class BaselineAgent implements Agent {
    private GameLogic gameLogic;
    private boolean isWhite;

    public BaselineAgent(boolean isWhite) {
        this.isWhite = isWhite;
    }
    
    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    @Override
    public boolean isWhite() {
        return isWhite;
    }

    @Override
    public void makeMove() {
        System.out.println("Baseline agent making move");
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(event -> {
        if (gameLogic.isWhiteTurn == isWhite && !gameLogic.isGameOver()) {
            ArrayList<Move> legalMoves = gameLogic.getLegalMoves();
            int randomIndex = new Random().nextInt(legalMoves.size());
            Move move = legalMoves.get(randomIndex);
            gameLogic.takeTurn(move);
            gameLogic.evaluateBoard();
            pause.playFromStart(); // Restart the pause for the next move
        }
    });
    pause.play();
    }
    
}
