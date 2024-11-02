package org.testing.project_2_1;

import java.util.ArrayList;
import java.util.Random;

public class BaselineAgent implements Agent {
    private GameLogic gameLogic;

    public BaselineAgent() {
    }
    
    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    @Override
    public void makeMove() {
        System.out.println("Baseline agent making move");
        Random random = new Random();
        Move move = null;
        while (gameLogic.isWhiteTurn == gameLogic.isAgentWhite) {
            if (gameLogic.hasAvailableCaptures()) {
                ArrayList<Capture> availableCaptures = gameLogic.checkAvailableCaptures();
                int randomIndex = random.nextInt(availableCaptures.size());
                move = availableCaptures.get(randomIndex);
            }
            else {
                ArrayList<Move> availableMoves = getAvailableMoves();
                int randomIndex = random.nextInt(availableMoves.size());
                move = availableMoves.get(randomIndex);
            }
            gameLogic.takeTurn(move);
        }
    }

    private ArrayList<Move> getAvailableMoves() {
        ArrayList<Move> availableMoves = new ArrayList<>();
        ArrayList<Piece> pieces = gameLogic.getListOfPieces();
        for (Piece piece : pieces) {
            for (int row = 0; row < gameLogic.board.length; row++) {
                int startCol = (row % 2 == 0) ? 1 : 0;
                for (int col = startCol; col < gameLogic.board.length; col += 2){
                    Move move = gameLogic.determineMoveType(piece, row, col);
                    if (move.getType() == MoveType.CAPTURE || move.getType() == MoveType.NORMAL) {
                        availableMoves.add(move);
                    }
                }
            }
        }
        return availableMoves;
    }
}
