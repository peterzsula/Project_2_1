package org.testing.project_2_1.Agents;

import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.GameLogic.GameState;

public class Human implements Agent{
    boolean isWhite;
    GameLogic gameLogic;
    GameState gameState;

    public Human(boolean isWhite){
        this.isWhite = isWhite;
    }

    @Override
    public void makeMove() {
        
    }

    @Override
    public boolean isWhite() {
     return isWhite;   
    }

    @Override
    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        this.gameState = gameLogic.g;
    }

    @Override
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public Agent reset() {
        return new Human(isWhite);
    }

    @Override
    public void simulate() {
        
    }

    @Override
    public void pause() {
        
    }

}