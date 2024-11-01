package org.testing.project_2_1;

import java.util.ArrayList;

public class BaselineAgent implements Agent {
    private GameLogic gameLogic;
    private boolean isPlayingAsWhite;


    public BaselineAgent(GameLogic gameLogic, boolean isPlayingAsWhite) {
        this.gameLogic = gameLogic;
        this.isPlayingAsWhite = isPlayingAsWhite;
    }

    @Override
    public void makeMove() {
        //gameLogic.takeTurn();
    }
}
