package org.testing.project_2_1.Agents;

import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.Turn;

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
        PauseTransition pause = new PauseTransition(Duration.seconds(Agent.delay));
        pause.setOnFinished(event -> {
        if (gameLogic.g.getIsWhiteTurn() == isWhite && !gameLogic.isGameOver(gameLogic.g)) {
            ArrayList<Turn> legalTurns = GameLogic.getLegalTurns(gameLogic.g);
            System.out.println("Legal moves: " + legalTurns.size());
            int randomIndex = new Random().nextInt(legalTurns.size());
            Turn turn = legalTurns.get(randomIndex);
            Move move = turn.getMoves().removeFirst();
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'simulate'");
    }
    
}
